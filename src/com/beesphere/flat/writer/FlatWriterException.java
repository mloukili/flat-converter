package com.beesphere.flat.writer;

public class FlatWriterException extends Exception {

	private static final long serialVersionUID = 3778652114497335944L;
	
	private int lineNumber;
	private String lineFragment;
	
	public FlatWriterException (int lineNumber, String lineFragment) {
		this ();
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatWriterException (int lineNumber) {
		this (lineNumber, null);
	}

	public FlatWriterException () {
		super ();
	}

	public FlatWriterException (String message, int lineNumber, String lineFragment) {
		this (message);
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatWriterException (String message) {
		super (message);
	}

	public FlatWriterException (Throwable throwable, String message) {
		super (message, throwable);
	}

	public FlatWriterException (Throwable throwable, String message, int lineNumber, String lineFragment) {
		this (throwable, message);
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatWriterException (Throwable throwable, int lineNumber, String lineFragment) {
		this (throwable);
		this.lineNumber = lineNumber;
		this.lineFragment = lineFragment;
	}

	public FlatWriterException (Throwable throwable) {
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
