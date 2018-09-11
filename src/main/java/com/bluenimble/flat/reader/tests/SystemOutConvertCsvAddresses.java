package com.bluenimble.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bluenimble.flat.reader.FlatReaderException;
import com.bluenimble.flat.reader.impls.xml.CsvToXmlConverter;

public class SystemOutConvertCsvAddresses {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		CsvToXmlConverter parser = new CsvToXmlConverter ();
		parser.setFirstLineAsHeader (true);
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setDelimiter (',');
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/addresses.csv"))));
	}
	
}
