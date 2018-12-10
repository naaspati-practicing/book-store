package sam.logging.filter;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface ANSI {
	public static String black(Object obj) {return "\u001b[30m"+obj+"\u001b[0m";}
	public static String red(Object obj) {return "\u001b[31m"+obj+"\u001b[0m";}
	public static String green(Object obj) {return "\u001b[32m"+obj+"\u001b[0m";}
	public static String yellow(Object obj) {return "\u001b[33m"+obj+"\u001b[0m";}
	public static String blue(Object obj) {return "\u001b[34m"+obj+"\u001b[0m";}
	public static String magenta(Object obj) {return "\u001b[35m"+obj+"\u001b[0m";}
	public static String cyan(Object obj) {return "\u001b[36m"+obj+"\u001b[0m";}
	public static String white(Object obj) {return "\u001b[37m"+obj+"\u001b[0m";}
	public static Object join(String delimeter, Object...args) {
		return Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(delimeter));
	}
}
