package com.beesphere.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.beesphere.flat.reader.FlatReaderException;
import com.beesphere.flat.reader.impls.xml.CsvToXmlConverter;

public class ConvertCsvAddresses {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		CsvToXmlConverter parser = 
			new CsvToXmlConverter (new FileOutputStream (new File ("files/addresses.xml")));
		parser.setFirstLineAsHeader (true);
		parser.setCharset(Charset.forName("UTF-8"));
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setDelimiter (',');
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/addresses.csv"))));
	}
	
}
