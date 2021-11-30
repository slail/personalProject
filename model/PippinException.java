package proj01.model;

public class PippinException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PippinException() { }

	public PippinException(String message) { 
		super("Pippin " + message);
	}

	public PippinException(Throwable cause) { 
		super(cause);
	}


	public PippinException(String message, Throwable cause) { 
		super("Pippin " + message, cause);
	}

	public PippinException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super("Pippin " + message, cause, enableSuppression, writableStackTrace);
	}

}
