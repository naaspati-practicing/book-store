package sam.spark.dao;

public class BookUtils {
	
	public String mediumCover(Book book) {
		return "http://covers.openlibrary.org/b/isbn/" + book.getIsbn() + "-M.jpg";
	}
	public String largeCover(Book book) {
		return "http://covers.openlibrary.org/b/isbn/" + book.getIsbn() + "-L.jpg";
	}
}
