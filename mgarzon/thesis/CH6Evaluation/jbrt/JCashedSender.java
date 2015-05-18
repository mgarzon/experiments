package org.jbrt.client.net;

import org.jbrt.client.JResponseBean;
import java.io.IOException;
import java.util.List;
import org.jbrt.client.JThrowable;
import org.jbrt.client.config.JConfigAtom;

/**
 *
 * @author Cipov Peter
 */
public interface JCashedSender extends JSender{

    public void setCasheSize(long size);
    public long getCashSize();
    public JResponseBean simplePost(List<JThrowable> throwables, JConfigAtom atom) throws Exception;
    public JResponseBean post() throws Exception;
    public void store(List<JThrowable> throwables, JConfigAtom atom) throws IOException;
    public void clean() throws IOException;
    
}
