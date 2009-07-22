package ${package};

import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.factory.RemoteServiceFactory;
import org.eclipse.riena.communication.core.factory.Register;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.eclipse.riena.core.injector.Inject;

import ${package.common}.ICustomerSearch;
import ${package.common}.Customer;


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
		Register.remoteProxy(ICustomerSearch.class).usingUrl(
		"http://localhost:8080/hessian/CustomerSearchWS")
		.withProtocol("hessian").andStart(context);
		
		Inject.service(ICustomerSearch.class.getName()).into(this).andStart(context);

	}
	
	public void bind(ICustomerSearch customerService) {
		System.out.println("calling customer search");
		Customer c = new Customer();
		c.setLastName("Skywalker");
		for (Customer customer : customerService
				.findCustomer(c)) {
			System.out.println(customer);
		}
		
	}
	
	public void unbind(ICustomerSearch customerService) {
		
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
