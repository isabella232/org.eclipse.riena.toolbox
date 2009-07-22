package ${package};

import java.util.Hashtable;

import org.eclipse.riena.communication.core.publisher.RSDPublisherProperties;
import ${package.common}.ICustomerSearch;
import ${package.common}.ICustomers;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class CustomersServiceActivator implements BundleActivator {
	private static final String REMOTE_PROTOCOL_HESSIAN = "hessian"; 

	private CustomersService customers;
	private ServiceRegistration customersSearchRegistration, customersRegistration;

	public CustomersServiceActivator() {
		super();
		
		customers = new CustomersService();
	}

	public void start(BundleContext context) throws Exception {
		Hashtable<String, String> properties = new Hashtable<String, String>(3);

		properties.put(RSDPublisherProperties.PROP_IS_REMOTE, Boolean.TRUE.toString());
		properties.put(RSDPublisherProperties.PROP_REMOTE_PROTOCOL, REMOTE_PROTOCOL_HESSIAN);
		properties.put(RSDPublisherProperties.PROP_REMOTE_PATH, "/CustomerSearchWS"); 
		customersSearchRegistration = context.registerService(ICustomerSearch.class.getName(), customers, properties);
		

		properties.put(RSDPublisherProperties.PROP_IS_REMOTE, Boolean.TRUE.toString());
		properties.put(RSDPublisherProperties.PROP_REMOTE_PROTOCOL, REMOTE_PROTOCOL_HESSIAN);
		properties.put(RSDPublisherProperties.PROP_REMOTE_PATH, "/CustomersWS"); 
		customersRegistration = context.registerService(ICustomers.class.getName(), customers, properties);
	}

	public void stop(BundleContext context) throws Exception {
		customersSearchRegistration.unregister();
		customersRegistration.unregister();
		
		customersSearchRegistration = customersRegistration = null;
		
		customers = null;
	}
}
