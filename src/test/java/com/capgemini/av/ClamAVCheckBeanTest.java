package com.capgemini.av;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.capgemini.av.ClamAVCheckClientBean;

import fi.solita.clamav.ClamAVClient;


public class ClamAVCheckBeanTest {

	@Mock
	private ClamAVClient client;
	
	private ClamAVCheckClientBean bean;	
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		bean = new ClamAVCheckClientBean(client);
		deleteTestFile();
	}
	
	@After
	public void tearDown() throws Exception {
		deleteTestFile();
	}
	
	private void deleteTestFile(){
		File testFile = new File("src/test/resources/testfile");
		testFile.delete();
	}

	@Test(expected=RuntimeException.class)
	public void testVirusFoundAndFileIsRemoved() throws Exception {	
		File testFile = createTempFile("src/test/resources/testfile");
		assertTrue(testFile.exists());
		when(client.scan(any(InputStream.class))).thenReturn("FOUND".getBytes());
		try {
			bean.performAntiVirusCheckOn("correlationId", "src/test/resources/testfile");
		} catch (RuntimeException e) {
			assertFalse(testFile.exists());
			throw(e);
		}
	}

	@Test
	public void testNoVirusFoundAndFileIsLeftAlone() throws Exception {	
		File testFile = createTempFile("src/test/resources/testfile");
		assertTrue(testFile.exists());
		when(client.scan(any(InputStream.class))).thenReturn("OK".getBytes());
		bean.performAntiVirusCheckOn("correlationId", "src/test/resources/testfile");
		assertTrue(testFile.exists());
	}
	
	private File createTempFile(String fullFilePath) throws Exception {
		File file = new File(fullFilePath);
		if (!file.createNewFile()) {
			throw new RuntimeException("Unable to create file during test.");
		}
		return file;
	}

}
