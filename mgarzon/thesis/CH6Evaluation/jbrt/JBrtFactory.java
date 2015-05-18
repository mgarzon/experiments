package org.jbrt.client;

import org.jbrt.client.handler.JDefaultPostingHandler;

/**
 * This class handles that in system is only one instance of jbrt.
 * All calls for jbrt should be done with calling of methods of this
 * class.
 * @author Cipov Peter
 * @version 1.0
 */
public final class JBrtFactory {

    private static JBrt jbrtInstance = null;
    private static final Object jbrtSynch = new Object();

    private JBrtFactory() {
    }

    /**
     * Crete new untouched instance of Jbrt (only one in runtime)
     * @return JBrt instance
     */
    public static JBrt instance() {
        synchronized (jbrtSynch) {
            if (jbrtInstance == null) {
                jbrtInstance = new JBrt();
                jbrtInstance.setPostingHandler(new JDefaultPostingHandler());
            }
            return jbrtInstance;
        }
    }

    /**
     * Removes completely jbrt from runtime, also all
     */
    public static void removeJbrt() {
        synchronized (jbrtSynch) {
            if (jbrtInstance == null) {
                return;
            }
            jbrtInstance.remove();
            jbrtInstance = null;
        }
    }
}
