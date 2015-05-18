package org.jbrt.client;

/**
 * Implementing this interface and registering in JBrt is preferred way how to
 * obtain response from server.
 * @author Cipov Peter
 * @version 1.0
 */
public interface JResponseListener {

    public void performAction(JResponseBean bean);

}
