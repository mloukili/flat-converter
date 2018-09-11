package com.beesphere.flat.reader.impls;

import java.io.Serializable;

/**
 * Token is an internal token representation.
 * 
 * It is used as contract between the lexer and the parser.
 */
public class FlatToken implements Serializable {

	private static final long serialVersionUID = 1167380684818212317L;

	/** length of the initial token (content-)buffer */
	public static final int INITIAL_TOKEN_LENGTH = 50;

	// the token types
	/** Token has no valid content, i.e. is in its initilized state. */
	public static final int TT_INVALID = -1;
	/** Token with content, at beginning or in the middle of a line. */
	public static final int TT_TOKEN = 0;
	/** Token (which can have content) when end of file is reached. */
	public static final int TT_EOF = 1;
	/** Token with content when end of a line is reached. */
	public static final int TT_EORECORD = 2;

	/** Token type, see TT_xxx constants. */
	private int type = TT_INVALID;
	/** The content buffer. */
	private CharBuffer content = new CharBuffer (INITIAL_TOKEN_LENGTH);
	/**
	 * Token ready flag: indicates a valid token with content (ready for the
	 * parser).
	 */
	private boolean ready;

	public FlatToken reset () {
		content.clear();
		type = TT_INVALID;
		ready = false;
		return this;
	}
	
	public void append (CharBuffer data)  {
		content.append (data);
	}
	
	public void append (char data)  {
		content.append (data);
	}
	
	public void trim () {
		content.trimTrailingWhitespace ();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public CharBuffer getContent() {
		return content;
	}

	public void setContent(CharBuffer content) {
		this.content = content;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
}

