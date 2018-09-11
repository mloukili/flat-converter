package com.beesphere.flat.reader.impls;

import java.io.Serializable;

/**
 * CSVStrategy
 * 
 * Represents the strategy for a CSV.
 */
public class FlatStrategy implements Serializable {

	private static final long serialVersionUID = 3473182664316000767L;

	private char delimiter;
	
	// used by Delimited reader
	private char encapsulator;
	private char commentStart;
	private char escape;
	private boolean ignoreLeadingWhitespaces;
	private boolean ignoreTrailingWhitespaces;
	private boolean interpretUnicodeEscapes;
	private boolean ignoreEmptyLines = true;
	
	// used by FixedLength reader
	private boolean ignoreAdditionalFields;
	private int [] lengths;
	private boolean handlingShortLines;
	
	// used internally
	private String sEncapsulator;
	private String sEncapsulatorReplace;

	// -2 is used to signal disabled, because it won't be confused with
	// an EOF signal (-1), and because \ufffe in UTF-16 would be
	// encoded as two chars (using surrogates) and thus there should never
	// be a collision with a real text char.
	public static char COMMENTS_DISABLED = (char) -2;
	public static char ESCAPE_DISABLED = (char) -2;

	public static FlatStrategy DEFAULT_STRATEGY = new FlatStrategy(',', '"',
			COMMENTS_DISABLED, ESCAPE_DISABLED, true, true, false, true);
	public static FlatStrategy EXCEL_STRATEGY = new FlatStrategy(',', '"',
			COMMENTS_DISABLED, ESCAPE_DISABLED, false, false, false, false);
	public static FlatStrategy TDF_STRATEGY = new FlatStrategy('\t', '"',
			COMMENTS_DISABLED, ESCAPE_DISABLED, true, true, false, true);

	/**
	 * Customized CSV strategy setter.
	 * 
	 * @param delimiter
	 *            a Char used for value separation
	 * @param encapsulator
	 *            a Char used as value encapsulation marker
	 * @param commentStart
	 *            a Char used for comment identification
	 * @param ignoreLeadingWhitespace
	 *            TRUE when leading whitespaces should be ignored
	 * @param interpretUnicodeEscapes
	 *            TRUE when unicode escapes should be interpreted
	 * @param ignoreEmptyLines
	 *            TRUE when the parser should skip emtpy lines
	 */
	public FlatStrategy (char delimiter, char encapsulator, char commentStart,
			char escape, boolean ignoreLeadingWhitespace,
			boolean ignoreTrailingWhitespace, boolean interpretUnicodeEscapes,
			boolean ignoreEmptyLines) {
		setDelimiter(delimiter);
		setEncapsulator(encapsulator);
		setCommentStart(commentStart);
		setEscape(escape);
		setIgnoreLeadingWhitespaces(ignoreLeadingWhitespace);
		setIgnoreTrailingWhitespaces(ignoreTrailingWhitespace);
		setUnicodeEscapeInterpretation(interpretUnicodeEscapes);
		setIgnoreEmptyLines(ignoreEmptyLines);
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public char getDelimiter() {
		return this.delimiter;
	}

	public void setEncapsulator(char encapsulator) {
		this.encapsulator = encapsulator;
	}

	public char getEncapsulator() {
		return this.encapsulator;
	}

	public void setCommentStart(char commentStart) {
		this.commentStart = commentStart;
	}

	public char getCommentStart() {
		return this.commentStart;
	}

	public boolean isCommentingDisabled() {
		return this.commentStart == COMMENTS_DISABLED;
	}

	public void setEscape (char escape) {
		this.escape = escape;
		if (escape != ESCAPE_DISABLED) {
			sEncapsulator = String.valueOf (encapsulator);
			sEncapsulatorReplace = String.valueOf (escape) + sEncapsulator;
		}
	}
	
	public String escape (String value) {
		if (getEscape () != FlatStrategy.ESCAPE_DISABLED &&
				value.indexOf (getEncapsulator()) > 0) {
			value = replace (value, sEncapsulator, sEncapsulatorReplace);
		}
		return value;
	}

	public char getEscape() {
		return this.escape;
	}

	public void setIgnoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
		this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
	}

	public boolean isIgnoreLeadingWhitespaces() {
		return this.ignoreLeadingWhitespaces;
	}

	public void setIgnoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
		this.ignoreTrailingWhitespaces = ignoreTrailingWhitespaces;
	}

	public boolean isIgnoreTrailingWhitespaces() {
		return this.ignoreTrailingWhitespaces;
	}

	public void setUnicodeEscapeInterpretation(boolean interpretUnicodeEscapes) {
		this.interpretUnicodeEscapes = interpretUnicodeEscapes;
	}

	public boolean isUnicodeEscapeInterpretation() {
		return this.interpretUnicodeEscapes;
	}

	public void setIgnoreEmptyLines(boolean ignoreEmptyLines) {
		this.ignoreEmptyLines = ignoreEmptyLines;
	}

	public boolean isIgnoreEmptyLines() {
		return this.ignoreEmptyLines;
	}

	public int[] getLengths() {
		return lengths;
	}

	public void setLengths(int[] lengths) {
		this.lengths = lengths;
	}
	
	private String replace (String source, String os, String ns) {
        if (source == null) {
            return null;
        }
        int i = 0;
        // Make sure that oldString appears at least once before doing any processing.
        if ((i = source.indexOf(os, i)) >= 0) {
            // Use char []'s, as they are more efficient to deal with.
            char[] sourceArray = source.toCharArray();
            char[] nsArray = ns.toCharArray();
            int oLength = os.length();
            StringBuilder buf = new StringBuilder (sourceArray.length);
            buf.append (sourceArray, 0, i).append(nsArray);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = source.indexOf(os, i)) > 0) {
                buf.append (sourceArray, j, i - j).append(nsArray);
                i += oLength;
                j = i;
            }
            buf.append (sourceArray, j, sourceArray.length - j);
            source = buf.toString();
            buf.setLength (0);
        }
        return source;
	}

	public int getSumOfLengths () {
		if (lengths == null) {
			throw new UnsupportedOperationException ("lengths not set");
		}
		int sum = 0;
		for (int l : lengths) {
			sum += l;
		}
		return sum;
	}

	public boolean isIgnoreAdditionalFields() {
		return ignoreAdditionalFields;
	}

	public void setIgnoreAdditionalFields(boolean ignoreAdditionalFields) {
		this.ignoreAdditionalFields = ignoreAdditionalFields;
	}

	public boolean isHandlingShortLines() {
		return handlingShortLines;
	}

	public void setHandlingShortLines(boolean handlingShortLines) {
		this.handlingShortLines = handlingShortLines;
	}
	
}
