package com.beesphere.flat.reader.impls;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.beesphere.flat.lang.LangUtils;
import com.beesphere.flat.reader.FlatReader;
import com.beesphere.flat.reader.FlatReaderException;

/**
 * Parses CSV files according to the specified configuration.
 * 
 * Because CSV appears in many different dialects, the parser supports many
 * configuration settings by allowing the specification of a {@link FlatStrategy}.
 * 
 * <p>
 * Parsing of a csv-string having tabs as separators, '"' as an optional value
 * encapsulator, and comments starting with '#':
 * </p>
 * <p>
 * Internal parser state is completely covered by the strategy and the
 * reader-state.
 * </p>
 * 
 * @author BeeSphere Team
 * 
 */
public abstract class AbstractFlatReader implements FlatReader {
	
	protected Charset charset = LangUtils.DEFAULT_CHARSET;

	// the input stream
	protected ExtendedBufferedReader in;

	protected FlatStrategy strategy = FlatStrategy.DEFAULT_STRATEGY;

	// the following objects are shared to reduce garbage
	/** A record buffer for getLine(). Grows as necessary and is reused. */
	protected final ArrayList<String> record = new ArrayList<String>();
	protected final FlatToken reusableToken = new FlatToken();
	protected final CharBuffer wsBuf = new CharBuffer();
	protected final CharBuffer code = new CharBuffer(4);

	@Override
	public void read (InputStreamReader reader) throws FlatReaderException {
		in = new ExtendedBufferedReader (reader);
		visitStart ();
		String [] line;
	    while ((line = getLine ()) != null)  {
	    	visitLine (line, getLineNumber ());
	    }
	    visitEnd ();
	    reset ();
	}
	
	public void reset () {
		in = null;
		record.clear ();
		reusableToken.reset ();
		wsBuf.clear();
		code.clear ();
		strategy = FlatStrategy.DEFAULT_STRATEGY;
		charset = LangUtils.DEFAULT_CHARSET;
	}
	
	protected abstract void visitStart () throws FlatReaderException;
	protected abstract void visitLine (String [] line, int lineNum) throws FlatReaderException;
	protected abstract void visitEnd () throws FlatReaderException;

	/**
	 * Parses from the current point in the stream til the end of the current
	 * line.
	 * 
	 * @return array of values til end of line ('null' when end of file has been
	 *         reached)
	 * @throws IOException
	 *             on parse error or input read-failure
	 */
	protected String [] getLine () throws FlatReaderException {
		String[] ret = LangUtils.EMPTY_STRING_ARRAY;
		record.clear();
		while (true) {
			reusableToken.reset();
			try {
				nextToken (reusableToken);
			} catch (IOException e) {
				throw new FlatReaderException (e, getLineNumber (), printRecord ());
			}
			switch (reusableToken.getType ()) {
			case FlatToken.TT_TOKEN:
				record.add (reusableToken.getContent ().toString());
				break;
			case FlatToken.TT_EORECORD:
				record.add (reusableToken.getContent ().toString());
				break;
			case FlatToken.TT_EOF:
				if (reusableToken.isReady ()) {
					record.add (reusableToken.getContent ().toString());
				} else {
					ret = null;
				}
				break;
			case FlatToken.TT_INVALID:
			default:
				// error: throw FlatReaderException
				throw new FlatReaderException ("line '" + getLineNumber () + "' invalid parse sequence", getLineNumber (), printRecord ());
				// unreachable: break;
			}
			if (reusableToken.getType () != FlatToken.TT_TOKEN) {
				break;
			}
		}
		if (!record.isEmpty()) {
			ret = (String[]) record.toArray (new String[record.size()]);
		}
		return ret;
	}
	
	protected String printRecord () {
		StringBuilder sb = new StringBuilder ();
		for (String l : record) {
			sb.append (l).append (LangUtils.PRINT_SPACE);
		}
		String str = sb.toString ();
		sb.setLength (0);
		sb = null;
		return str;
	}

	/**
	 * Returns the current line number in the input stream.
	 * 
	 * ATTENTION: in case your csv has multiline-values the returned number does
	 * not correspond to the record-number
	 * 
	 * @return current line number
	 */
	public int getLineNumber() {
		return in.getLineNumber ();
	}

	// ======================================================
	// the lexer(s)
	// ======================================================

	/**
	 * Convenience method for <code>nextToken(null)</code>.
	 */
	protected FlatToken nextToken () throws IOException {
		return nextToken (new FlatToken ());
	}

	/**
	 * Returns the next token.
	 * 
	 * A token corresponds to a term, a record change or an end-of-file
	 * indicator.
	 * 
	 * @param tkn
	 *            an existing Token object to reuse. The caller is responsible
	 *            to initialize the Token.
	 * @return the next token found
	 * @throws IOException
	 *             on stream access error
	 */
	protected abstract FlatToken nextToken (FlatToken tkn) throws IOException;

	/**
	 * A simple token lexer
	 * 
	 * Simple token are tokens which are not surrounded by encapsulators. A
	 * simple token might contain escaped delimiters (as \, or \;). The token is
	 * finished when one of the following conditions become true:
	 * <ul>
	 * <li>end of line has been reached (TT_EORECORD)</li>
	 * <li>end of stream has been reached (TT_EOF)</li>
	 * <li>an unescaped delimiter has been reached (TT_TOKEN)</li>
	 * </ul>
	 * 
	 * @param tkn
	 *            the current token
	 * @param c
	 *            the current character
	 * @return the filled token
	 * 
	 * @throws IOException
	 *             on stream access error
	 */
	protected FlatToken simpleTokenLexer(FlatToken tkn, int c) throws IOException {
		for (;;) {
			if (isEndOfLine(c)) {
				// end of record
				tkn.setType (FlatToken.TT_EORECORD);
				tkn.setReady (true);
				break;
			} else if (isEndOfFile(c)) {
				// end of file
				tkn.setType (FlatToken.TT_EOF);
				tkn.setReady (true);
				break;
			} else if (c == strategy.getDelimiter()) {
				// end of token
				tkn.setType (FlatToken.TT_TOKEN);
				tkn.setReady (true);
				break;
			} else if (c == '\\' && strategy.isUnicodeEscapeInterpretation()
					&& in.lookAhead() == 'u') {
				// interpret unicode escaped chars (like \u0070 -> p)
				tkn.append ((char) unicodeEscapeLexer(c));
			} else if (c == strategy.getEscape()) {
				tkn.append ((char) readEscape(c));
			} else {
				tkn.append ((char) c);
			}

			c = in.read();
		}

		if (strategy.isIgnoreTrailingWhitespaces()) {
			tkn.trim ();
		}

		return tkn;
	}

	/**
	 * An encapsulated token lexer
	 * 
	 * Encapsulated tokens are surrounded by the given encapsulating-string. The
	 * encapsulator itself might be included in the token using a doubling
	 * syntax (as "", '') or using escaping (as in \", \'). Whitespaces before
	 * and after an encapsulated token are ignored.
	 * 
	 * @param tkn
	 *            the current token
	 * @param c
	 *            the current character
	 * @return a valid token object
	 * @throws IOException
	 *             on invalid state
	 */
	protected FlatToken encapsulatedTokenLexer(FlatToken tkn, int c) throws IOException {
		// save current line
		int startLineNumber = getLineNumber();
		// ignore the given delimiter
		// assert c == delimiter;
		for (;;) {
			c = in.read();

			if (c == '\\' && strategy.isUnicodeEscapeInterpretation()
					&& in.lookAhead() == 'u') {
				tkn.append ((char) unicodeEscapeLexer(c));
			} else if (c == strategy.getEscape()) {
				tkn.append ((char) readEscape(c));
			} else if (c == strategy.getEncapsulator()) {
				if (in.lookAhead() == strategy.getEncapsulator()) {
					// double or escaped encapsulator -> add single encapsulator
					// to token
					c = in.read();
					tkn.append ((char) c);
				} else {
					// token finish mark (encapsulator) reached: ignore
					// whitespace till delimiter
					for (;;) {
						c = in.read();
						if (c == strategy.getDelimiter()) {
							tkn.setType (FlatToken.TT_TOKEN);
							tkn.setReady (true);
							return tkn;
						} else if (isEndOfFile(c)) {
							tkn.setType (FlatToken.TT_EOF);
							tkn.setReady (true);
							return tkn;
						} else if (isEndOfLine(c)) {
							// ok eo token reached
							tkn.setType (FlatToken.TT_EORECORD);
							tkn.setReady (true);
							return tkn;
						} else if (!isWhitespace(c)) {
							// error invalid char between token and next
							// delimiter
							throw new IOException(
									"(line "
											+ getLineNumber()
											+ ") invalid char between encapsulated token end delimiter");
						}
					}
				}
			} else if (isEndOfFile(c)) {
				// error condition (end of file before end of token)
				throw new IOException("(startline " + startLineNumber + ")"
						+ "eof reached before encapsulated token finished");
			} else {
				// consume character
				tkn.append((char) c);
			}
		}
	}

	/**
	 * Decodes Unicode escapes.
	 * 
	 * Interpretation of "\\uXXXX" escape sequences where XXXX is a hex-number.
	 * 
	 * @param c
	 *            current char which is discarded because it's the "\\" of
	 *            "\\uXXXX"
	 * @return the decoded character
	 * @throws IOException
	 *             on wrong unicode escape sequence or read error
	 */
	protected int unicodeEscapeLexer(int c) throws IOException {
		int ret = 0;
		// ignore 'u' (assume c==\ now) and read 4 hex digits
		c = in.read();
		code.clear();
		try {
			for (int i = 0; i < 4; i++) {
				c = in.read();
				if (isEndOfFile(c) || isEndOfLine(c)) {
					throw new NumberFormatException("number too short");
				}
				code.append((char) c);
			}
			ret = Integer.parseInt(code.toString(), 16);
		} catch (NumberFormatException e) {
			throw new IOException("(line " + getLineNumber()
					+ ") Wrong unicode escape sequence found '"
					+ code.toString() + "'" + e.toString());
		}
		return ret;
	}

	private int readEscape(int c) throws IOException {
		// assume c is the escape char (normally a backslash)
		c = in.read();
		int out;
		switch (c) {
		case 'r':
			out = '\r';
			break;
		case 'n':
			out = '\n';
			break;
		case 't':
			out = '\t';
			break;
		case 'b':
			out = '\b';
			break;
		case 'f':
			out = '\f';
			break;
		default:
			out = c;
		}
		return out;
	}

	// ======================================================
	// strategies
	// ======================================================

	/**
	 * Sets the specified CSV Strategy
	 * 
	 * @return current instance of CSVParser to allow chained method calls
	 */
	public void setStrategy (FlatStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Obtain the specified CSV Strategy
	 * 
	 * @return strategy currently being used
	 */
	public FlatStrategy getStrategy () {
		return this.strategy;
	}

	// ======================================================
	// Character class checker
	// ======================================================

	/**
	 * @return true if the given char is a whitespace character
	 */
	protected boolean isWhitespace(int c) {
		return Character.isWhitespace((char) c)
				&& (c != strategy.getDelimiter());
	}

	/**
	 * Greedy - accepts \n and \r\n This checker consumes silently the second
	 * control-character...
	 * 
	 * @return true if the given character is a line-terminator
	 */
	protected boolean isEndOfLine(int c) throws IOException {
		// check if we have \r\n...
		if (c == '\r') {
			if (in.lookAhead() == '\n') {
				// note: does not change c outside of this method !!
				c = in.read();
			}
		}
		return (c == '\n');
	}

	/**
	 * @return true if the given character indicates end of file
	 */
	protected boolean isEndOfFile(int c) {
		return c == ExtendedBufferedReader.END_OF_STREAM;
	}
	
	public String [][] toArray (InputStreamReader reader) throws FlatReaderException {
		in = new ExtendedBufferedReader (reader);
		ArrayList<String[]> records = new ArrayList<String[]> ();
		String[] values;
		String[][] ret = null;
		while ((values = getLine ()) != null) {
			records.add(values);
		}
		if (records.size() > 0) {
			ret = new String[records.size()][];
			records.toArray(ret);
		}
		return ret;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

}
