package trio.core;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


public final class TrioFacadeFactory {
	private static final Logger log = Logger.getLogger("TrioLogging");
	
	private final String host;
	private final String method;
	
	public TrioFacadeFactory() throws IOException {
		Properties  prop  = new Properties();
		InputStream input = new FileInputStream("config/server.properties");
		prop.load(input);
		input.close();
		
		String method = prop.getProperty("method");
		this.method = method;
		log.warning("Connection method: " + method);
		
		String host = prop.getProperty("host");
		this.host = host;
		log.warning("Server address: " + host);
	}
	
	public TrioFacade getTrioFacade() {
		switch (method) {
			case "http":
				return getHttpTrioFacade();
			case "rmi":
				return getRMITrioFacade();
			default:
				throw new RuntimeException("Unsupported connection method: " + method);
		}
	}
	
	public TrioFacade getHttpTrioFacade() {
		return new HttpTrioFacade(host + "/Trio");
	}
	
	public TrioFacade getRMITrioFacade() {
		try {
			return (TrioFacade) getInitialContext().lookup("Trio/TrioFacadeImpl!trio.core.TrioFacade");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Context getInitialContext() throws NamingException {
		Properties prop = new Properties();
		prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		prop.put(Context.PROVIDER_URL, "http-remoting://" + host);
		prop.put("jboss.naming.client.ejb.context", true);
		return new InitialContext(prop);
	}
}
