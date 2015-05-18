package org.jbrt.client.handler;

/**
 *
 * @author Cipov Peter
 */
public interface JInternalErrorHandler {
    public void handle(Throwable th, String message);
}
