package com.beesphere.flat.writer.impls.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.beesphere.flat.lang.LangUtils;
import com.beesphere.flat.reader.impls.FlatStrategy;
import com.beesphere.flat.writer.FlatWriter;
import com.beesphere.flat.writer.FlatWriterException;

public abstract class SaxFlatWriter extends DefaultHandler implements FlatWriter {
	
	private static final long serialVersionUID = 3469027812826957334L;

	private static final SAXParserFactory DEFAULT_FACTORY = SAXParserFactory.newInstance();
	
	protected transient SAXParserFactory factory;
	protected transient SAXParser saxParser;
	
	protected Charset charset = LangUtils.DEFAULT_CHARSET;
	protected FlatStrategy strategy = FlatStrategy.DEFAULT_STRATEGY;
	
	protected OutputStream output;
	protected Writer writer;
	
	protected boolean endLine;
	protected boolean startCell;
	protected boolean firstCell;
	
	protected String currentTag;
	protected StringBuilder cell;
	protected int currCell;
	
	public SaxFlatWriter (String saxParserClassFactory, OutputStream output) throws FlatWriterException {
		super ();
		if (output == null) {
			this.output = System.out;
		}
		this.output = output;
		try {
			if (saxParserClassFactory != null) {
				factory = (SAXParserFactory)Thread.currentThread().getContextClassLoader().loadClass (saxParserClassFactory).newInstance ();
			} else {
				factory = DEFAULT_FACTORY;
			}
			factory.setNamespaceAware(true);
			saxParser = factory.newSAXParser ();
		} catch (Throwable e) {
			throw new FlatWriterException (e);
		} 
	}
	
	public SaxFlatWriter (OutputStream output) throws FlatWriterException {
		this (null, output);
	}

	public SaxFlatWriter () throws FlatWriterException {
		this (null);
	}

	@Override
	public void reset () {
		startCell = false;
		output = null;
		writer = null;
		charset = LangUtils.DEFAULT_CHARSET;
		saxParser.reset ();
		cell.setLength (0);
		cell = null;
		currentTag = null;
		firstCell = false;
		currCell = 0;
	}
		
	@Override
	public void write (InputStreamReader reader) throws FlatWriterException {
		// call parse
		if (output == null) {
			output = System.out;
		}
		writer = new OutputStreamWriter (output, charset);
		cell = new StringBuilder ();
		
		try {
			saxParser.parse (new InputSource(reader), this);
		} catch (IOException e) {
			throw new FlatWriterException (e);
		} catch (SAXException e) {
			throw new FlatWriterException (e);
		} 
		reset ();
	}
	/**
	 * @see org.xml.sax.ContentHandler#startElement()
	 */
	public void startElement (String nsURI, String localName, // local name
			String qName, // qualified name
			Attributes attrs)
		throws SAXException {
		try {
			if (endLine) {
				writer.write (LangUtils.ENDLN);
			}
		} catch (IOException e) {
			throw new SAXException (e);
		}
		String eName = localName; // element name
		if (LangUtils.EMPTY.equals (eName)) {
			eName = qName; // namespaceAware = false
		}
		
		firstCell = isLine (currentTag);
		
		startCell = isCell (eName);
		
		if (firstCell) {
			currCell = 0;
		} else if (startCell) {
			currCell++;
		}
		
		currentTag = eName;
		endLine = false;
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement (String uri, String localName, String tagName)
		throws SAXException {
		String eName = localName; // element name
		if (LangUtils.EMPTY.equals (eName)) {
			eName = tagName; // namespaceAware = false
		}
		try {
			if (isCell (eName)) {
				endCell ();
				startCell = false;
			} else {
				endLine = isLine (eName);
			}
			writer.flush ();
		} catch (IOException e) {
			throw new SAXException (e);
		}
		
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#characters()
	 */
	public void characters (char [] ch, int start, int length) throws SAXException {
		try {
			if (startCell) {
				startCell (ch, start, length);
			}
		} catch (IOException e) {
			throw new SAXException (e);
		}
	}
	
	private void startCell (char [] ch, int start, int length) throws IOException {
		cell.append (ch, start, length);
	}
	
	private void endCell () throws IOException {
		String cellContent = cell.toString ();
		cell.setLength (0);
		cellContent = onCell (cellContent, currCell);
		if (cellContent != null) {
			writer.write (cellContent);
		}
		cell.setLength (0);
	}
	
	protected abstract String onCell (String cellContent, int index) throws IOException;
	
	private boolean isCell (String eName) {
		return eName.startsWith (LangUtils.H_) || eName.startsWith (LangUtils.C_);
	}
	
	private boolean isLine (String eName) {
		return LangUtils.HS.equals (eName) || LangUtils.R.equals (eName);
	}

	public void setValidating (boolean validating) {
		factory.setValidating (validating);
	}

	public boolean isValidating () {
		return factory.isValidating ();
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument () throws SAXException {
		// nothing
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument () throws SAXException {
		try {
			writer.flush ();
		} catch (IOException e) {
			throw new SAXException (e);
		}
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public FlatStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(FlatStrategy strategy) {
		this.strategy = strategy;
	}

}
