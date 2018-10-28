package b.exceptions;

public class InvalidLogging extends CouponSystemException {

	public InvalidLogging(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessage() {
		return "Logging failed";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
