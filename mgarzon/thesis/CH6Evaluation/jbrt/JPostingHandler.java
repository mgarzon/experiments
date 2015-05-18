package org.jbrt.client.handler;

import org.jbrt.client.exception.JBrtException;

/**
 * It is a layer before posting to
 * server where you can choose wether posting to server or not.
 * If you use this way you have to call posting JBrt rutine alone.
 * @author Cipov Peter
 */
public interface JPostingHandler {
    /**
     * Scenario should call this method before posting to server.
     * You can easily reat to this event
     * @param brt instance of local jbrt
     * @throws Exception if exception while posting.
     */
    public void onPosting() throws JBrtException;
}
