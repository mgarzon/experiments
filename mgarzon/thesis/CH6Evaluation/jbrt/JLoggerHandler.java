package org.jbrt.client.handler;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jbrt.client.JBrt;

/**
 *
 * @author Cipov Peter
 * @version 1.0 13/09/2008
 */
public class JLoggerHandler implements JErrorHandler {

    private Handler errorHandler;

    public void initialize() {
        errorHandler = new Handler() {

            @Override
            public void publish(LogRecord record) {
                if (record.getThrown() == null) {
                    //JBrt is designed only to catch Throwable.
                    return;
                }
                JBrt.commit(record.getThrown(), record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        Logger.getLogger("").addHandler(errorHandler);
    }

    public void remove() {
        if(errorHandler != null) {
            Logger.getLogger("").removeHandler(errorHandler);
        }
    }


}
