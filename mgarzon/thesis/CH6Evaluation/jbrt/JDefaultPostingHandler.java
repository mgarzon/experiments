package org.jbrt.client.handler;

import org.jbrt.client.JBrt;
import org.jbrt.client.JBrtFactory;
import org.jbrt.client.exception.JBrtException;

/**
 * Implementation of standart posting handler. It is recommended to use
 * this class (or extended in your class) for post handling.
 * 
 * @author Cipov Peter
 * @version 1.0
 */
public class JDefaultPostingHandler implements JPostingHandler {

    public void onPosting() throws JBrtException{
        try {
            Thread postingThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        JBrtFactory.instance().postExceptions();
                    } catch (JBrtException e) {
                        JBrt.commitInternal(e);
                    }
                }
            });
            // fixing memory overflow failure not enough space for exception handling
            postingThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                public void uncaughtException(Thread t, Throwable e) {
                    JBrt.commitInternal(e);
                }
            });
            postingThread.start();

        } catch (Throwable e) {
            JBrt.commitInternal(e);
        }
        
    }
}
