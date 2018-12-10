package sam.main;

import static sam.main.Paths.AUTH;
import static sam.main.Paths.BOOKS;
import static sam.main.Paths.BOOK_BY_ISBN;
import static sam.main.Paths.HOME;
import static sam.main.Paths.INDEX;
import static sam.main.Paths.LOGIN;
import static sam.main.Paths.LOGOUT;
import static spark.Spark.awaitInitialization;
import static spark.Spark.before;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.stop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Duration;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import sam.spark.authentication.MyServerFactory;
import sam.spark.dao.BookDao;
import sam.spark.extra.Constants;
import sam.spark.filters.HandleLocalChange;
import sam.spark.view.BookListView;
import sam.spark.view.BookView;
import sam.spark.view.FrontPage;
import spark.Route;
import spark.Session;
import spark.Spark;
import spark.embeddedserver.EmbeddedServers;

public class Main {
	public static final String PUBLIC_DIR = "public"; 

	public static void main(String[] args) throws Exception {
		boolean init = false;
		try(BookDao bookDao = new BookDao(Paths.get("books.tsv"))) {
			
			EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new MyServerFactory());
			staticFiles.location(PUBLIC_DIR);
			staticFiles.expireTime(Duration.ofHours(1).getSeconds());
			port(8080);
			
			init = true;

			// before("*", new RemoveTrailingSlash());
			before("*", new HandleLocalChange());

			final FrontPage front = new FrontPage();
		    Spark.get(HOME, front);
			apply(INDEX, front, Spark::get);

			apply(BOOKS, new BookListView(bookDao), Spark::get);
			apply(BOOK_BY_ISBN.concat(":isbn"), (req, res) -> new BookView(bookDao.getByIsbn(req.params("isbn"))).handle(req, res), Spark::get);
			
			Spark.post(LOGOUT, (req,res) -> {
				String s = req.queryParams("request_url");
				Session ses = req.session(false);
				if(ses != null)
					ses.invalidate();
				res.redirect(s.replace(AUTH, ""));
				return null;
			});
			apply(LOGIN, (req,res) -> {
				req.session().attribute(Constants.CURRENT_USER, Optional.of(req.raw()).map(HttpServletRequest::getUserPrincipal).map(Principal::getName).orElse(null));
				res.redirect(AUTH.concat(req.queryParams("request_url")));
				return null;
			}, Spark::post);
			
			notFound(getPublicResource("404.html"));
			internalServerError(getPublicResource("404.html"));

			awaitInitialization();
			Thread.sleep(200);
			waitForExit();
		} finally {
			if(init) 
				stop();
		}
	}
	private static String getPublicResource(String name) throws IOException {
		try(InputStream is2 = ClassLoader.getSystemResourceAsStream(PUBLIC_DIR+"/"+name);
				InputStreamReader reader = new InputStreamReader(is2);
				BufferedReader bdr = new BufferedReader(reader);
				) {
			return bdr.lines().collect(Collectors.joining("\n"));
		}
	}
	private static void apply(String path, Route route, BiConsumer<String, Route> consumer) {
		consumer.accept(path, route);
		consumer.accept(path.concat("/"), route);
		if(!path.startsWith("/auth"))
			apply("/auth"+path, route, consumer);
	}
	private static void waitForExit() {
		System.out.println("\n\ntype --exit to exit....");

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while(true) {
			if(sc.nextLine().trim().equalsIgnoreCase("--exit"))
				break;
		}
	}

}
