package sam.main;

import sam.spark.extra.Getter;

public class Paths implements Getter {
	public static final String AUTH = "/auth";
	public static final String HOME = "/";
	public static final String INDEX = "/index";
	public static final String BOOKS = "/books";
	public static final String BOOK_BY_ISBN = "/book/";
	public static final String LOGIN = AUTH.concat("/login");
	public static final String LOGOUT = "/logout";

	@Override
	public Object get(String key) {
		switch (key.toUpperCase().trim()) {
			case "HOME": return HOME;
			case "INDEX": return INDEX;
			case "BOOKS": return BOOKS;
			case "BOOK_BY_ISBN": return BOOK_BY_ISBN;
			case "LOGIN": return LOGIN;
			case "LOGOUT": return LOGOUT;
			default:
				throw new IllegalArgumentException("unknown key: "+key);
		}
	}
}
