package sam.spark.extra;

import java.util.Locale;

import org.eclipse.jetty.http.MimeTypes;

public interface Constants {
	String LOCALE = "locale";
	Locale DEFAULT_LOCALE = Locale.ENGLISH;
	String ROLE_ADMIN = "role";
	String ROLE_USER = "user";
	
	String TEXT_JSON = MimeTypes.Type.TEXT_JSON.asString();
	String APPLICATION_JSON = MimeTypes.Type.APPLICATION_JSON.asString();
	String TEXT_HTML = MimeTypes.Type.TEXT_HTML.asString();
	
	String VELOCITY_DIR = "/velocity/";
	String CURRENT_USER = "logged_in";
}
