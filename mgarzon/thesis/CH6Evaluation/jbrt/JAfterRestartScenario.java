package org.jbrt.client;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.jbrt.client.annotations.Parameter;
import org.jbrt.client.annotations.Type;
import org.jbrt.client.config.Helper;
import org.jbrt.client.config.JConfigAtom;
import org.jbrt.client.config.JConfiguration;
import org.jbrt.client.handler.JPostingHandler;
import org.jbrt.client.net.JFileCashedSender;
import org.jbrt.client.net.impl.JFileCashedSenderImpl;
import org.jbrt.client.store.JHashMapStore;
import org.jbrt.client.store.JHashSetStore;

/**
 * Philosophy:<br>
 * Throwables are cached, stored and after program restart they are processed
 * and sent to dedicated server. Forasmuch as storing to disc takes some
 * resources, throwables are stored batch-oriented. After storing throwables
 * database (TD) is cleared. For this purpose there exists one more database
 * that stores only throwable hash codes (HD).  HD should reflect actual
 * state of throwables in memory or stored on disc. During committing,
 * throwable hash code is compared with HD. If HD contains hash, throwable
 * will be processed, other way it will be ignored.
 * <br>
 * This scenario can be divided into parts:<br>
 * <ul>
 * <li>Initialization:<br>
 * HD and TD will be loaded from storage. Method post is called in a new
 * thread. Method post preprocess database. It iteratively goes through
 * JConfigurationAtoms and opens a connection to dedicated server.
 * IF it is success, method will create message (XML) and send it in
 * multipart post. Then it waits for response. After it receives response,
 * method will create JResponse objects. Responded throwables are removed
 * from HD and TH. HD and TH are stored (not synchronously).
 * According to response throwables and hashes are removed from databases
 * and response listeners are called.</li>
 * <li>Running:<br>
 * Exceptions are committed to scenario through commit method.
 * They are directly stored to memory if throwable hash code is not in HD.
 * If throwables count in memory is bigger the bufferSize (parameter gained
 * from JBrt), HD and TH are stored synchronously. </li>
 * <li>Ending:<br>
 * HD and TD are stored synchronously.</li>
 * </ul>
 * 
 * @author Cipov Peter
 * @version 1.0
 */
public class JAfterRestartScenario implements JScenario {

    private JPostingHandler postingHandler;
    private File workingDirectory;
    private List<JAppearanceListener> appListeners;
    private Integer bufferSize;
    private List<JResponseListener> resListeners;
    private JConfiguration configuration;
    /** Hash output file name. */
    private static final String HASHES_OUTPUT_FILE = "JAfterRestartScenario_hashes.ser";
    /** Database output file name. */
    private static final String DATABASE_OUTPUT_FILE = "JAfterRestartScenario_database.ser";
    /** Hashes store field - it takes less memory, then all
     * exceptions. New exceptions are compared according this field. */
    private JHashSetStore<String> hashSet;
    /** Exceptions are stored here.*/
    private JHashMapStore<JThrowable> database;
    /** flag that handles information wether database was already synchronized. */
    private boolean isSynchronized = false;
    /**object used for internal synchrnization*/
    private final Object synchObj = new Object();

    @Parameter(type = Type.APPERANCE_LISTENER)
    public void setAppListeners(List<JAppearanceListener> appListeners) {
        this.appListeners = appListeners;
    }

    @Parameter(type = Type.BUFFER_SIZE)
    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Parameter(type = Type.CONFIGURATION)
    public void setConfiguration(JConfiguration configuration) {
        this.configuration = configuration;
    }

    @Parameter(type = Type.POSTING_HANDLER)
    public void setPostingHandler(JPostingHandler postingHandler) {
        this.postingHandler = postingHandler;
    }

    @Parameter(type = Type.RESPONSE_LISTENER)
    public void setResListeners(List<JResponseListener> resListeners) {
        this.resListeners = resListeners;
    }

    @Parameter(type = Type.WORKING_DIR)
    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void initialize() {
        initializeHashes();
        initializeDatabase();
        if (!database.getList().isEmpty()) {
            callPostingHandler();
        }
    }

    /**
     * According to this scenario, this method will call
     * posting rutine in new thread.
     */
    private void callPostingHandler() {
        try {
            postingHandler.onPosting();
        } catch (Throwable th) {
            JBrt.commitInternal(th);
        }
    }

    /**
     * Method will initialize and set hash file field.
     * @throws IllegalStateException if can not write to file
     */
    private void initializeHashes() {
        File hashFile = new File(workingDirectory, HASHES_OUTPUT_FILE);
        if (!hashFile.exists()) {
            try {
                hashFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        if (!hashFile.canWrite()) {
            throw new IllegalStateException("JBrt can not write to" + hashFile.getAbsolutePath());
        }
        hashSet = new JHashSetStore<String>(hashFile);
        hashSet.synchronizeWithMemory();
    }

    /**
     * Method will initialize database for error storing.
     * @throws IllegalStateException If can not write to file
     */
    private void initializeDatabase()  {
        File databaseFile = new File(workingDirectory, DATABASE_OUTPUT_FILE);
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        if (!databaseFile.canWrite()) {
            throw new IllegalStateException("JBrt can not write to " + databaseFile.getAbsolutePath());
        }
        database = new JHashMapStore<JThrowable>(databaseFile);
        database.synchronizeWithMemory();
        this.isSynchronized = true;
    }

    @Override
    public boolean commit(JThrowable exception) {
        synchronized (synchObj) {
            String hash = exception.getHashCode();
            if (!this.hashSet.contains(hash)) {
                this.database.add(hash, exception);
                this.hashSet.add(hash);
                try {
                    synchronizedStore();
                } catch (Exception ex) {
                    JBrt.commitInternal(ex);
                    return false;
                }
            }
            try {
                for (JAppearanceListener listener : this.appListeners) {
                    listener.performAction(exception);
                }
            } catch (Throwable th) {
                JBrt.commitInternal(th);
            }
            return true;
        }
    }

    private void synchronizedStore() throws IOException {
        if (this.database.size() > this.bufferSize) {
            this.database.synchronizeWithStore();
            this.database.clean();
            this.isSynchronized = false;
            this.hashSet.synchronizeWithStore();
        }
    }

    @Override
    public void delete() {
        File databaseFile = new File(workingDirectory, DATABASE_OUTPUT_FILE);
        File hashFile = new File(workingDirectory, HASHES_OUTPUT_FILE);

        if (databaseFile.exists()) {
            if (!databaseFile.delete()) {
                JBrt.commitInternal(databaseFile.getAbsolutePath() + "was not removed");
            }
        }
        if (hashFile.exists()) {
            if (!hashFile.delete()) {
                JBrt.commitInternal(hashFile.getAbsolutePath() + " was not removed");
            }
        }
    }

    @Override
    public void end() {
        synchronized (synchObj) {
            try {
                database.synchronizeWithStore();
                hashSet.save();
            } catch (Exception ex) {
                JBrt.commitInternal(ex);
                //in this section system is already ending, and all exceptions
                //that I throw here will be ignored.
                //data have to be consistent, therefore, I try to delete storage
                //and start again.
                this.delete();
            }
        }
    }

    @Override
    public void post() {
        synchronized (synchObj) {
            if (!this.isSynchronized) {
                database.synchronizeWithMemory();
                this.isSynchronized = true;
            }
        }

        HashMap<JConfigAtom, HashSet<JThrowable>> preproces = preproces();
        if (preproces == null) {
            return;
        }
        JFileCashedSender send = new JFileCashedSenderImpl();
        Long size = (Long) configuration.getOption(Helper.OPTION_BRT_NET_SENDER_CASHE_SIZE);
        if(size != null) {
            send.setCasheSize(size);
        }
        send.setRootDir(new File(workingDirectory, "sendcashe"));


        Set<Entry<JConfigAtom, HashSet<JThrowable>>> set = preproces.entrySet();
        JResponseBean bean = new JResponseBean();
        List<JThrowable> temp = new LinkedList<JThrowable>();
        for (Entry<JConfigAtom, HashSet<JThrowable>> entry : set) {
            try {
                temp.clear();
                temp.addAll(entry.getValue());
                bean.merge(send.post(temp, entry.getKey()));

            } catch (Throwable t) {
                JBrt.commitInternal(t);
            }
            
        }
        try {            
            database.clean();
            database.save();
            hashSet.clear();
            hashSet.save();
        } catch (Exception ex) {
            JBrt.commitInternal(ex);
        }
        if (!bean.getResponses().isEmpty()) {
            for (JResponseListener listener : this.resListeners) {
                listener.performAction(bean);
            }
        }

    }

    private HashMap<JConfigAtom, HashSet<JThrowable>> preproces() {
        HashMap<JConfigAtom, HashSet<JThrowable>> toSend = new HashMap<JConfigAtom, HashSet<JThrowable>>();

        List<JThrowable> exceptions = database.getList();
        StackTraceElement[] stackTrace;
        boolean added = false;
        String className;
        for (JThrowable t : exceptions) {
            stackTrace = t.getThrowable().getStackTrace();
            if (stackTrace == null) {
                continue;
            }
            /**
             * StackTrace[] is ordered accoring to cause.
             * I will iterate from the other side, because
             * It will include the most common exception name.
             * (Because at top can be classes from java.* , as arraylist ....)
             */
            added = false;
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                className = stackTrace[i].getClassName();
                Collection<JConfigAtom> atoms = this.configuration.find(className);
                if (atoms.isEmpty()) {
                    // any
                    continue;
                }

                //add to hashMap
                for (JConfigAtom atom : atoms) {
                    if (toSend.containsKey(atom)) {
                        toSend.get(atom).add(t);
                    } else {
                        HashSet<JThrowable> throwables = new HashSet<JThrowable>();
                        throwables.add(t);
                        toSend.put(atom, throwables);
                    }
                }
                added = true;
                break; //findonly one

            }

            if (!added) {
                Object option = this.configuration.getOption(Helper.OPTION_BRT_NOT_MAPPED_THROWABLE);
                if (option != null) {

                    if (toSend.containsKey(option)) {
                        toSend.get(option).add(t);
                    } else {
                        HashSet<JThrowable> throwables = new HashSet<JThrowable>();
                        throwables.add(t);
                        toSend.put((JConfigAtom) option, throwables);
                    }
                }
            }
        }

        return toSend;
    }

    @Override
    public List<JThrowable> getThrowablesInMemory() {
        return database.getList();
    }
}
