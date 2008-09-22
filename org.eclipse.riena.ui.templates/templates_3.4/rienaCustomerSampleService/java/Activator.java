package $packageName$;

import java.util.Hashtable;

import org.eclipse.riena.communication.core.publisher.RSDPublisherProperties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The Customer Service sample shows how to publish an OSGi Services.
 * 
 * This sample Activator registers the ICustomerService OSGi Service. This service
 * becomes published by the Riena Communication Hessian Publisher
 * 
 */
public class Activator implements BundleActivator {
	private ServiceRegistration customerServiceReg;

	public void start(BundleContext context) throws Exception {
		// create hessian service
		CustomerService cs = new CustomerService();
		Hashtable<String, String> properties = new Hashtable<String, String>(3);

		properties.put(RSDPublisherProperties.PROP_IS_REMOTE, Boolean.TRUE.toString());
		properties.put(RSDPublisherProperties.PROP_REMOTE_PROTOCOL, "hessian");
		properties.put(RSDPublisherProperties.PROP_REMOTE_PATH, "/CustomerService");

		customerServiceReg = context.registerService(ICustomerService.class.getName(), cs, properties);

		// as an alternative
		// Publish.service(ICustomerService.class.getName()).usingPath("/CustomerService").withProtocol("hessian").andStart(context);

	}

	public void stop(BundleContext context) throws Exception {
		customerServiceReg.unregister();
		customerServiceReg = null;
	}

}
