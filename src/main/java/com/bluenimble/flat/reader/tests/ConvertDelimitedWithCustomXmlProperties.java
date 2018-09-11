package com.beesphere.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.beesphere.flat.reader.FlatReaderException;
import com.beesphere.flat.reader.impls.xml.CsvToXmlConverter;

public class ConvertDelimitedWithCustomXmlProperties {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		CsvToXmlConverter parser = 
			new CsvToXmlConverter (new FileOutputStream (new File ("files/advxml_addresses.xml")));
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setIgnoreTrailingWhitespaces(true);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setDelimiter (',');
		parser.setOmitXmlDeclaration(true);
		parser.setFirstLineAsHeader(false);
		parser.setXmlns(new String [] {
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
				"xmlns:txt=\"http://www.beesphere.net/xsds/2008/txt\""
				
		});
		parser.setNamespace("txt");
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/addresses.csv"))));
	}
	
}
