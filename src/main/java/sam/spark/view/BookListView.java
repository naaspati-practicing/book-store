package sam.spark.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;

import sam.spark.dao.Book;
import sam.spark.extra.Constants;
import spark.Request;
import spark.Response;
import spark.Spark;

public class BookListView extends ViewRoot {
	private Iterable<Book> books;

	public BookListView(Iterable<Book> books) {
		this.books = books;
	}
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		if(books == null) {
			Spark.halt(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return super.handle(request, response);
	}

	@Override
	protected Map<String, Object> model() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("books", books);
		return map;
	}
	@Override
	protected String templatePath() {
		return Constants.VELOCITY_DIR.concat("books_list.vm");
	}
	@Override
	protected Object asJson() {
		JSONArray array = new JSONArray();
		for (Book book : books) 
			array.put(toJson(book));
		return array;
	} 
}
