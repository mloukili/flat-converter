package com.bluenimble.flat.writer.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.bluenimble.flat.writer.FlatWriterException;
import com.bluenimble.flat.writer.impls.xml.XmlToCsvConverter;

public class ToTxt {
	
	public static void main (String [] args) throws IOException, FlatWriterException {
		XmlToCsvConverter writer = 
			new XmlToCsvConverter (
				new FileOutputStream (new File (args[1]))
			);
		writer.setCharset(Charset.forName("UTF-8"));
		writer.getStrategy().setEscape ('\\');
		writer.write (new InputStreamReader(new FileInputStream (new File (args[0]))));
	}
	
}
