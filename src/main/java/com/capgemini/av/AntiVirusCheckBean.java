package com.capgemini.av;

/**
 * Encapsulates all our anti-virus logic.
 */
public interface AntiVirusCheckBean {

	/**
	 * Check for any virus in a given file.
	 * 
	 * If a virus is found an error will be logged and the file will be deleted.
	 * 
	 * @param correlationId
	 *            for logging purposes
	 * @param fullFilePath
	 *            fully qualified location of the file to be checked
	 */
	public void performAntiVirusCheckOn(String correlationId, String fullFilePath);

}
