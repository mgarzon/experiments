package org.jbrt.client;

/**
 * Interface is used as a way how to obtain exceptions from JBrt.
 * It will be called back when system catches Exception. 
 * @author Cipov Peter
 * @version 1.0
 */
public interface JAppearanceListener {

    public void performAction(JThrowable exception);
    
}
