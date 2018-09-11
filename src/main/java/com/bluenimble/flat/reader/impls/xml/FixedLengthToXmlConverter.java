package com.bluenimble.flat.reader.impls.xml;

import java.io.IOException;
import java.io.OutputStream;

import com.bluenimble.flat.lang.LangUtils;
import com.bluenimble.flat.reader.FlatReaderException;
import com.bluenimble.flat.reader.impls.FlatToken;

public class FixedLengthToXmlConverter extends FlatToXmlConverter {
	
	private static final long serialVersionUID = -7231559252574798591L;

	public FixedLengthToXmlConverter (OutputStream output) {
		super (output);
	}
	
	public FixedLengthToXmlConverter () {
		super ();
	}

	protected FlatToken nextToken (FlatToken tkn) throws IOException {
		return tkn;
	}

	protected String [] getLine () throws FlatReaderException {
		String [] ret = LangUtils.EMPTY_STRING_ARRAY;
		record.clear ();
		String line;
		try {
			line = in.readLine ();
		} catch (IOException e) {
			throw new FlatReaderException (e, recordNumber, null);
		}
		if (line == null) {
			return null;
		}
		String ln = line.trim ();
		if (strategy.isIgnoreTrailingWhitespaces ()) {
			line = ln;
		}
		if (ln.equals (LangUtils.EMPTY)) {
			if (strategy.isIgnoreEmptyLines ()) {
				return null;
			} else {
				return LangUtils.EMPTY_STRING_ARRAY;
			}
		}
		if (line.charAt (0) == strategy.getCommentStart ()) {
			return LangUtils.EMPTY_STRING_ARRAY;
		}
		int length = line.length ();
		int recordLength = strategy.getSumOfLengths ();
		if (length > recordLength) {
			if (strategy.isIgnoreAdditionalFields()) {
				line = line.substring(0, recordLength);
			} else {
				throw new FlatReaderException(
						"Line size too long than predicted size ("
								+ recordLength + ")", recordNumber, line);
			}
		} else if (length < recordLength) {
			if (strategy.isHandlingShortLines ()) {
				// We can pad this line out
				line += padding (recordLength - line.length(), ' ');
			} else {
				throw new FlatReaderException(
						"Line too short than predicted size ("
								+ recordLength + ")", recordNumber, line);
			}
		}
		int start = 0;
		for (int l : strategy.getLengths ()) {
			record.add (line.substring (start, start + l));
			start += l;
		}
		if (!record.isEmpty()) {
			ret = (String[]) record.toArray (new String [record.size()]);
		}
		return ret;
	}
	
    private String padding (final int repeat, final char padChar) {
		if (repeat < 0) {
			throw new IndexOutOfBoundsException(
					"Cannot pad a negative amount: " + repeat);
		}
		final char[] buf = new char[repeat];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = padChar;
		}
		return new String(buf);
	}

}
