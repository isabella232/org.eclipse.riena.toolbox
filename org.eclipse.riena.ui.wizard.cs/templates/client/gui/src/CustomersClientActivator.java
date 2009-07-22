package  ${package};

import org.eclipse.riena.communication.core.factory.Register;
import ${package.common}.ICustomerSearch;
import ${package.common}.ICustomers;
import org.eclipse.riena.ui.swt.AbstractRienaUIPlugin;

import org.osgi.framework.BundleContext;

public class CustomersClientActivator extends AbstractRienaUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.riena.sample.app.client.helloworld"; 

	private static CustomersClientActivator plugin;

	public CustomersClientActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		plugin = this;

		Register.remoteProxy(ICustomerSearch.class)
				.usingUrl("http://localhost:8080/hessian/CustomerSearchWS").withProtocol("hessian").andStart(context);

		Register.remoteProxy(ICustomers.class)
			.usingUrl("http://localhost:8080/hessian/CustomersWS").withProtocol("hessian").andStart(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;

	}

	public static CustomersClientActivator getDefault() {
		return plugin;
	}
}
