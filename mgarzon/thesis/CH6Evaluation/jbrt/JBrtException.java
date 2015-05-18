package org.jbrt.client.exception;

public class JBrtException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	public JBrtException() {
		super();
	}

	public JBrtException(String message, Throwable cause) {
		super(message, cause);
	}

	public JBrtException(String message) {
		super(message);
	}

	public JBrtException(Throwable cause) {
		super(cause);
	}

}
