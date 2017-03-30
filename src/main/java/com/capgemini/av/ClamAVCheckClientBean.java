package com.capgemini.av;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.av.exception.MonitoredError;
import com.capgemini.av.util.StopWatch;

import fi.solita.clamav.ClamAVClient;

/**
 * Encapsulates all our anti-virus logic.
 */
public class ClamAVCheckClientBean implements AntiVirusCheckBean {

	private static final Logger logger = LoggerFactory.getLogger(ClamAVCheckClientBean.class);

	private ClamAVClient clamAVClient;

	/** Recommended for testing use only hence default scope. */
	ClamAVCheckClientBean(ClamAVClient clamAVClient) {
		this.clamAVClient = clamAVClient;
	}

	public ClamAVCheckClientBean(String server, int port, int timeOut) {
		this.clamAVClient = new ClamAVClient(server, port, timeOut);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performAntiVirusCheckOn(String correlationId, String fullFilePath) {
		
		byte[] reply = null;
		FileInputStream fis = null;
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			fis = inputStreamFor(fullFilePath);
			reply = clamAVClient.scan(fis);
			fis.close();
			sw.end();
			logger.debug("CorrelationId {} : Took [{}] milliseconds to AV the file {}", correlationId, sw.timeTaken(), fullFilePath);
			deleteAndLogIfVirusFound(correlationId, fullFilePath, reply);
		} catch (Exception e) {
			MonitoredError.ANTIVIRUS_EXECUTION_ERROR.create(correlationId, fullFilePath, "When performing AV check.", e);
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					MonitoredError.ANTIVIRUS_EXECUTION_ERROR.create(correlationId, fullFilePath, "When closing FileInputStream", e);
				}
			}
		}
	}

	private void deleteAndLogIfVirusFound(String correlationId, String fullFilePath, byte[] reply) throws UnsupportedEncodingException {
		if (!ClamAVClient.isCleanReply(reply)) {
	        File file = new File(fullFilePath);
	        if (file.exists()) {
	            if(!file.delete()) {
	            	throw new RuntimeException("Didn't delete");
	            }
	        }          
	        MonitoredError.VIRUS_DETECTED.create(correlationId, fullFilePath, "Virus found in file: file will be deleted");			
		}
	}
	
	private FileInputStream inputStreamFor(String fullFilePath) throws FileNotFoundException {
		return new FileInputStream(fullFilePath);
	}
}