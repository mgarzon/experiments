package org.jbrt.client;

import java.util.LinkedList;
import java.util.List;
import org.jbrt.client.JResponse;

/**
 *
 * @author Cipov Peter
 */
public class JResponseBean {
    public static final int STATE_OK = 1;
    public static final int STATE_FAILURE = 2;

    List<JResponse> responses;
    int state;
    StringBuilder message;

    public JResponseBean() {
        responses = new LinkedList<JResponse>();
        message = new StringBuilder();
    }

    public List<JResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<JResponse> responses) {
        this.responses = responses;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message.toString();
    }

    public void setMessage(String message) {
        this.message.append(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JResponseBean other = (JResponseBean) obj;
        if (this.responses != other.responses && (this.responses == null || !this.responses.equals(other.responses))) {
            return false;
        }
        if (this.state != other.state) {
            return false;
        }
        if (this.message != other.message && (this.message == null || !this.message.equals(other.message))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.responses != null ? this.responses.hashCode() : 0);
        hash = 53 * hash + this.state;
        hash = 53 * hash + (this.message != null ? this.message.hashCode() : 0);
        return hash;
    }

    public void merge(JResponseBean bean) {
        if(bean == null) {return;}
        if( this.state < bean.state ) {
            this.state = bean.state;
        }

        this.message.append(bean.message);
        this.responses.addAll(bean.responses);

    }

}
