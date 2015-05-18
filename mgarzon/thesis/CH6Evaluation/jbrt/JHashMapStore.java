package org.jbrt.client.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JHashMapStore<E> {

    /** File instance to source file. There is this object serialized. */
    private File outFile;
    private HashMap<String, E> store = new HashMap<String, E>();

    public JHashMapStore(File outFile) {
        this.outFile = outFile;
    }

    public boolean add(String hash, E object) {
        synchronized (this) {
            E put = this.store.put(hash, object);
            return (put != null);
        }
    }

    public void clean() {
        synchronized (this) {
            this.store.clear();
        }
    }

    public boolean contains(E object) {
        synchronized (this) {
            return this.store.containsValue(object);
        }
    }

    public boolean contains(String hash) {
        synchronized (this) {
            return this.store.containsKey(hash);
        }
    }

    public List<E> getList() {
        synchronized (this) {
            return new ArrayList<E>(this.store.values());
        }
    }

    public Iterator<E> iterator() {
        synchronized (this) {
            return this.store.values().iterator();
        }
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, E> loadFromFile(File file) throws Exception {
        FileInputStream in = null;
        ObjectInputStream stream = null;
        HashMap<String, E> data = null;
        try {
            in = new FileInputStream(file);
            stream = new ObjectInputStream(in);
            data = (HashMap<String, E>) stream.readObject();
        } finally {
            try {
                stream.close();
            } finally {
                in.close();
            }
        }

        return data;
    }

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

    public int size() {
        synchronized (this) {
            return store.size();
        }
    }

    public void synchronizeWithMemory() {
        synchronized (this) {
            HashMap<String, E> load = null;
            try {
                load = loadFromFile(outFile);
            } catch (Exception ex) {
                load = new HashMap<String, E>();
            } finally {
                if (load == null) {
                    load = new HashMap<String, E>();
                }
            }
            store.putAll(load);
        }
    }

    public void synchronizeWithStore() throws IOException {
        synchronized (this) {
            synchronizeWithMemory();
            save();
        }
    }

    public void remove(String hash) {
        synchronized (this) {
            this.store.remove(hash);
        }
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}
