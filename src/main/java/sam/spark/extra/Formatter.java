package sam.spark.extra;

import java.text.MessageFormat;

public class Formatter {
	public String format(String format, Object...args) {
		return MessageFormat.format(format, args);
	}

}
