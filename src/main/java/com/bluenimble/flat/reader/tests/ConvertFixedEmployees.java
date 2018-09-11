package com.bluenimble.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bluenimble.flat.reader.FlatReaderException;
import com.bluenimble.flat.reader.impls.xml.FixedLengthToXmlConverter;

public class ConvertFixedEmployees {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		FixedLengthToXmlConverter parser = 
			new FixedLengthToXmlConverter (
					new FileOutputStream (new File ("files/employees.xml")));
		parser.setFirstLineAsHeader (true);
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setLengths (new int [] {5, 7, 2});
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/employees.csv"))));
	}
	
}
