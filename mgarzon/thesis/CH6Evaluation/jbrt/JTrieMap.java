package org.jbrt.client.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Cipov Peter
 */
public class JTrieMap<K> {

    private final CharacterNode<K> ROOT = new CharacterNode<K>('\0');

    public JTrieMap() {
    }

    /**
     * Insert in public
     * @param string
     * @param object
     */
    @SuppressWarnings("unchecked")
    public void put(String string, K object) {
        CharacterNode node = ROOT;
        for (int i = 0, k = string.length(); i < k; i++) {
            node = node.addChildren(string.charAt(i));
        }
        node.addPublicObject(object);
    }

    @SuppressWarnings("unchecked")
    public void putPrivate(String string, K object) {
        CharacterNode node = ROOT;
        for (int i = 0, k = string.length(); i < k; i++) {
            node = node.addChildren(string.charAt(i));
        }
        node.addPrivateObject(object);
    }

    public List<K> get(String key) {
        return find(ROOT, key);
    }

    public List<K> get(Class className) {
        return find(ROOT, className.getName());
    }

    private List<K> find(CharacterNode<K> node, String key) {
        List<K> objects = new LinkedList<K>();
        List<K> temp;

        for (int i = 0, k = key.length(); i <= k; i++) {
            temp = node.getPublicObjects();
            if (temp != null) {
                objects.addAll(temp);
            }
            if (i == k) {
                temp = node.getPrivateObjects();
                if (temp != null) {
                    objects.addAll(temp);
                }
                //find next node
                break;
            }
            node = node.findChildren(key.charAt(i));
            if (node == null) {
                break;
            }
        }
        return objects;
    }

    public int size() {
        Queue<CharacterNode<K>> queue = new LinkedList<CharacterNode<K>>();
        queue.add(ROOT);
        CharacterNode<K> node;
        List<CharacterNode<K>> children;
        List<K> objects;
        int size = 0;

        while (!queue.isEmpty()) {
            node = queue.poll();
            children = node.getChildren();
            objects = node.getPublicObjects();
            if (objects != null) {
                if (!objects.isEmpty()) {
                    size += 1;
                }
            }
            objects = node.getPrivateObjects();
            if (objects != null) {
                if (!objects.isEmpty()) {
                    size += 1;
                }
            }
            if (children != null) {
                queue.addAll(children);
            }

        }
        return size;
    }

    public void clear() {
        ROOT.clear();
    }

    private class CharacterNode<K> {

        private final char chracter;
        private List<CharacterNode<K>> children = null;
        private List<K> privateObjects = null;
        public List<K> publicObjects = null;

        public CharacterNode(char chracter) {
            this.chracter = chracter;
        }

        public char getChracter() {
            return chracter;
        }

        public List<CharacterNode<K>> getChildren() {
            return children;
        }

        public void addPrivateObject(K object) {
            if (privateObjects == null) {
                privateObjects = new LinkedList<K>();
            }
            privateObjects.add(object);
        }

        public void addPublicObject(K object) {
            if (publicObjects == null) {
                publicObjects = new LinkedList<K>();
            }
            publicObjects.add(object);
        }

        public CharacterNode<K> findChildren(char ch) {
            CharacterNode<K> temp = null;
            if (children == null) {
                return null;
            }
            for (int i = 0, k = children.size(); i < k; i++) {
                temp = children.get(i);
                if (temp.getChracter() == ch) {
                    return temp;
                }
            }
            return null;
        }

        public CharacterNode<K> addChildren(char ch) {
            if (children == null) {
                children = new ArrayList<CharacterNode<K>>(5);
                CharacterNode<K> newChild = new CharacterNode<K>(ch);
                children.add(newChild);
                return newChild;
            }
            CharacterNode<K> node = null;
            for (int i = 0, k = children.size(); i < k; i++) {
                CharacterNode<K> car = children.get(i);
                if (car.chracter == ch) {
                    node = car;
                    break;
                }
            }
            if (node == null) {
                node = new CharacterNode<K>(ch);
                children.add(node);
            }
            return node;
        }

        public List<K> getPublicObjects() {
            return publicObjects;
        }

        public List<K> getPrivateObjects() {
            return privateObjects;
        }

        public void clear() {
            if (children != null) {
                children.clear();
            }
            if (privateObjects != null) {
                privateObjects.clear();
            }
            if (publicObjects != null) {
                publicObjects.clear();
            }
        }
    }
}

  

