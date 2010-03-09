package ${package}.applications;

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
import ${package}.CustomersClientActivator;
import ${package}.views.CustomerSearchSubModuleView;
import org.eclipse.riena.ui.workarea.WorkareaManager;
import org.osgi.framework.Bundle;

public class CustomersApplication extends SwtApplication {
	private IApplicationNode application;

	@Override
	protected IApplicationNode createModel() {

		application = new ApplicationNode("${project}"); 
		ISubApplicationNode subApplication = new SubApplicationNode("Customers"); 
		WorkareaManager.getInstance().registerDefinition(subApplication, "subApplication"); 
		application.addChild(subApplication);

		IModuleGroupNode moduleGroup = new ModuleGroupNode();
		subApplication.addChild(moduleGroup);


		
		IModuleNode cSearchModule = new ModuleNode("Customer Search"); 
		moduleGroup.addChild(cSearchModule);

		ISubModuleNode cSearchSubModule = new SubModuleNode("Customer Search"); 
		WorkareaManager.getInstance().registerDefinition(cSearchSubModule, CustomerSearchSubModuleView.ID);
		cSearchModule.addChild(cSearchSubModule);

		return application;

	}
}
