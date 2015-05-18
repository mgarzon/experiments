package org.jbrt.client;

/**
 * Response object that is parsed from XML.<br>
 * Response contains:<br>
 * <blockquote>
 * exception hash code<br>
 * response String for user<br>
 * response String for program<br>
 * </blockquote>
 * @author Cipov Peter
 * @version 1.0
 */
public class JResponse {
	private final String hashCode;
	private final String responseForUser;
	private final String responseForProgram;
	private static final String EMPTY_STRING = "";
	
	public JResponse(String hashCode, String responseForProgram,
			String responseForUser) {
		this.hashCode = hashCode;
		this.responseForProgram = responseForProgram == null ? EMPTY_STRING : responseForProgram;
		this.responseForUser = responseForUser == null ? EMPTY_STRING : responseForUser;
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JResponse other = (JResponse) obj;
        if ((this.hashCode == null) ? (other.hashCode != null) : !this.hashCode.equals(other.hashCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.hashCode != null ? this.hashCode.hashCode() : 0);
        return hash;
    }

	

	public String getHashCode() {
		return hashCode;
	}

	public String getResponseForUser() {
		return responseForUser;
	}

	public String getResponseForProgram() {
		return responseForProgram;
	}
	
	@Override
	public String toString() {
		String linebreak = "\n";
		return "HASHCODE:\t"+ hashCode + linebreak +
			   "MESSAGE FOR USER:\t"+responseForUser + linebreak +
			   "MESSAGE FOR PROGRAM:\t"+responseForProgram + linebreak;
	}
	
	
	
}
