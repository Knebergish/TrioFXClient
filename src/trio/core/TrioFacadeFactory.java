package trio.core;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;


public final class TrioFacadeFactory {
	//	private static final String HOST = "trio-trio.1d35.starter-us-east-1.openshiftapps.com";
	private static final String HOST = "127.0.0.1:8080";
	
	private TrioFacadeFactory() {
	}
	
	public static TrioFacade getHttpTrioFacade() {
		return new HttpTrioFacade(HOST + "/Trio");
	}
	
	public static TrioFacade getRMITrioFacade() {
		try {
			return (TrioFacade) getInitialContext().lookup("Trio/TrioFacadeImpl!trio.core.TrioFacade");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Context getInitialContext() throws NamingException {
		Properties prop = new Properties();
		prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		prop.put(Context.PROVIDER_URL, "http-remoting://" + HOST);
		prop.put(Context.SECURITY_PRINCIPAL, "temon137");
		prop.put(Context.SECURITY_CREDENTIALS, "12345");
		prop.put("jboss.naming.client.ejb.context", true);
		return new InitialContext(prop);
	}
}
