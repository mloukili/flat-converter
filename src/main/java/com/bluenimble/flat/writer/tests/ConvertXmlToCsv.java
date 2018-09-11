package com.bluenimble.flat.writer.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bluenimble.flat.writer.FlatWriterException;
import com.bluenimble.flat.writer.impls.xml.XmlToCsvConverter;

public class ConvertXmlToCsv {
	
	public static void main (String [] args) throws IOException, FlatWriterException {
		XmlToCsvConverter writer = 
			new XmlToCsvConverter (
				new FileOutputStream (new File ("files/addresses.csv"))
			);
		writer.write (new InputStreamReader(new FileInputStream (new File ("files/addresses.xml"))));
	}
	
}
