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

package $packageName$;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import $packageName$.client.ClientExample;
import $packageName$.server.ServerExample;

/**
 * The Customer Service sample shows how to publish and consume "remote" OSGi
 * Services.
 * <p>
 * This bundle can be launched in either client or server mode (just a
 * simplification).
 * <p>
 * In server mode this activator registers the ICustomerService OSGi Service.
 * This service becomes published by the Riena Communication Hessian Publisher
 * and can be consumed over http.
 * <p>
 * In client mode this activator configures a "remote" OSGi Service which acts
 * as a proxy for the actual service on the server.
 *
 */
public class Activator implements BundleActivator {

	private ClientExample client;
	private ServerExample server;

	public void start(final BundleContext context) throws Exception {
		final String port = System.getProperty("org.eclipse.equinox.http.jetty.http.port");
		if (System.getProperty("server") != null) {
			System.out.println(String.format("Starting server on port %s...\nhttp://localhost:%s/hessian/CustomerService",
					port, port));
			server = new ServerExample();
			server.start(context);
		} else {
			System.out.println(String.format("Starting client, server port=%s...", port));
			client = new ClientExample(port);
			client.start(context);
		}
	}

	public void stop(BundleContext context) throws Exception {
		if(client != null) {
			client.stop();
			client = null;
		}
		if(server != null) {
			server.stop();
			server = null;
		}
	}

}
