package $packageName$;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;
import org.osgi.framework.Bundle;

/**
 * Riena Hello World Sample.
 * <p>
 * A very simple application with only one sub application, one module group, one
 * module and one sub module.
 */
public class $applicationClass$ extends SwtApplication {

	/**
	 * Creates the model of the application "Hello world".
	 */
	@Override
	protected IApplicationNode createModel() {
		return new ApplicationNode("Hello World Application");
	}

	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
