package sam.spark.view;

import spark.Request;
import spark.Response;
import spark.Route;

public class FrontPage implements Route {

	@Override
	public Object handle(Request request, Response response) throws Exception {
		//FIXME 
		return "this is front page";
	}

}
