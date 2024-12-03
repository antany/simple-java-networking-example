package ca.antany.network.common.util;

import java.io.Closeable;
import java.io.IOException;

import ca.antany.network.common.exception.InitializationError;

public class SafeClose {

	public static void close(Closeable obj) {
		try {
			if(obj!=null) {
				obj.close();
			}
		}catch(IOException e) {
			throw new InitializationError(e.getMessage(), e);
		}
	}
}
