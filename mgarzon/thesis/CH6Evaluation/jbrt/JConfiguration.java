package org.jbrt.client.config;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.InputSource;

/**
 *
 * @author Cipov Peter
 */
public class JConfiguration {

    private JTrieMap<JConfigAtom> mappping = new JTrieMap<JConfigAtom>();

    //options
    private Map<String, Object> options = new HashMap<String, Object>(0);

    public JConfiguration() {
    }

    public void addConfiguration(URL xmlFile) {
        synchronized (this) {
            JXmlConfigurationParser parser = null;
            try {
                parser = new JXmlConfigurationParser(xmlFile);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not parse ! ",ex);
            }
            processParsing(parser);
        }
    }

    public void addConfiguration(File xmlFile) {
        synchronized (this) {
            JXmlConfigurationParser parser = null;
            try {
                parser = new JXmlConfigurationParser(xmlFile);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not parse ! ",ex);
            }
            processParsing(parser);
        }
    }

    public void addConfiguration(InputSource xmlSource) {
        synchronized (this) {
            JXmlConfigurationParser parser = null;
            try {
                parser = new JXmlConfigurationParser(xmlSource);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not parse ! ",ex);
            }
            processParsing(parser);
        }
    }

    private void processParsing(JXmlConfigurationParser parser)  {
        if (parser == null) {
            throw new IllegalStateException("Parser was not created");
        }
        try {
            parser.parse();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        if (parser.isOverwritten()) {
            this.clear();
        }
        List<JMappingBean> beans = parser.getMapping();
        for(JMappingBean bean : beans) {
            if(bean.isPrefixType()) {
                mappping.put(bean.getName(), bean.getAtom());
            } else if(bean.isDirectType()) {
                mappping.putPrivate(bean.getName(), bean.getAtom());
            }
        }
        //options        
        this.options.putAll(parser.getOptions());
    }

    public Object getOption(String key) {
        synchronized (this) {
            return this.options.get(key);
        }
    }

    public boolean setOption(String key, Object val) {
        synchronized (this) {
            this.options.put(key, val);
            return true;
        }
    }

    public Collection<JConfigAtom> find(String key) {
        synchronized (this) {
            return this.mappping.get(key);
        }
    }

    public Collection<JConfigAtom> find(Class className) {
        synchronized (this) {
            return this.mappping.get(className);
        }
    }

    public void clear() {
        synchronized (this) {
            this.mappping.clear();
        }
    }

}
