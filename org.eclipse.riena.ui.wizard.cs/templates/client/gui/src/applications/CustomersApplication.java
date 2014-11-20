package ${package}.applications;

import org.eclipse.riena.navigation.NavigationNodeId;
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
import ${package}.views.CustomerSearchSubModuleView;
import org.eclipse.riena.ui.workarea.WorkareaManager;

public class CustomersApplication extends SwtApplication {
	private IApplicationNode application;

	@Override
	public IApplicationNode createModel() {

		application = new ApplicationNode("${project}"); 
		ISubApplicationNode subApplication = new SubApplicationNode(new NavigationNodeId("customers.subapp"), "Customers"); 
		WorkareaManager.getInstance().registerDefinition(subApplication, "subApplication"); 
		application.addChild(subApplication);

		IModuleGroupNode moduleGroup = new ModuleGroupNode(new NavigationNodeId("customer.modulegroup"));
		subApplication.addChild(moduleGroup);


		
		IModuleNode cSearchModule = new ModuleNode(new NavigationNodeId("customer.module"),"Customer Search"); 
		moduleGroup.addChild(cSearchModule);

		ISubModuleNode cSearchSubModule = new SubModuleNode(new NavigationNodeId("customersearch.submodule"),"Customer Search"); 
		WorkareaManager.getInstance().registerDefinition(cSearchSubModule, CustomerSearchSubModuleView.ID);
		cSearchModule.addChild(cSearchSubModule);

		return application;

	}
}
