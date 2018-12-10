package sam.spark.view;

import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_REFERENCES_STRICT;
import static org.eclipse.jetty.http.HttpStatus.NOT_ACCEPTABLE_406;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;
import static sam.spark.extra.Constants.APPLICATION_JSON;
import static sam.spark.extra.Constants.DEFAULT_LOCALE;
import static sam.spark.extra.Constants.LOCALE;
import static sam.spark.extra.Constants.ROLE_ADMIN;
import static sam.spark.extra.Constants.TEXT_HTML;
import static sam.spark.extra.Constants.TEXT_JSON;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import sam.logging.filter.ANSI;
import sam.main.Paths;
import sam.spark.dao.Book;
import sam.spark.dao.BookUtils;
import sam.spark.extra.Constants;
import sam.spark.extra.Formatter;
import sam.spark.extra.Getter;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

public abstract class ViewRoot implements Route {
	private static final Properties PROPERTIES = new Properties();

	static {
		PROPERTIES.setProperty(RUNTIME_REFERENCES_STRICT, "true");
		PROPERTIES.setProperty(RESOURCE_LOADER, "class");
		PROPERTIES.setProperty("class."+RESOURCE_LOADER+".class", ClasspathResourceLoader.class.getName());

		if(Log.getLogger(ViewRoot.class).isDebugEnabled())
			PROPERTIES.setProperty(RUNTIME_LOG.concat(".name"), "view.root.velocity");
	}

	private final VelocityTemplateEngine v = new VelocityTemplateEngine(new VelocityEngine(PROPERTIES)); 

	public synchronized  String render(ModelAndView mv) {
		return v.render(mv);
	}
	@Override
	public Object handle(Request request, Response response) throws Exception {
		String accept = request.headers(HttpHeader.ACCEPT.asString());

		if(accept.contains("*/*") || accept.contains(TEXT_HTML)) {
			Map<String, Object> model = model();
			if(model == null)
				model = new HashMap<>();
			model.put("msg", messages(request));
			model.put("paths", new Paths());
			model.put("bookUtils", new BookUtils());
			model.put("formatter", new Formatter());
			model.put("current_user", Optional.ofNullable(request.session(false)).map(s -> s.attribute(Constants.CURRENT_USER)).orElse(null));
			model.put("request_url", request.pathInfo());
			
			System.out.println(ANSI.red("current_user: "+model.get(Constants.CURRENT_USER)));
			
			return render(new ModelAndView(model, templatePath()));	
		} 

		if(!accept.contains(TEXT_JSON) && !accept.contains(APPLICATION_JSON)){
			Spark.halt(NOT_ACCEPTABLE_406);
			return null;
		}
		if(!request.raw().isUserInRole(ROLE_ADMIN)){
			Spark.halt(UNAUTHORIZED_401);
			return null;
		}

		Object o = Objects.requireNonNull(asJson());
		if(o instanceof JSONObject || o instanceof JSONArray)
			return asJson().toString();

		throw new IllegalArgumentException("must be: o instanceof JSONObject || o instanceof JSONArray, found: "+o.getClass());
	}
	private Getter messages(Request req) {
		ResourceBundle bundle = ResourceBundle.getBundle("localization/messages", locale(req));
		return bundle::getString;
	}
	public Locale locale(Request req) {
		Session s = req.session(false);
		Locale c = s == null ? null : s.attribute(LOCALE);
		return c == null ? DEFAULT_LOCALE : c;
	}

	protected JSONObject toJson(Book book) {
		JSONObject c = new JSONObject();
		c.put("title", book.getTitle());
		c.put("author", book.getAuthor());
		c.put("isbn", book.getIsbn());

		return c;
	}

	protected abstract Map<String, Object> model();
	protected abstract String templatePath();
	/**
	 * must return a {@link JSONArray} or {@link JSONObject}
	 * @return
	 */
	protected abstract Object asJson();
}
