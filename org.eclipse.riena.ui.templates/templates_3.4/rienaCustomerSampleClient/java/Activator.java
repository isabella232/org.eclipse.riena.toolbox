package $packageName$;

import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.factory.RemoteServiceFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Customer sample shows to config a "remote" OSGi Services on the base of
 * service end point parameters.
 * 
 */
public class Activator implements BundleActivator {

	private IRemoteServiceRegistration customerServiceReg;

	/**
	 * Creates a RemoteServiceReferences based on Hessian protocol and registers
	 * this as "remote" OSGi Service
	 */
	public void start(final BundleContext context) throws Exception {
		// register hessian proxy for riena remote service
		RemoteServiceFactory rsf = new RemoteServiceFactory();
		Class<?> serviceInterface = ICustomerService.class;
		String url = "http://localhost:8080/hessian/CustomerService";
		String protocol = "hessian";

		customerServiceReg = rsf.createAndRegisterProxy(serviceInterface, url,
				protocol, null);
		
		Inject.service(ICustomerService.class.getName()).into(this).andStart(context);

	}
	
	public void bind(ICustomerService customerService) {
		System.out.println("calling customer search");
		Customer c = new Customer();
		c.setLastName("Skywalker");
		for (Customer customer : customerService
				.findCustomer(c)) {
			System.out.println(customer);
		}
		
	}
	
	public void unbind(ICustomerService customerService) {
		
	}

	/**
	 * unregister and release the "remote" OSGi Service
	 */
	public void stop(BundleContext context) throws Exception {
		if (customerServiceReg != null) {
			customerServiceReg.unregister();
			customerServiceReg = null;
		}
	}

}
