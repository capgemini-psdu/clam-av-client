package com.capgemini.av.exception;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class MonitoredErrorTest {

	private static final String CORRELATION_ID = "myCorrelationId";
	private static final String FILE_NAME = "myFilename.txt";
	private static final String ERROR_MESSAGE = "My Message Text";
	private static final Exception CAUSE = new IOException("Exception message");

	@Test
	public void testCreate() {
		try {
			MonitoredError.VIRUS_DETECTED.create(CORRELATION_ID, FILE_NAME, ERROR_MESSAGE, CAUSE);
		} catch (Exception e) {
			System.out.println("Exception message was: " + e.getMessage());
			assertTrue("Exception should be a RuntimeException", e instanceof RuntimeException);
			assertTrue("Cause should be an IOException", e.getCause() instanceof IOException);
		}
	}

	@Test
	public void testCreateWithoutReRaise() {
		try {
			MonitoredError.VIRUS_DETECTED.create(CORRELATION_ID, FILE_NAME, ERROR_MESSAGE, null);
		} catch (Exception e) {
			System.out.println("Exception message was: " + e.getMessage());
			assertTrue("Exception should be a RuntimeException", e instanceof RuntimeException);
			assertNull("Cause should be null", e.getCause());
		}
	}

}
