package org.jbrt.client;

import org.jbrt.client.handler.JPostingHandler;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import java.util.List;
import org.jbrt.client.annotations.JParameterInjection;
import org.jbrt.client.annotations.Type;
import org.jbrt.client.config.JConfiguration;
import org.jbrt.client.exception.JBrtException;
import org.jbrt.client.handler.JErrorHandler;
import org.jbrt.client.handler.JInternalErrorHandler;

/**
 * 
 * <p>
 *     JBrt is system that unites exception catching, local exception storing 
 *     and sending to dedicated server. 
 *     System tries to be independent and symbiotic part of  system.
 *     <b>Non-invasive</b> philosophy was considered as the main demand during
 *     developing. Non-invasive means that any big source code change is required. 
 *     The only change that you need to do is add a couple lines of code at the
 *     beginning of your source code.
 *    </p>
 * 
 * <p> System is divided to 3 main parts: </p>
 * <ol>
 *     <li>Exception handling</li>
 *     <li>Exception processing &amp; storing</li>
 *     <li>Exception sending and response handling</li>
 * </ol>
 * <p>Exception handling is solved with help of <i>commit</i>functions.
 * You can use them directly or you can use handlers from <i>org.jbrt.client.handler</i> package:</p>
 * <ul>
 *     <li><i>JUncaughtExceptionHandler</i>
 *         <span> it will catch all uncatched exceptions.</span>
 *     </li>
 *     <li>
 *         <i>JLoggerHandler </i>
 *         <span>it will catch exception from <i>java.util.logging.*</i></span>
 *     </li>
 *   </ul>
 * <p>If it is not enough, you can create custom handler by implementing
 * <i>JErrorHandler</i>. These handlers need to be registered with
 * <i>addErrorHandler</i> method.</p>
 *
 * <p>If you need to be in the know of handled exceptions, you can register
 * listener that will implement <i>JApperanceListener</i>, with <i>addApperanceListner</i>
 * method. Listener is called back after every commit (but this depends of used scenario).</p>
 *
 * <p>Exception processing is managed with help of scenarios. Scenario is part
 * of JBrt system that is responsible for almost all business logic.
 * You can choose from two included scenarios:</p>
 * <ul>
 *    <li>
 *        <i>after restart</i>
 *        <span> exceptions are stored and sent after new start of application </span>
 *    </li>
 * </ul>
 * <p>Default scenario is <i>after restart</i> scenario.</p>
 * <p>Exception storing and sending are part of scenario. Therefore JBrt do not care about
 * this part</p>
 *
 * <p>Code example:</p>
 * <pre>
 * final JBrt brt = JBrtFactory.jbrtInstance();
 * try {
 *    brt.addAtomConfiguration(Main.class.getResource("/test/config.xml"));
 * } catch (Exception ex) {
 *     ex.printStackTrace();
 * }
 * 
 * brt.setBufferSize(99);
 * brt.addErrorHandler(new JUncaughtExceptionHandler(false));
 * brt.addErrorHandler(new JLoggerHandler());
 * brt.addResponseListener(new JResponseListener() {
 * 
 *     public void performAction(List<JResponse> responses) {
 *         System.out.println(responses.toString());
 *     }
 * });
 * 
 * try {
 *     brt.start();
 * } catch (JBrtException e) {
 *     e.printStackTrace();
 * }
 * </pre>
 *
 * @author Cipov Peter
 * @version 1.0
 */
public final class JBrt {

    /**default exception buffer size.*/
    private static final Integer DEFAULT_BUFFER_SIZE = 25;
    /** jbrt was not started.*/
    public static final int STATE_NOT_STARTED = 1;
    /** jbrt was started.*/
    public static final int STATE_STARTED = 2;
    /**jbrt was removed.*/
    public static final int STATE_REMOVED = 3;
    /**jbrt configuration object.*/
    private transient final JConfiguration configuration;
    /** throwable memory buffer size.*/
    private Integer bufferSize;
    /** file directory, where jbrt can store.*/
    private File workingDirectory;
    /** listeners for exception apperance. */
    private final List<JAppearanceListener> appListeners;
    /** response listeners.*/
    private final List<JResponseListener> resListeners;
    /** errors handlers list.*/
    private final List<JErrorHandler> errorHandlers;
    /** handler for onpost reaction. */
    private JPostingHandler postingHandler;
    /** scenario reference. */
    private JScenario scenario;
    /** state flag.*/
    private int state;
    /** shutdown hook reference.*/
    private Thread shutdownHook;
    /**Internal error handler*/
    private JInternalErrorHandler internalErrorHandler;

    /**
     * Create instance, should be created only with factory.
     */
    JBrt() {
        this.configuration = new JConfiguration();
        this.bufferSize = Integer.valueOf(DEFAULT_BUFFER_SIZE);
        this.workingDirectory = new File("jbrtwork");
        this.appListeners = new LinkedList<JAppearanceListener>();
        this.resListeners = new LinkedList<JResponseListener>();
        this.errorHandlers = new LinkedList<JErrorHandler>();
        this.state = STATE_NOT_STARTED;
        this.shutdownHook = null;
    }

    /**
     * Shows actual jbrt state.
     * @return jbrt state
     */
    public int getState() {
        return this.state;
    }

    /**
     * Internal chanel for internal exceptions. These exceptions will not be
     * sent to server.
     * @param t
     */
    public static void commitInternal(Throwable t) {
        commitInternal(t, null);
    }

    public static void commitInternal(Throwable t, String message) {
        if (JBrtFactory.instance().internalErrorHandler != null) {
            JBrtFactory.instance().internalErrorHandler.handle(t, message);
        }
    }

    public static void commitInternal(String message) {
        commitInternal(null, message);
    }

    /**
     * Method will invode staring rutine: scanario initialozation,
     * error handlers loading, shut down hook initialization.
     * Can be called only when jbrt is NOT running
     */
    public void start() {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                //method has started once before
                throw new IllegalStateException("JBrt has already started.");
            }
            if (!workingDirectory.exists()) {
                workingDirectory.mkdirs();
            }
            initializeScenario();
            loadErrorHandlers();
            initializeShutDownHook();
            this.state = STATE_STARTED;
        }
    }

    private void initializeScenario() {
        //default scenario
        if (this.scenario == null) {
            this.scenario = new JAfterRestartScenario();
        }

        try {

            //injection
            JParameterInjection injection = new JParameterInjection(scenario);
            injection.add(Type.APPERANCE_LISTENER, this.appListeners);
            injection.add(Type.BUFFER_SIZE, this.bufferSize);
            injection.add(Type.CONFIGURATION, this.configuration);
            injection.add(Type.POSTING_HANDLER, this.postingHandler);
            injection.add(Type.RESPONSE_LISTENER, this.resListeners);
            injection.add(Type.WORKING_DIR, this.workingDirectory);
            injection.inject();

            scenario.initialize();
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void loadErrorHandlers() {
        Iterator<JErrorHandler> iterator = this.errorHandlers.iterator();
        while (iterator.hasNext()) {
            iterator.next().initialize();
        }
    }

    private void removeErrorHandlers() {
        for (JErrorHandler handler : this.errorHandlers) {
            handler.remove();
        }
    }

    private void removeShutDownHook() {
        if (this.shutdownHook == null) {
            return;
        }
        Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
        this.shutdownHook = null;
    }

    private void initializeShutDownHook() {
        this.shutdownHook = new Thread(new Runnable() {

            @Override
            public void run() {
                //disconnect from error sources
                try {
                    removeErrorHandlers();
                } catch (Throwable ex) {
                    commitInternal(ex);
                }
                //scenario ending
                scenario.end();
            }
        });
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);

    }

    /**
     * Throwable can be commited for processing. This method
     * passes it direcly to scenario.
     * Can be called only when jbrt is running
     * @param throwable Throwable reference
     * @param message message if you need it
     * @return true if ok, else false
     */
    public static boolean commit(Throwable throwable, String message) {
        if (JBrtFactory.instance() != null) {
            return JBrtFactory.instance().commitThrowable(new JThrowable(throwable, message));
        }
        return false;
    }

    /**
     * Throwable can be commited for processing. This method
     * passes it direcly to scenario.
     * Can be called only when jbrt is running
     * @param throwable Throwable reference
     * @return true if ok, else false
     */
    public static boolean commit(Throwable throwable) {
        if (JBrtFactory.instance() != null) {
            return JBrtFactory.instance().commitThrowable(new JThrowable(throwable));
        }
        return false;
    }


    /**
     * Throwable can be commited for processing. This method
     * passes it direcly to scenario.
     * Can be called only when jbrt is running
     * @param exception reference
     * @return true if ok, else false
     */
    public static boolean commit(JThrowable exception) {
        if (JBrtFactory.instance() != null) {
            return JBrtFactory.instance().commitThrowable(exception);
        }
        return false;
    }

    /**
     * Throwable can be commited for processing. This method
     * passes it direcly to scenario. 
     * Can be called only when jbrt is running
     * @param exception reference
     * @return true if ok, else false
     */
    private boolean commitThrowable(JThrowable exception) {
        synchronized (this) {
            if (this.state != STATE_STARTED) {
                return false;
            }
            try {
                scenario.commit(exception);
                return true;
            } catch (Throwable ex) {
                commitInternal(ex);
                return false;
            }
        }
    }

    /**
     * Method invokes direcly posting routine. (Scenario could call it in
     * separate thread for better performance ...).
     * @throws org.jbrt.client.exception.JBrtRunException
     */
    public void postExceptions() throws JBrtException {
        try {
            scenario.post();
        } catch (Throwable t) {
            throw new JBrtException(t);
        }
    }

    /**
     * You can set scenario
     * Can be called only when jbrt is NOT running
     * @param scenario
     * @return true if OK
     */
    public boolean setScenario(JScenario scenario) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            this.scenario = scenario;
            return true;
        }
    }

    /**
     * Posting handler is a state before sending exceptions to
     * to server. You can invoke some dialog, that can approve
     * this sending
     * Can be called only when jbrt is NOT running
     * @param postingHandler reference to object
     * @return true if OK
     */
    public boolean setPostingHandler(JPostingHandler postingHandler) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            this.postingHandler = postingHandler;
            return true;
        }
    }

    /**
     * You can add configuration form xml.
     * You can not call this method when jbrt is removed.
     * @param externalXML url to xml
     */
    public boolean addAtomConfiguration(URL externalXML) {
        synchronized (this) {
            if (this.state == STATE_REMOVED) {
                return false;
            }
            this.configuration.addConfiguration(externalXML);
            return true;
        }
    }

    /**
     * Method cleares configuration.
     * You can not call this method when jbrt is removed.
     */
    public void clearConfiguration() {
        synchronized (this) {
            if (this.state == STATE_REMOVED) {
                return;
            }
            this.configuration.clear();
        }
    }

    /**
     * actual buffer size value.
     * @return actual buffer size value
     */
    public int getBufferSize() {
        synchronized (this) {
            return this.bufferSize;
        }
    }

    /**
     * Return actual list of throwables in memory.
     * @return list , an empty list if nothing.
     */
    public List<JThrowable> getThrowablesInMemory() {
        synchronized (this) {
            if (this.scenario != null) {
                return this.scenario.getThrowablesInMemory();
            }
            return new LinkedList<JThrowable>();
        }
    }

    /**
     * Set buffer value.
     * Can be called only when jbrt is NOT running
     * @param value new buffer size value
     * @return true if OK
     */
    public boolean setBufferSize(int value) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            if (value < 0) {
                return false;
            }
            this.bufferSize = value;
            return true;
        }
    }

    /**
     * @return actual working directory.
     */
    public File getWorkingDirectory() {
        synchronized (this) {
            return this.workingDirectory;
        }
    }

    /**
     * Set actual working directory.
     * Can be called only when jbrt is NOT running
     * @param workingDirectory
     * @return true if OK
     */
    public boolean setWorkingDirectory(File workingDirectory) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            this.workingDirectory = workingDirectory;
            return true;
        }
    }

    /**
     * Method add apperance listener. It will be called back
     * when an throwable occures.
     * Can be called only when jbrt is NOT running
     * @param e reference
     * @return true if OK.
     */
    public boolean addAppearanceListener(JAppearanceListener e) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            return appListeners.add(e);
        }
    }

    /**
     * Removes all throwable apperance listeners
     * Can be called only when jbrt is NOT running
     * @return
     */
    public boolean clearAppearanceListeners() {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            appListeners.clear();
            return true;
        }
    }

    /**
     * Method add new error handler that will handle some
     * domain of throwables.
     * Can be called only when jbrt is NOT running
     * @param e reference to handler
     * @return true if OK
     */
    public boolean addErrorHandler(JErrorHandler e) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            return errorHandlers.add(e);
        }
    }

    /**
     * removes all errors handlers
     * Can be called only when jbrt is NOT running
     * @return
     */
    public boolean clearErrorHandlers() {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            errorHandlers.clear();
            return true;
        }
    }

    /**
     * Method adds new response listener. Listener will called back
     * when response will come.
     * Can be called only when jbrt is NOT running
     * @param e
     * @return
     */
    public boolean addResponseListener(JResponseListener e) {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            return resListeners.add(e);
        }
    }

    /**
     * Removes all response listeners.
     * Can be called only when jbrt is NOT running
     * @return true if OK
     */
    public boolean clearResponseListeners() {
        synchronized (this) {
            if (this.state != STATE_NOT_STARTED) {
                return false;
            }
            resListeners.clear();
            return true;
        }
    }

    /**
     * method will invode delete storage procedure of scenario.
     */
    public void deleteStored() {
        synchronized (this) {
            this.scenario.delete();
        }
    }

    /**
     * Set a logic that is processed while internal error occurence.
     * By default is is set to null (internal errors are ignored).
     * @param handler reference to internal error handler.
     */
    public void setInternalErrorHandler(JInternalErrorHandler handler) {
        synchronized (this) {
            this.internalErrorHandler = handler;
        }
    }

    /**
     * Method completly stop jbrt, should be called only from factory.
     */
    void remove() {
        synchronized (this) {
            removeErrorHandlers();
            this.errorHandlers.clear();
            this.appListeners.clear();
            this.resListeners.clear();
            this.configuration.clear();
            removeShutDownHook();
            scenario.delete();
            scenario = null;
            this.state = STATE_REMOVED;
        }
    }
}
