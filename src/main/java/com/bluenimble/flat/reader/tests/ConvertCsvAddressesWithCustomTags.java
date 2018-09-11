package com.bluenimble.flat.reader.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.bluenimble.flat.reader.FlatReaderException;
import com.bluenimble.flat.reader.impls.xml.CsvToXmlConverter;

public class ConvertCsvAddressesWithCustomTags {
	
	public static void main (String [] args) throws IOException, FlatReaderException {
		CsvToXmlConverter parser = 
			new CsvToXmlConverter (new FileOutputStream (new File ("files/addresses.xml")));
		parser.setFirstLineAsHeader (true);
		parser.setCharset(Charset.forName("UTF-8"));
		parser.getStrategy().setIgnoreEmptyLines (false);
		parser.getStrategy().setCommentStart ('#');
		parser.getStrategy().setDelimiter (',');
		Map<String, String> tags = new HashMap<String, String> ();
		tags.put("data", "data");
		tags.put("hs", "headers");
		tags.put("rs", "employees");
		tags.put("r", "employe");
		parser.setTags(tags);
		parser.read (new InputStreamReader(new FileInputStream (new File ("files/addresses.csv"))));
	}
	
}
