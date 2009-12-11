/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package $packageName$.client;

import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.factory.RemoteServiceFactory;
import org.eclipse.riena.core.injector.Inject;
import org.osgi.framework.BundleContext;

import $packageName$.common.Customer;
import $packageName$.common.ICustomerService;

public class ClientExample {

	private String port;
	private IRemoteServiceRegistration customerServiceReg;

	public ClientExample(String port) {
		this.port = port;
	}
	
	public void start(BundleContext context) {
		if (customerServiceReg != null) {
			return;
		}

		RemoteServiceFactory rsf = new RemoteServiceFactory();
		Class<?> serviceInterface = ICustomerService.class;
		String url = "http://localhost" 
			    + (port != null ? ":" + port : "")
				+ "/hessian/CustomerService";
		String protocol = "hessian";

		customerServiceReg = rsf.createAndRegisterProxy(serviceInterface, url,
				protocol, context);

		Inject.service(ICustomerService.class.getName()).into(this).andStart(
				context);
	}

	public void bind(ICustomerService customerService) {
		System.out.println("ClientExample.bind() - calling customer search");
		Customer c = new Customer();
		c.setLastName("Skywalker");
		for (Customer customer : customerService.findCustomer(c)) {
			System.out.println(customer);
		}
	}
	
	public void unbind(ICustomerService customerService) {
		System.out.println("ClientExample.unbind()");
	}

	public void stop() {
		if (customerServiceReg != null) {
			customerServiceReg.unregister();
			customerServiceReg = null;
		}
	}
}
