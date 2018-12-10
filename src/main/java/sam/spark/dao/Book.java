package sam.spark.dao;

import java.util.Objects;

public class Book {
	private final String title, author, isbn;

	public Book(String title, String author, String isbn) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		return author;
	}
	public String getIsbn() {
		return isbn;
	}
	
	@Override
	public String toString() {
		return "Book [title=" + title + ", author=" + author + ", isbn=" + isbn + "]";
	}
	@Override
	public int hashCode() {
		return isbn.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		Book other = (Book) obj;
		
		return Objects.equals(author, other.author) && 
				Objects.equals(isbn, other.isbn) && 
				Objects.equals(title, other.title);
	}
}
