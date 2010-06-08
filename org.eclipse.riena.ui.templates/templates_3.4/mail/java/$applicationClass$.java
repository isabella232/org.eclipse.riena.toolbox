package $packageName$;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.SubApplicationNode;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;
import org.eclipse.riena.ui.workarea.WorkareaManager;

/**
 * This class controls all aspects of the application's execution
 */
public class $applicationClass$ extends SwtApplication {

	public static final String ID_GROUP_MBOXES = "rcp.mail.groupMailboxes"; //$NON-NLS-1$
	
	@Override
	protected IApplicationNode createModel() {
		ApplicationNode app = new ApplicationNode("Riena Mail"); //$NON-NLS-1$

		ISubApplicationNode subApp = new SubApplicationNode("Your Mail"); //$NON-NLS-1$
		app.addChild(subApp);
		WorkareaManager.getInstance().registerDefinition(subApp, "rcp.mail.perspective"); //$NON-NLS-1$

		IModuleGroupNode groupMailboxes = new ModuleGroupNode(new NavigationNodeId($applicationClass$.ID_GROUP_MBOXES));
		subApp.addChild(groupMailboxes);

		IModuleNode moduleAccount1 = NodeFactory.createModule("me@this.com", groupMailboxes); //$NON-NLS-1$
		moduleAccount1.setClosable(false);
		NodeFactory.createSubMobule("Inbox", moduleAccount1, View.ID); //$NON-NLS-1$
		NodeFactory.createSubMobule("Drafts", moduleAccount1, View.ID); //$NON-NLS-1$
		NodeFactory.createSubMobule("Sent", moduleAccount1, View.ID); //$NON-NLS-1$

		IModuleNode moduleAccount2 = NodeFactory.createModule("other@aol.com", groupMailboxes); //$NON-NLS-1$
		NodeFactory.createSubMobule("Inbox", moduleAccount2, View.ID); //$NON-NLS-1$

		return app;
	}
}
