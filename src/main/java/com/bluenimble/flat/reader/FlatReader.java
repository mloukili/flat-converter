package com.beesphere.flat.reader;

import java.io.InputStreamReader;
import java.io.Serializable;

public interface FlatReader extends Serializable {
	void read (InputStreamReader input) throws FlatReaderException;
	void reset ();
}
