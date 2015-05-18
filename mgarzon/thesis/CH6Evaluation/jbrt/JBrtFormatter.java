package org.jbrt.client.net;

import org.jbrt.client.JResponseBean;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jbrt.client.JBrt;
import org.jbrt.client.JResponse;
import org.jbrt.client.JThrowable;
import org.jbrt.client.config.JConfigAtom;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;



/**
 *
 * @author Cipov Peter
 * @version 1.0
 */
public final class JBrtFormatter {
    //singleton

    private JBrtFormatter() {
    }
    
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final String LINE_BREAK = "\r\n";
    private static final String RESPONSE_SCHEMA_PATH = "/" + JBrtFormatter.class.getPackage().getName().replaceAll("\\.", "/") + "/response.xsd";


    /**
     * Convets string from inputstream to response.
     * @param input Inputstream
     * @return List with responses
     * @throws java.lang.Exception
     */
    public static JResponseBean convertXMLToJResponse(InputStream input) throws Exception {

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

        parser.setEntityResolver(new JXSDSchemaLoader(RESPONSE_SCHEMA_PATH));

        JResponseHandler handler = new JResponseHandler();
        parser.setContentHandler(handler);
        parser.parse(new InputSource(input));

        JResponseBean bean = new JResponseBean();
        bean.setResponses(handler.getResponse());
        bean.setMessage(handler.getProcessMessage());
        bean.setState(handler.getState());

        return bean;

    }
    

    /**
     * Print stack trace to string in one standart way.
     * @param s OutputStream for writing
     * @param t throwable to trace.
     */
    private static void printStackTrace(PrintStream out, Throwable t) throws IOException{
            out.print("<line>");
            escape(out, t.toString());
            out.print("</line>");
            out.print(LINE_BREAK);
            StackTraceElement[] trace = t.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                out.print("<line>");
                escape(out, "\tat " + trace[i]);
                out.print("</line>");
                out.print(LINE_BREAK);
            }

            Throwable ourCause = t.getCause();
            if (ourCause != null) {
                printStackTraceAsCause(out, trace, ourCause);
            }
    }

    /**
     * Print our stack trace as a cause for the specified stack trace.
     * @param s OutputStream for writing
     * @param causedTrace trace from upper throwable
     * @param cause caused throwable
     */
    private static void printStackTraceAsCause(PrintStream out,
            StackTraceElement[] causedTrace, Throwable cause) throws IOException {

        // Compute number of frames in common between this and caused
        StackTraceElement[] trace = cause.getStackTrace();
        int m = trace.length - 1, n = causedTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
            m--;
            n--;
        }
        int framesInCommon = trace.length - 1 - m;

        out.print("<line>");
        escape(out, "Caused by: " + cause);
        out.print("</line>");
        out.print(LINE_BREAK);
        for (int i = 0; i <= m; i++) {
            out.print("<line>");
            escape(out, "\tat " + trace[i]);
            out.print("</line>");
            out.print(LINE_BREAK);
        }
        if (framesInCommon != 0) {
            out.print("<line>");
            escape(out, "\t... " + framesInCommon + " more");
            out.print("</line>");
            out.print(LINE_BREAK);
        }

        // Recurse if we have a cause
        Throwable ourCause = cause.getCause();
        if (ourCause != null) {
           printStackTraceAsCause(out, trace, ourCause);
        }
    }

    /** 
     * Append to the given StringBuffer an escaped version of the
     * given text string where XML special characters have been escaped.
     * For a null string we append "<null>"
     * @param 
     */
    private static void escape(PrintStream out, String text) throws IOException {
        if (text == null) {
            text = "";
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                out.print("&lt;");
            } else if (ch == '>') {
                out.print("&gt;");
            } else if (ch == '&') {
                out.print("&amp;");
            } else {
                out.print(ch);
            }
        }
    }
    /**
     * Crete xml log and writes it to outputstream.
     * @param out OutputStream
     * @param atom Configuration atom
     * @param throwables Log throwables.
     * @throws java.io.IOException
     */
    public static void writeLog(PrintStream out, JConfigAtom atom, Collection<JThrowable> throwables) throws IOException {

        writeLogStart(out, atom);
        writeContent(out, throwables);
        writeLogEnd(out);
    }

    public static void writeContent(PrintStream out, Collection<JThrowable> throwables) throws IOException {
        for (JThrowable throwable : throwables) {
            convertExceptionToStream(throwable, out);
        }
    }

    public static void writeLogEnd(PrintStream out) throws IOException {
        out.print("</log>");
        out.print(LINE_BREAK);
    }


    public static void writeLogStart(PrintStream out, JConfigAtom atom) throws IOException {
        out.print("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        out.print(LINE_BREAK);

        out.print("<log xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:noNamespaceSchemaLocation=\"./request.xsd\" version=\"1.0\">");
        out.print(LINE_BREAK);

        out.print("<project>");
        escape(out, atom.getProject());
        out.print("</project>");
        out.print(LINE_BREAK);

        out.print("<version>");
        escape(out, atom.getVersion());
        out.print("</version>");
        out.print(LINE_BREAK);

        out.print("<language>");
        escape(out, atom.getLanguage());
        out.print("</language>");
        out.print(LINE_BREAK);
    }

    private static void convertExceptionToStream(JThrowable exception, PrintStream out) throws IOException {
        out.print("<exception>");
        out.print(LINE_BREAK);
        out.print("<date value=\"");
        //computing in UTC+0 greenwitch
        out.print(TIME_FORMATTER.format(new Date(exception.getDate().getTime()  - TimeZone.getDefault().getRawOffset())));
        out.print("\" />");
        out.print(LINE_BREAK);

        out.print("<hashcode>");
        out.print(exception.getHashCode());
        out.print("</hashcode>");
        out.print(LINE_BREAK);

        out.print("<message>");
        escape(out, exception.getMessage());
        out.print("</message>");
        out.print(LINE_BREAK);

        out.print("<content>");
        out.print(LINE_BREAK);
        printStackTrace(out, exception.getThrowable());
        out.print("</content>");
        out.print(LINE_BREAK);

        out.print("<additionalInfo>");
        out.print(LINE_BREAK);
        Iterator<Entry<String, String>> iterator = exception.additionalInfoEntrySet().iterator();
        Entry<String, String> next;

        for (; iterator.hasNext();) {
            next = iterator.next();
            out.print("<addInfoItem key=\"");
            escape(out, next.getKey());
            out.print("\">");
            escape(out, next.getValue());
            out.print("</addInfoItem>");
            out.print(LINE_BREAK);
        }
        out.print("</additionalInfo>");
        out.print(LINE_BREAK);
        out.print("</exception>");
        out.print(LINE_BREAK);
    }
}
/**
 * SAX data response handler.
 * @author Cipov Peter
 * @version 1.0
 */
class JResponseHandler extends DefaultHandler {

    private static final String TAG_STATE = "state";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_EXCEPTION = "exception";
    private static final String TAG_HASHCODE = "hashcode";
    private static final String TAG_MESSAGE_FOR_USER = "messageForUser";
    private static final String TAG_MESSAGE_FOR_PROGRAM ="messageForProgram";

    private LinkedList<JResponse> response = new LinkedList<JResponse>();
    private int state;
    private String processMessage;

    private boolean canRead = false;
    private StringBuilder value = new StringBuilder(1024);
    
    private String hashCode;
    private String messageForUser;
    private String messageForProgram;



    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if(name.equals(TAG_STATE)) {
            canRead = true;
        } else if(name.equals(TAG_MESSAGE)) {
            canRead = true;
        } else if(name.equals(TAG_EXCEPTION)) {
            hashCode = "";
            messageForProgram = "";
            messageForUser = "";
            canRead = false;
        } else if(name.equals(TAG_HASHCODE)) {
            canRead = true;
        } else if(name.equals(TAG_MESSAGE_FOR_USER)) {
            canRead = true;
        } else if(name.equals(TAG_MESSAGE_FOR_PROGRAM)) {
            canRead = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if(name.equals(TAG_STATE)) {
            state = Integer.parseInt(value.toString().trim());
            value.setLength(0);
            canRead = false;
        } else if(name.equals(TAG_MESSAGE)) {
            processMessage = value.toString().trim();
            value.setLength(0);
            canRead = false;
        } else if(name.equals(TAG_EXCEPTION)) {
            response.add(new JResponse(hashCode, messageForProgram, messageForUser));
            canRead = false;
        } else if(name.equals(TAG_HASHCODE)) {
            hashCode = value.toString().trim();
            value.setLength(0);
            canRead = false;
        } else if(name.equals(TAG_MESSAGE_FOR_USER)) {
            messageForUser = value.toString().trim();
            value.setLength(0);
            canRead = false;
        } else if(name.equals(TAG_MESSAGE_FOR_PROGRAM)) {
            messageForProgram = value.toString().trim();
            value.setLength(0);
            canRead = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if(canRead) {
            value.append(ch, start, length);
        }
      
    }

    public List<JResponse> getResponse() {
        return response;
    }

    public String getProcessMessage() {
        return processMessage;
    }

    public int getState() {
        return state;
    }

    
}
final class JXSDSchemaLoader implements EntityResolver {

    private final String path;

    public JXSDSchemaLoader(String path) {
        this.path = path;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(JXSDSchemaLoader.class.getResourceAsStream(path));
    }
}

