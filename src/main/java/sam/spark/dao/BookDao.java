package sam.spark.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookDao implements AutoCloseable, Iterable<Book> {
	private Map<String, Book> books = new ConcurrentHashMap<>(); 

	public BookDao(Path savePath) throws IOException {
		try(BufferedReader reader = Files.newBufferedReader(savePath)) {
			List<String> columnNames = Arrays.asList(reader.readLine().split("\t"));

			int title = columnNames.indexOf("title");
			int author = columnNames.indexOf("author");
			int isbn = columnNames.indexOf("isbn");

			reader.lines()
			.map(s -> s.split("\t"))
			.forEach(s -> books.put(s[isbn], new Book(s[title], s[author], s[isbn])));
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		// to save new books
	}

	@Override
	public Iterator<Book> iterator() {
		return books.values().iterator();
	}
	public Book getByIsbn(String isbn) {
		return books.get(isbn);
	}


}
