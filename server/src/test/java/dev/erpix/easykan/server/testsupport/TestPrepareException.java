package dev.erpix.easykan.server.testsupport;

public class TestPrepareException extends RuntimeException {

	public TestPrepareException(String message) {
		super(appendContext(message));
	}

	public TestPrepareException(String message, Throwable cause) {
		super(appendContext(message), cause);
	}

	private static String appendContext(String message) {
		return "There was an error while preparing test scenario: " + message;
	}

}
