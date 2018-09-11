package com.bluenimble.flat.writer.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bluenimble.flat.writer.FlatWriterException;
import com.bluenimble.flat.writer.impls.xml.XmlToFixedLengthConverter;

public class ConvertXmlToFixedLength {
	
	public static void main (String [] args) throws IOException, FlatWriterException {
		XmlToFixedLengthConverter writer = 
			new XmlToFixedLengthConverter (new FileOutputStream (new File ("files/addresses-fl-out.csv")));
		writer.getStrategy ().setLengths (new int [] {15, 30, 50});
		writer.write (new InputStreamReader(new FileInputStream (new File ("files/addresses.xml"))));
	}
	
}
