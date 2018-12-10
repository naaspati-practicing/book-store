package sam.spark.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;

import sam.spark.dao.Book;
import sam.spark.extra.Constants;
import spark.Request;
import spark.Response;
import spark.Spark;

public class BookView extends ViewRoot {
	private final Book book;

	public BookView(Book book) {
		this.book = book;
	}
	@Override
	public Object handle(Request request, Response response) throws Exception {
		if(book == null) {
			Spark.halt(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return super.handle(request, response);
	}

	@Override
	protected Map<String, Object> model() {
		Map<String, Object> map = new HashMap<>();
		map.put("book", book);
		return map;
	}

	@Override
	protected String templatePath() {
		return Constants.VELOCITY_DIR.concat("book_one.vm");
	}
	@Override
	protected Object asJson() {
		return toJson(book);
	}
}
