package com.bluenimble.flat.reader.impls.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import com.bluenimble.flat.lang.LangUtils;
import com.bluenimble.flat.reader.FlatReaderException;
import com.bluenimble.flat.reader.impls.AbstractFlatReader;

public abstract class FlatToXmlConverter extends AbstractFlatReader {
	
	private static final long serialVersionUID = -1202393013921179573L;

	protected boolean omitXmlDeclaration;
	protected boolean firstLineAsHeader;
	protected boolean ignoreEmptyCells = true;
	protected String namespace;
	protected String [] xmlns;

	protected Map<String, String> tags = new HashMap<String, String> ();
	
	protected OutputStream output;
	protected Writer writer;
	protected int recordNumber;
	protected StringBuilder buffer;
	
	protected FlatToXmlConverter (OutputStream output) {
		this.output = output;
	}

	protected FlatToXmlConverter () {
		this (null);
	}

	@Override
	protected void visitStart () throws FlatReaderException {
		if (output == null) {
			output = System.out;
		}
		if (buffer == null) {
			buffer = new StringBuilder ();
		}
		writer = new OutputStreamWriter (output, charset);
		try {
			if (!omitXmlDeclaration) {
				writer.write (LangUtils.XML_DECLARATION_START);
				writer.write (charset.name ());
				writer.write (LangUtils.XML_DECLARATION_START_END);
			}
			writer.write (LangUtils.LESS);
			writeTag (LangUtils.ROOT, -1, -1);
			if (xmlns != null && xmlns.length > 0) {
				for (String xn : xmlns) {
					writer.write (LangUtils.SPACE);
					writer.write (xn);
				}
			}
			writer.write (LangUtils.GREATER);
		} catch (IOException e) {
			throw new FlatReaderException (e);
		}
	}

	@Override
	protected void visitEnd () throws FlatReaderException {
		try {
			writer.write (LangUtils.LESS);writer.write (LangUtils.SLASH);writeTag (LangUtils.RS, -1, -1);writer.write (LangUtils.GREATER);
			writer.write (LangUtils.LESS);writer.write (LangUtils.SLASH);writeTag (LangUtils.ROOT, -1, -1);writer.write (LangUtils.GREATER);
			writer.flush ();
		} catch (IOException e) {
			throw new FlatReaderException (e);
		}
	}

	@Override
	protected void visitLine (String[] line, int ln) throws FlatReaderException {
		try {
			if (recordNumber == 0) {
				if (firstLineAsHeader) {
					writer.write (LangUtils.LESS);writeTag (LangUtils.HS, recordNumber, -1);writer.write (LangUtils.GREATER);
					writeLine (line, recordNumber, null, LangUtils.H);
					writer.write (LangUtils.LESS);writer.write (LangUtils.SLASH);writeTag (LangUtils.HS, recordNumber, -1);writer.write (LangUtils.GREATER);
					writer.write (LangUtils.LESS);writeTag (LangUtils.RS, recordNumber, -1);writer.write (LangUtils.GREATER);
				} else {
					writer.write (LangUtils.LESS);writeTag (LangUtils.RS, recordNumber, -1);writer.write (LangUtils.GREATER);
					writeLine (line, recordNumber, LangUtils.R, LangUtils.C);
				}
			} else {
				writeLine (line, recordNumber, LangUtils.R, LangUtils.C);
			}
		} catch (IOException e) {
			throw new FlatReaderException (e);
		}
		recordNumber++;
	}
	
	@Override
	public void reset () {
		super.reset ();
		output = null;
		writer = null;
		omitXmlDeclaration = false;
		firstLineAsHeader = false;
		ignoreEmptyCells = true;
		recordNumber = 0;
		namespace = null;
		xmlns = null;
		buffer = null;
	}
	
	protected void writeLine (String [] line, int recordNumber, String tag, String cellTag) throws IOException {
		if (tag != null) {
			writer.write (LangUtils.LESS);writeTag (tag, recordNumber, -1);writer.write (LangUtils.GREATER);
		}
		for (int i = 0; i < line.length; i++) {
			String cell = line [i];
			if (ignoreEmptyCells && cell.trim ().equals (LangUtils.EMPTY)) {
				continue;
			}
			writer.write (LangUtils.LESS);writeTag (cellTag, recordNumber, i);writer.write (LangUtils.GREATER);
			writer.write (StringEscapeUtils.escapeXml10 (cell));
			writer.write (LangUtils.LESS);writer.write (LangUtils.SLASH);writeTag (cellTag, recordNumber, i);writer.write (LangUtils.GREATER);
		}
		if (tag != null) {
			writer.write (LangUtils.LESS);writer.write (LangUtils.SLASH);writeTag (tag, recordNumber, -1);writer.write (LangUtils.GREATER);
		}
	}
	
	protected void writeTag (String name, int recordNumber, int column) throws IOException {
		if (name == null) {
			return;
		}
		if (tags == null) {
			writer.write (name);
			return;
		}
		if (namespace != null) {
			writer.write (namespace);
			writer.write (LangUtils.COLON);
		}
		if (tags.get (name) != null) {
			writer.write (tags.get (name));
		} else {
			writer.write (name);
		}
		if (name.equals (LangUtils.H) || name.equals (LangUtils.C)) {
			writer.write (LangUtils.UNDERSCORE);writer.write (String.valueOf (column));
		}
		writer.flush ();
	}
	
	public void renameTag (String name, String newName) {
		tags.put (name, newName);
	}

	public boolean isOmitXmlDeclaration() {
		return omitXmlDeclaration;
	}

	public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
		this.omitXmlDeclaration = omitXmlDeclaration;
	}

	public boolean isFirstLineAsHeader() {
		return firstLineAsHeader;
	}

	public void setFirstLineAsHeader(boolean firstLineAsHeader) {
		this.firstLineAsHeader = firstLineAsHeader;
	}

	public boolean isIgnoreEmptyCells() {
		return ignoreEmptyCells;
	}

	public void setIgnoreEmptyCells(boolean ignoreEmptyCells) {
		this.ignoreEmptyCells = ignoreEmptyCells;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String[] getXmlns() {
		return xmlns;
	}

	public void setXmlns(String[] xmlns) {
		this.xmlns = xmlns;
	}

	public StringBuilder getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuilder buffer) {
		this.buffer = buffer;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

}
