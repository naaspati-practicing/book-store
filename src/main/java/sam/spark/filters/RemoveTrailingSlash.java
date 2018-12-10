package sam.spark.filters;

import spark.Filter;
import spark.Request;
import spark.Response;

public class RemoveTrailingSlash implements Filter {

	@Override
	public void handle(Request req, Response res) throws Exception {
		String s = req.uri();
		if(s.length() > 1 && s.charAt(s.length() - 1) == '/')
			res.redirect(s.substring(0, s.length() - 1));
	}

}
