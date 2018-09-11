package com.beesphere.flat.lang;

import java.nio.charset.Charset;

public interface LangUtils {
	
	String XML_DECLARATION_START = "<?xml version=\"1.0\" encoding=\"";
	String XML_DECLARATION_START_END = "\"?>";
	
	char ENDLN = '\n';

	String LESS = "<";
	String GREATER = ">";
	String SLASH = "/";
	String UNDERSCORE = "_";
	String EMPTY = "";
	String SPACE = " ";
	String COLON = ":";
	String AMP = "&";
	String APOS = "'";
	String QUOT = "\"";
	
	String ENT_AMP = "&amp;";
	String ENT_LT = "&lt;";
	String ENT_GT = "&gt;";
	String ENT_QUOT = "&quot;";
	String ENT_APOS = "&apos;";
	
	String HS = "hs";
	String H = "h";
	String H_ = "h_";
	String RS = "rs";
	String R = "r";
	String C = "c";
	String C_ = "c_";
	String ROOT = "data";
	String PRINT_SPACE = "  ";
	
	Charset DEFAULT_CHARSET = Charset.forName ("utf-8");
	String [] EMPTY_STRING_ARRAY = new String [0];
}
