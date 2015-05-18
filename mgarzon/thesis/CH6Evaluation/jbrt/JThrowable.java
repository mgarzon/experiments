package org.jbrt.client;


import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

/**
 * <p>Main throwable storage class. It is implemented as (nearly) immutable
 * and synchronized class</p>
 * Throwable is always recognized according to its stacktrace and
 * message. It means that one hascode is computed from that two fields and 
 * objects are compared according to this hash code.
 * But there are simply cases, when it is not very convenient. (for example
 * when you just add some new empty lines - the same code, but another hashcode )
 * Therefore hash can counted in two ways :
 * defalut<br>
 * normal - lines will be ignored <br>
 *
 *
 * @author Cipov Peter
 * @version 1.0
 */
public class JThrowable implements Serializable {

    /** serial version UID for this class. */
    private static final long serialVersionUID = 12340004L;
    /** empty string for internal use. */
    transient private static final String EMPTY_STRING = "";
    /**Hashcode creator class.*/
    private static JHashCodeCreator hashCodeCreator = new JDefaultHashCodeCreator();
    /** message that can be passed (f.e. from logger log method , etc.).*/
    private final String message;
    /** Exception occurrence time.*/
    private final Date date;
    /** JException hash code - it will be computed.*/
    private final String hashCode;
    /** throwable instance */
    private final Throwable throwable;
    /** Additional info storage field. */
    private final HashMap<String, String> additionalInfo = new HashMap<String, String>();

    public JThrowable(Throwable t, String message) {
        this.message = message;
        this.date = new Date();
        this.throwable = t;
        this.hashCode = hashCodeCreator.countHashCode(t);
    }

    public JThrowable(Throwable t) {
        this(t, EMPTY_STRING);
    }

    public synchronized  static void setHashCodeCreator(JHashCodeCreator hashCodeCreator) {
        JThrowable.hashCodeCreator = hashCodeCreator;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String getHashCode() {
        return hashCode;
    }

    

    /**
     * Method will add new additional info
     * @param key String that gives unique name
     * @param value String value
     * @return the previous value associated with key, or null 
     * if there was no mapping for key. (A null return can also 
     * indicate that the map previously associated null with key.)
     * @see java.util.HashMap
     */
    public synchronized String putAdditionalInfo(String key, String value) {
        return additionalInfo.put(key, value);
    }

    /**
     * Method will return actual content of additional info store
     * @return a set view of the mappings contained in this field
     */
    public synchronized Set<Entry<String, String>> additionalInfoEntrySet() {
        return additionalInfo.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JThrowable other = (JThrowable) obj;
        if ((this.hashCode == null) ? (other.hashCode != null) : !this.hashCode.equals(other.hashCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.hashCode != null ? this.hashCode.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String lineSep = "\n";
        StringWriter sb = new StringWriter();
        sb.append("{ " + lineSep);
        sb.append("HASHCODE: " + this.hashCode + lineSep);
        sb.append("DATE: " + this.date.toString() + lineSep);
        sb.append("MESSAGE: " + this.message + lineSep);
        sb.append("ADDITIONAL INFO: " + lineSep);

        Entry<String, String> next;
        for (Iterator<Entry<String, String>> iterator = additionalInfo.entrySet().iterator();
                iterator.hasNext();) {
            next = iterator.next();
            sb.append("\tKEY: " + next.getKey());
            sb.append("\tVALUE: " + next.getValue() + lineSep);
        }
        sb.append("STACK TRACE: " + lineSep);
        throwable.printStackTrace(new PrintWriter(sb));

        sb.append("}" + lineSep);
        sb.flush();
        return sb.toString();
    }
}
