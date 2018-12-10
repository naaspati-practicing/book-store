package sam.spark.extra;

import java.util.concurrent.Callable;

public class Utils {
	public static <E> E noError(Callable<E> call) {
		try {
			return call.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
