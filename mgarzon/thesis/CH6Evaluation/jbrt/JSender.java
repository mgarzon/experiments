package org.jbrt.client.net;

import org.jbrt.client.JResponseBean;
import java.util.List;
import org.jbrt.client.JThrowable;
import org.jbrt.client.config.JConfigAtom;

/**
 *
 * @author Cipov Peter
 */
public interface JSender {
    
    public JResponseBean post(List<JThrowable> throwables, JConfigAtom atom) throws Exception;
}
