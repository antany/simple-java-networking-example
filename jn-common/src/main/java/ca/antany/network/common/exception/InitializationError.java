package ca.antany.network.common.exception;

public class InitializationError extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public InitializationError(String message, Throwable ex) {
		super(message,ex);
	}
}
