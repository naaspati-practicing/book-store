package sam.spark.authentication;

import static sam.main.Paths.LOGOUT;
import static sam.spark.extra.Constants.ROLE_ADMIN;
import static sam.spark.extra.Constants.ROLE_USER;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServerFactory;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.embeddedserver.jetty.EmbeddedJettyServer;
import spark.embeddedserver.jetty.JettyHandler;
import spark.embeddedserver.jetty.JettyServerFactory;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;
/**
 * almost a copy of {@link EmbeddedJettyFactory}
 * @author Sameer
 *
 */
public class MyServerFactory implements EmbeddedServerFactory, JettyServerFactory {
	private final Logger LOGGER = Log.getLogger(getClass());

	private SessionHandler handler;
	private final ConstraintSecurityHandler security = new ConstraintSecurityHandler();
	private final LoginService loginService = new HashLoginService("MyRealm", "realm.properties");
	private StaticFilesConfiguration staticFilesConfiguration;

	public StaticFilesConfiguration getStaticFiles() {
		return staticFilesConfiguration;
	}

	@Override
	public EmbeddedServer create(Routes routeMatcher, StaticFilesConfiguration staticFilesConfiguration, boolean hasMultipleHandler) {
		MatcherFilter matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, false, hasMultipleHandler);
		matcherFilter.init(null);
		this.staticFilesConfiguration = staticFilesConfiguration;

		Constraint constraint = new Constraint();
		constraint.setAuthenticate(true);

		constraint.setRoles(new String[] { ROLE_USER, ROLE_ADMIN});
		ConstraintMapping mapping = new ConstraintMapping();

		mapping.setPathSpec("/auth/*");
		mapping.setConstraint(constraint);

		security.setConstraintMappings(Collections.singletonList(mapping));

		security.setLoginService(loginService);
		security.setRealmName("myrealm");

		handler = new JettyHandler2(matcherFilter);
		security.setHandler(handler);

		handler.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));

		constraint.setName(Constraint.__DIGEST_AUTH);
		security.setAuthenticator(new DigestAuthenticator());
		
		return new EmbeddedJettyServer(this, security);
	}
	
	private class JettyHandler2 extends JettyHandler {

		public JettyHandler2(Filter filter) {
			super(filter);
		}
		@Override
		public void doHandle(String target, Request baseRequest, HttpServletRequest request,
				HttpServletResponse response) throws IOException, ServletException {
			
			if(LOGOUT.equals(baseRequest.getRequestURI()) && baseRequest.getAuthentication() instanceof Authentication.User) {
				security.logout((User) baseRequest.getAuthentication());
				baseRequest.logout();
				request.logout();
			}
			super.doHandle(target, baseRequest, request, response);
		}
	}
	
	@Override
	public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
		Server server;

		if (maxThreads > 0) {
			int max = maxThreads; 
			int min = minThreads > 0 ? minThreads : 8;
			int idleTimeout = threadTimeoutMillis > 0 ? threadTimeoutMillis : 60000;

			server = new Server(new QueuedThreadPool(max, min, idleTimeout));
		} else {
			server = new Server();
		}
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Server created: "+server, (Exception)null);
		return server;
	}
	@Override
	public Server create(ThreadPool threadPool) {
		Server server = threadPool == null ? new Server() : new Server(threadPool);
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Server created: "+server, (Exception)null);
		return server;
	}
}
