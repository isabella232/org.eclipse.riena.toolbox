package $packageName$;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.SubApplicationNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProvider;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProviderAccessor;
import org.osgi.framework.Bundle;

/**
 * Riena Hello World Sample
 */
public class $applicationClass$ extends SwtApplication {

	private IApplicationNode application;

	/**
	 * Creates the model of the application "Hello world".
	 * 
	 * @see org.eclipse.riena.navigation.ui.application.AbstractApplication#createModel()
	 */
	@Override
	protected IApplicationNode createModel() {


		application = new ApplicationNode("Hello World Application");

		return application;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.riena.navigation.ui.swt.application.SwtApplication#getBundle
	 * ()
	 */
	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
