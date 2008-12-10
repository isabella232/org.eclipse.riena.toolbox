package $packageName$;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.SubApplicationNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;
import org.eclipse.riena.ui.workarea.WorkareaManager;
import org.osgi.framework.Bundle;

/**
 * This class controls all aspects of the application's execution
 */
public class Application extends SwtApplication {

	public static final String ID_GROUP_MBOXES = "rcp.mail.groupMailboxes"; //$NON-NLS-1$
	
	@Override
	protected IApplicationNode createModel() {
		ApplicationNode app = new ApplicationNode("Riena Mail"); //NON-NLS-1

		ISubApplicationNode subApp = new SubApplicationNode("Your Mail"); //NON-NLS-1
		app.addChild(subApp);
		WorkareaManager.getInstance().registerDefinition(subApp, "rcp.mail.perspective"); //NON-NLS-1

		IModuleGroupNode groupMailboxes = new ModuleGroupNode(new NavigationNodeId(Application.ID_GROUP_MBOXES));
		subApp.addChild(groupMailboxes);

		IModuleNode moduleAccount1 = new ModuleNode("me@this.com"); //NON-NLS-1
		groupMailboxes.addChild(moduleAccount1);
		moduleAccount1.setClosable(false);

		createSubMobule("Inbox", moduleAccount1, View.ID); //NON-NLS-1
		createSubMobule("Drafts", moduleAccount1, View.ID); //NON-NLS-1
		createSubMobule("Sent", moduleAccount1, View.ID); //NON-NLS-1


		IModuleNode moduleAccount2 = new ModuleNode("other@aol.com"); //NON-NLS-1
		groupMailboxes.addChild(moduleAccount2);
		createSubMobule("Inbox", moduleAccount2, View.ID); //NON-NLS-1
		
 		return app;
	}

	public ISubModuleNode createSubMobule(String caption, IModuleNode parent, String viewId) {
		ISubModuleNode result = new SubModuleNode(caption);
		parent.addChild(result);
		WorkareaManager.getInstance().registerDefinition(result, viewId);
		return result;
	}

	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}
}
