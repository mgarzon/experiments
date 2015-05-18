package org.jbrt.client.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jbrt.client.JBrt;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parsing class for extracting informations from xml, created for xsd version 1.0
 * Method parese has to be started before calling other methods.
 * @author Cipov Peter
 */
class JXmlConfigurationParser {

    private static final String SCHEMA_PATH = "/" + JXmlConfigurationParser.class.getPackage().getName().replaceAll("\\.", "/") + "/schema.xsd";
    private final InputSource xmlSource;
    private JXMLDefaultHandler handler;

    public JXmlConfigurationParser(URL xmlFile) throws SAXException {
        this.xmlSource = new InputSource(xmlFile.getPath());
    }

    public JXmlConfigurationParser(File xmlFile) throws SAXException, FileNotFoundException {
        this.xmlSource = new InputSource(new FileReader(xmlFile));
    }

    public JXmlConfigurationParser(InputSource xmlFile) {
        this.xmlSource = xmlFile;
    }

    public boolean isOverwritten() {
        return handler.overrideConfiguration;
    }

    public HashMap<String, JConfigAtom> getConfigurationAtoms() {
        return handler.atoms;
    }

    public List<JMappingBean> getMapping() {
        return handler.mapping;
    }

    public HashMap<String, Object> getOptions() {
        return handler.options;
    }

    public void parse() throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(true);
        spf.setNamespaceAware(true);
        spf.setFeature("http://apache.org/xml/features/validation/schema", true);
        SAXParser sp = spf.newSAXParser();
        XMLReader parser = sp.getXMLReader();
        parser.setErrorHandler(new ErrorHandler() {

            public void warning(SAXParseException ex) throws SAXException {
                JBrt.commitInternal(ex);
            }

            public void error(SAXParseException ex) throws SAXException {
                JBrt.commitInternal(ex);
            }

            public void fatalError(SAXParseException ex) throws SAXException {
                JBrt.commitInternal(ex);
            }
        });
        parser.setEntityResolver(new JXSDSchemaLoader(SCHEMA_PATH));
        this.handler = new JXMLDefaultHandler();
        parser.setContentHandler(this.handler);
        parser.parse(xmlSource);


        processOptions(this.handler);




    }

    private void processNetCasheSize(JXMLDefaultHandler handler) {
        Object object = handler.options.get(Helper.OPTION_BRT_NET_SENDER_CASHE_SIZE);
        if(object == null) {
            return;
        }
        String value = object.toString();
        Long number = null;
        try {
            number = Long.parseLong(value);
        } catch (Throwable th) {
            number = null;
        }
        handler.options.put(Helper.OPTION_BRT_NET_SENDER_CASHE_SIZE, number);
    }

    private void processNotMappedThrowable(JXMLDefaultHandler handler) {
        String atomId = (String) handler.options.get(Helper.OPTION_BRT_NOT_MAPPED_THROWABLE);
        if (atomId == null) {
            return;
        }
        JConfigAtom atom = handler.atoms.get(atomId);
        if (atom == null) {
            return;
        }
        handler.options.put(Helper.OPTION_BRT_NOT_MAPPED_THROWABLE, atom);
    }

    private void processOptions(JXMLDefaultHandler handler) {
        processNotMappedThrowable(handler);
        processNetCasheSize(handler);
    }

    private class JXMLDefaultHandler extends DefaultHandler {

        static final String TAG_CONFIGURATION = "configuration";
        static final String TAG_CONFIGURATION_ATT_OVERWRITE = "overwriteConfiguration";
        static final String TAG_ATOM = "atom";
        static final String TAG_ATOM_ATT_ID = "id";
        static final String TAG_URL = "url";
        static final String TAG_PROJECT = "project";
        static final String TAG_VERSION = "version";
        static final String TAG_LOCALE = "locale";
        static final String TAG_MAP = "map";
        static final String TAG_MAP_ATT_ATOM = "atom";
        static final String TAG_CLASS = "class";
        static final String TAG_CLASS_ATT_TYPE = "type";
        static final String TAG_OPTION = "option";
        static final String TAG_OPTION_ATT_NAME = "name";
        boolean overrideConfiguration;
        HashMap<String, JConfigAtom> atoms;
        LinkedList<JMappingBean> mapping;
        HashMap<String, Object> options;
        private StringBuilder value;
        private boolean inside;
        private String atomID;
        private String atomURL;
        private String atomProject;
        private String atomVersion;
        private String atomLanguage;
        private String mapAtomID;
        private String mappingType;
        private String optionName;

        @Override
        public void startDocument() throws SAXException {
            value = new StringBuilder();
            inside = false;
            this.atoms = new HashMap<String, JConfigAtom>();
            this.mapping = new LinkedList<JMappingBean>();
            this.options = new HashMap<String, Object>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String val;
            if (qName.equals(TAG_CONFIGURATION)) {
                inside = false;
                if ((val = attributes.getValue(TAG_CONFIGURATION_ATT_OVERWRITE)) != null) {
                    val = val.trim();
                    this.overrideConfiguration = Boolean.parseBoolean(val);
                } else {
                    this.overrideConfiguration = false;
                }
            } else if (qName.equals(TAG_ATOM)) {
                inside = false;
                atomID = attributes.getValue(TAG_ATOM_ATT_ID).trim();
            } else if (qName.equals(TAG_URL)) {
                inside = true;
            } else if (qName.equals(TAG_PROJECT)) {
                inside = true;
            } else if (qName.equals(TAG_VERSION)) {
                inside = true;
            } else if (qName.equals(TAG_LOCALE)) {
                inside = true;
            } else if (qName.equals(TAG_MAP)) {
                mapAtomID = attributes.getValue(TAG_MAP_ATT_ATOM).trim();
                inside = false;
            } else if (qName.equals(TAG_CLASS)) {
                mappingType = attributes.getValue(TAG_CLASS_ATT_TYPE);
                inside = true;
            } else if (qName.equals(TAG_OPTION)) {
                optionName = attributes.getValue(TAG_OPTION_ATT_NAME).trim();
                inside = true;
            }
        }

        private int mappingType(String type) {
            if (type == null) {
                return JMappingBean.PREFIX;
            }
            if (type.equals("direct")) {
                return JMappingBean.DIRECT;
            } else {
                return JMappingBean.PREFIX;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (qName.equals(TAG_ATOM)) {
                this.atoms.put(atomID, new JConfigAtom(atomID, atomURL, atomProject, atomVersion, atomLanguage));
            } else if (qName.equals(TAG_URL)) {
                this.atomURL = value.toString().trim();
                value.setLength(0);
            } else if (qName.equals(TAG_PROJECT)) {
                this.atomProject = value.toString().trim();
                value.setLength(0);
            } else if (qName.equals(TAG_VERSION)) {
                this.atomVersion = value.toString().trim();
                value.setLength(0);
            } else if (qName.equals(TAG_LOCALE)) {
                this.atomLanguage = value.toString().trim();
                value.setLength(0);
            } else if (qName.equals(TAG_MAP)) {
                mapAtomID = null;
            } else if (qName.equals(TAG_CLASS)) {
                this.mapping.add(
                        new JMappingBean(
                        value.toString().trim(),
                        this.atoms.get(mapAtomID),
                        mappingType(mappingType)));
                value.setLength(0);
            } else if (qName.equals(TAG_OPTION)) {
                this.options.put(optionName, value.toString().trim());
                value.setLength(0);
            }
            inside = false;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inside) {
                value.append(ch, start, length);
            }
        }
    }

    private class JXSDSchemaLoader implements EntityResolver {

        private final String path;

        public JXSDSchemaLoader(String path) {
            this.path = path;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(JXSDSchemaLoader.class.getResourceAsStream(path));
        }
    }
}