package com.beesphere.flat.writer;

import java.io.InputStreamReader;
import java.io.Serializable;

public interface FlatWriter extends Serializable {
	void write (InputStreamReader input) throws FlatWriterException;
	void reset ();
}
