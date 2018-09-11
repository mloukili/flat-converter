package com.bluenimble.flat.reader.impls.xml;

import java.io.IOException;
import java.io.OutputStream;

import com.bluenimble.flat.reader.impls.ExtendedBufferedReader;
import com.bluenimble.flat.reader.impls.FlatToken;

public class CsvToXmlConverter extends FlatToXmlConverter {

	private static final long serialVersionUID = -8662241011805116148L;

	public CsvToXmlConverter (OutputStream output) {
		super (output);
	}

	public CsvToXmlConverter () {
		super ();
	}

	protected FlatToken nextToken (FlatToken tkn) throws IOException {
		wsBuf.clear(); // resuse

		// get the last read char (required for empty line detection)
		int lastChar = in.readAgain();

		// read the next char and set eol
		/*
		 * note: unfourtunately isEndOfLine may consumes a character silently.
		 * this has no effect outside of the method. so a simple workaround is
		 * to call 'readAgain' on the stream... uh: might using objects instead
		 * of base-types (jdk1.5 autoboxing!)
		 */
		int c = in.read();
		boolean eol = isEndOfLine(c);
		c = in.readAgain();

		// empty line detection: eol AND (last char was EOL or beginning)
		while (strategy.isIgnoreEmptyLines()
				&& eol
				&& (lastChar == '\n' || lastChar == ExtendedBufferedReader.UNDEFINED)
				&& !isEndOfFile(lastChar)) {
			// go on char ahead ...
			lastChar = c;
			c = in.read();
			eol = isEndOfLine(c);
			c = in.readAgain();
			// reached end of file without any content (empty line at the end)
			if (isEndOfFile(c)) {
				tkn.setType (FlatToken.TT_EOF);
				return tkn;
			}
		}

		// did we reached eof during the last iteration already ? TT_EOF
		if (isEndOfFile(lastChar)
				|| (lastChar != strategy.getDelimiter() && isEndOfFile(c))) {
			tkn.setType (FlatToken.TT_EOF);
			return tkn;
		}

		// important: make sure a new char gets consumed in each iteration
		while (!tkn.isReady ()) {
			// ignore whitespaces at beginning of a token
			while (isWhitespace(c) && !eol) {
				wsBuf.append((char) c);
				c = in.read();
				eol = isEndOfLine(c);
			}
			// ok, start of token reached: comment, encapsulated, or token
			if (c == strategy.getCommentStart ()) {
				// ignore everything till end of line and continue (incr
				// linecount)
				in.readLine ();
				tkn = nextToken (tkn.reset ());
			} else if (c == strategy.getDelimiter ()) {
				// empty token return TT_TOKEN("")
				tkn.setType (FlatToken.TT_TOKEN);
				tkn.setReady (true);
			} else if (eol) {
				// empty token return TT_EORECORD("")
				// noop: tkn.content.append("");
				tkn.setType (FlatToken.TT_EORECORD);
				tkn.setReady (true);
			} else if (c == strategy.getEncapsulator()) {
				// consume encapsulated token
				encapsulatedTokenLexer (tkn, c);
			} else if (isEndOfFile (c)) {
				// end of file return TT_EOF()
				// noop: tkn.content.append("");
				tkn.setType (FlatToken.TT_EOF);
				tkn.setReady (true);
			} else {
				// next token must be a simple token
				// add removed blanks when not ignoring whitespace chars...
				if (!strategy.isIgnoreLeadingWhitespaces()) {
					tkn.append (wsBuf);
				}
				simpleTokenLexer (tkn, c);
			}
		}
		return tkn;
	}
	
}
