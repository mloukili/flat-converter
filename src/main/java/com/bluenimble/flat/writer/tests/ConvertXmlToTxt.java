package com.bluenimble.flat.writer.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.bluenimble.flat.writer.FlatWriterException;
import com.bluenimble.flat.writer.impls.xml.XmlToCsvConverter;

public class ConvertXmlToTxt {
	
	public static void main (String [] args) throws IOException, FlatWriterException {
		XmlToCsvConverter writer = 
			new XmlToCsvConverter (
				new FileOutputStream (new File ("files/out.txt"))
			);
		
		writer.setCharset(Charset.forName("iso-8859-1"));
		writer.getStrategy().setEscape ('\\');
		writer.write (new InputStreamReader(new FileInputStream (new File ("files/addresses.xml"))));
	}
	
}
