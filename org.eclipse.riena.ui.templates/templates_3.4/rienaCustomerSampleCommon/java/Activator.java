package $packageName$;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Ping Pong sample shows to config a "remote" OSGi Services on the base of
 * service end point parameters.
 * 
 * This sample Activator registers manually the remote PingPong end point as
 * remote OSGi Service into the ServiceRegistry. This is reached by a simple way
 * using the Riena communication RemoteServiceFactory. #createAndRegisterProxy
 * creates a proxy references for the end point and registers the proxy
 * references as RemoteServiceReference into the Riena communication
 * IRemoteServiceRegistry OSGi Service. The IRemoteServiceRegistry itself
 * registers and manages the RemoteServiceReferences as remote OSGi service
 * within the ServiceRegistry.
 * 
 * 
 */
public class Activator implements BundleActivator {


	public void start(BundleContext context) throws Exception {
	}

	public void stop(BundleContext context) throws Exception {
	}

}
