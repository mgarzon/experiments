package org.jbrt.client.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of synchronized and serialized HashSet. 
 * Implements only adding and reading operations. It is 
 * designed to be used with multiple threads.
 * @author Cipov Peter
 * @version 1.0 01/9/2008
 */
public class JHashSetStore<E> {

    /** File instance to source file. There is this object serialized. */
    private File outFile;
    /** 
     * HashSet instance, that is serialized. 
     * @serial 
     */
    private HashSet<E> store;

    /**
     * Create object instance with reference to store file.
     * @param outFile Reference to file where object will be stored.
     */
    public JHashSetStore(File outFile) {
        this.outFile = outFile;
        store = new HashSet<E>();
    }

    /**
     * Method will serialize object to file.
     * If file exists, method will override it.
     * @throws IOException if something happens while storing to file.
     */
    public void save() throws IOException {
        synchronized (this) {
            FileOutputStream out = null;
            ObjectOutputStream stream = null;
            try {
                out = new FileOutputStream(outFile, false);
                stream = new ObjectOutputStream(out);
                stream.writeObject(store);
                stream.flush();
            } finally {
                try {
                    stream.close();
                } finally {
                    out.close();
                }
            }
        }
    }

    /**
     * It will load HashSet from file.
     * @param file Source file.
     * @return HashSet serialized from file.
     * @throws Exception If something unexpected occurs while reading.
     */
    private HashSet<?> loadFromFile(File file) throws Exception {
        FileInputStream in = null;
        ObjectInputStream stream = null;
        HashSet<?> object = null;

        try {
            in = new FileInputStream(file);
            stream = new ObjectInputStream(in);
            object = (HashSet<?>) stream.readObject();
        } finally {
            try {
                stream.close();
            } finally {
                in.close();
            }
        }
        return object;
    }

    /**
     * Removes all of the elements from this set.  The list will
     * be empty after this call returns.
     */
    public void clear() {
        synchronized (this) {
            store.clear();
        }
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this set
     * contains an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param object element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    public boolean contains(E object) {
        synchronized (this) {
            return store.contains(object);
        }
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element <tt>e</tt> to this set if
     * this set contains no element <tt>e2</tt> such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>.
     *
     * @param object element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    public boolean add(E object) {
        synchronized (this) {
            return store.add(object);
        }
    }

    /**
     * Object will be synchronized with store in memory but file will not
     * be affected.
     */
    @SuppressWarnings("unchecked")
    public void synchronizeWithMemory() {
        synchronized (this) {
            HashSet<E> objects = null;
            try {
                objects = (HashSet<E>) loadFromFile(outFile);
            } catch (Exception ex) {
                objects = new HashSet<E>();
            } finally {
                if (objects == null) {
                    objects = new HashSet<E>();
                }
            }
            for (E object : objects) {
                if (!store.contains(object)) {
                    store.add(object);
                }
            }
        }
    }

    public boolean remove(E object) {
        synchronized (this) {
            return this.store.remove(object);
        }
    }

    public void synchronizeWithStore() throws IOException {
        synchronized (this) {
            synchronizeWithMemory();
            save();
        }
    }

    public Iterator<E> iterator() {
        synchronized (this) {
            return this.store.iterator();
        }
    }

    public int size() {
        synchronized (this) {
            return this.store.size();
        }
    }

    public List<E> getList() {
        synchronized (this) {
            //defensive coding
            return new ArrayList<E>(store);
        }
    }
}
