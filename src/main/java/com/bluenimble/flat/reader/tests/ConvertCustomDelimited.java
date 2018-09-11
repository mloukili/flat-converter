package com.beesphere.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.beesphere.flat.reader.FlatReaderException;
import com.beesphere.flat.reader.impls.xml.CsvToXmlConverter;

public class ConvertCustomDelimited {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		CsvToXmlConverter parser = 
			new CsvToXmlConverter (new FileOutputStream (new File ("files/adv_addresses.xml")));
		parser.setFirstLineAsHeader (true);
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setIgnoreTrailingWhitespaces(true);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setDelimiter (';');
		parser.getStrategy().setEncapsulator('_');
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/adv_addresses.csv"))));
	}
	
}
