package $packageName$;

import org.eclipse.riena.navigation.IApplicationModel;
import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.ISubApplication;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.model.ApplicationModel;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.SubApplication;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtPresentationManager;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtPresentationManagerAccessor;
import org.osgi.framework.Bundle;

/**
 * Riena Hello World Sample
 */
public class $applicationClass$ extends SwtApplication {
	
	private IApplicationModel application;
	
	/**
	 * Creates the model of the application "Hello world".
	 * 
	 * @see org.eclipse.riena.navigation.ui.application.AbstractApplication#createModel()
	 */
	@Override
	protected IApplicationModel createModel() {

		SwtPresentationManager presentation = SwtPresentationManagerAccessor.getManager();

		application = new ApplicationModel("Hello World Application");
		ISubApplication subApplication = new SubApplication("Riena Samples");
		presentation.present(subApplication, "helloWorldSubApplication");
		application.addChild(subApplication);

		IModuleGroupNode moduleGroup = new ModuleGroupNode("ModuleGroup 1");
		subApplication.addChild(moduleGroup);

		// simple hello world
		IModuleNode helloWorldModule = new ModuleNode("Hello World");
		moduleGroup.addChild(helloWorldModule);

		ISubModuleNode helloWorldSubModule = new SubModuleNode("Hello World");
		presentation.registerView(HelloWorldSubModuleView.ID, false);
		presentation.present(helloWorldSubModule, HelloWorldSubModuleView.ID);
		helloWorldModule.addChild(helloWorldSubModule);

		return application;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.riena.navigation.ui.swt.application.SwtApplication#getBundle()
	 */
	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}
	

}
