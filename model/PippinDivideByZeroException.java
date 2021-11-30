package proj01.model;

public class PippinDivideByZeroException extends PippinException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PippinDivideByZeroException() { }

	public PippinDivideByZeroException(String message) { 
		super(message);
	}

	public PippinDivideByZeroException(Throwable cause) { 
		super(cause);
	}

	public PippinDivideByZeroException(String message, Throwable cause) {
		super(message, cause);
	}

	public PippinDivideByZeroException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
