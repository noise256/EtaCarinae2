package util.math;

public class SingletonAccessException extends RuntimeException {
	private static final long serialVersionUID = -3761055756011099072L;
	
	public SingletonAccessException(String error) {
		super(error);
	}
}
