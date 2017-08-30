package org.fartpig.encodingchange.entity;

public class FileEncodingInfo {

	private String fileName;
	private String fileEncoding;

	private boolean isLocalEncoding = false;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public boolean isLocalEncoding() {
		return isLocalEncoding;
	}

	public void setLocalEncoding(boolean isLocalEncoding) {
		this.isLocalEncoding = isLocalEncoding;
	}

}
