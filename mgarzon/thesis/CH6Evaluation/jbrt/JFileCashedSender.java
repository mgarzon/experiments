package org.jbrt.client.net;

import java.io.File;

/**
 *
 * @author Cipov Peter
 */
public interface JFileCashedSender extends JCashedSender{

    public boolean setRootDir(File dir);
    public File getRootDir();
}
