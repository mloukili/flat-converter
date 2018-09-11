package com.bluenimble.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.bluenimble.flat.reader.FlatReaderException;
import com.bluenimble.flat.reader.impls.xml.CsvToXmlConverter;

public class ConvertTxt {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		CsvToXmlConverter parser = 
			new CsvToXmlConverter (new FileOutputStream (new File ("files/out-txt.xml")));
		parser.setFirstLineAsHeader (true);
		parser.setCharset(Charset.forName("UTF-8"));
		//parser.setDataCharset(Charset.forName("iso-8859-1"));
		parser.setOmitXmlDeclaration(true);
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setDelimiter (',');
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/in.txt"))));
	}
	
}
