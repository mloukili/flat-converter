package com.beesphere.flat.reader;

public class FlatReaderException extends Exception {

	private static final long serialVersionUID = 3778652114497335944L;
	
	private int lineNumber;
	private String lineFragment;
	
	public FlatReaderException (int lineNumber, String lineFragment) {
		this ();
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatReaderException (int lineNumber) {
		this (lineNumber, null);
	}

	public FlatReaderException () {
		super ();
	}

	public FlatReaderException (String message, int lineNumber, String lineFragment) {
		this (message);
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatReaderException (String message) {
		super (message);
	}

	public FlatReaderException (Throwable throwable, String message) {
		super (message, throwable);
	}

	public FlatReaderException (Throwable throwable, String message, int lineNumber, String lineFragment) {
		this (throwable, message);
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatReaderException (Throwable throwable, int lineNumber, String lineFragment) {
		this (throwable);
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatReaderException (Throwable throwable) {
		super (throwable);
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLineFragment() {
		return lineFragment;
	}

	public void setLineFragment(String lineFragment) {
		this.lineFragment = lineFragment;
	}

}
