package com.bluenimble.flat.writer.impls.xml;

import java.io.IOException;
import java.io.OutputStream;

import com.bluenimble.flat.lang.LangUtils;
import com.bluenimble.flat.writer.FlatWriterException;

public class XmlToFixedLengthConverter extends SaxFlatWriter {
	
	private static final long serialVersionUID = 4751069818502184787L;

	public XmlToFixedLengthConverter (String saxParserClassFactory, OutputStream output) throws FlatWriterException {
		super (saxParserClassFactory, output);
	}
	
	public XmlToFixedLengthConverter (OutputStream output) throws FlatWriterException {
		super (output);
	}

	protected String onCell (String cellContent, int index) throws IOException {
		if (strategy.getLengths () == null) {
			throw new UnsupportedOperationException ("lengths not set");
		}
		int length = 100;
		try {
			length = strategy.getLengths () [index];
		} catch (ArrayIndexOutOfBoundsException aiobex) {
			return null;
		}
		if (cellContent.length () < length) {
			cell.append (cellContent);
			for (int i = 0; i < length - cellContent.length (); i++) {
				cell.append (LangUtils.SPACE);
			}
			cellContent = cell.toString ();
		} else if (cellContent.length () > length) {
			cellContent = cellContent.substring (0, length);
		}
		return cellContent;
	}

}
