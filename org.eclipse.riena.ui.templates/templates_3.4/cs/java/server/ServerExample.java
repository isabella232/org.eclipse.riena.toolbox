/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package $packageName$.server;

import java.util.Hashtable;

import org.eclipse.riena.communication.core.publisher.RSDPublisherProperties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import $packageName$.common.ICustomerService;

public class ServerExample {

	private ServiceRegistration customerServiceReg;

	public void start(BundleContext context) {
		if (customerServiceReg != null) {
			return;
		}

		// create hessian service
		CustomerService cs = new CustomerService();
		Hashtable<String, String> properties = new Hashtable<String, String>(3);

		properties.put(RSDPublisherProperties.PROP_IS_REMOTE, Boolean.TRUE
				.toString());
		properties.put(RSDPublisherProperties.PROP_REMOTE_PROTOCOL, "hessian");
		properties.put(RSDPublisherProperties.PROP_REMOTE_PATH,
				"/CustomerService");

		customerServiceReg = context.registerService(ICustomerService.class
				.getName(), cs, properties);

		// as an alternative
		// Publish.service(ICustomerService.class.getName()).usingPath("/CustomerService").withProtocol("hessian").andStart(context);
	}

	public void stop() {
		if (customerServiceReg != null) {
			customerServiceReg.unregister();
			customerServiceReg = null;
		}
	}
}
