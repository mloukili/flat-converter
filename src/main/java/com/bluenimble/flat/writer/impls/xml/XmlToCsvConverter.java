package com.bluenimble.flat.writer.impls.xml;

import java.io.IOException;
import java.io.OutputStream;

import com.bluenimble.flat.lang.LangUtils;
import com.bluenimble.flat.writer.FlatWriterException;

public class XmlToCsvConverter extends SaxFlatWriter {
	
	private static final long serialVersionUID = 4751069818502184787L;

	public XmlToCsvConverter (String saxParserClassFactory, OutputStream output) throws FlatWriterException {
		super (saxParserClassFactory, output);
	}
	
	public XmlToCsvConverter (OutputStream output) throws FlatWriterException {
		super (output);
	}

	public XmlToCsvConverter () throws FlatWriterException {
		super ();
	}

	protected String onCell (String cellContent, int index) throws IOException {
		if (!firstCell) {
			writer.write (strategy.getDelimiter ());
		}
		cellContent = strategy.escape (cellContent);
		if (cellContent.indexOf (strategy.getDelimiter ()) > 0 ||
				cellContent.indexOf (LangUtils.ENDLN) > 0) {
			cellContent = cell.append (strategy.getEncapsulator()).append (cellContent).append (strategy.getEncapsulator()).toString ();
		}
		return cellContent;
	}

}
