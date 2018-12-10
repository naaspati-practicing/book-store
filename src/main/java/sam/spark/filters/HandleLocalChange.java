package sam.spark.filters;

import java.util.Locale;

import org.eclipse.jetty.http.HttpStatus;

import sam.spark.extra.Constants;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

public class HandleLocalChange implements Filter {
	// private final Map<String, Locale> map = new HashMap<>();

	@Override
	public void handle(Request request, Response response) throws Exception {
		String s = request.queryParams(Constants.LOCALE);
		if(s == null)
			return;
		
		Locale l;
		switch (s.toLowerCase()) {
			case "en":
				l = Locale.ENGLISH;
				break;
			case "de":
				l = Locale.GERMAN;
				break;
			default:
				Spark.halt(HttpStatus.BAD_REQUEST_400, "unknown Locale: "+s);
				return;
				/*  support for more Locale(s)
				 * l = map.get(s.toLowerCase());
				if(l == null) {
					l = Locale.forLanguageTag(s);
					map.put(s.toLowerCase(), l);
				}
				break; 
				 */
		}
		request.session().attribute(Constants.LOCALE, l);
		response.redirect(request.pathInfo());
	}

}
