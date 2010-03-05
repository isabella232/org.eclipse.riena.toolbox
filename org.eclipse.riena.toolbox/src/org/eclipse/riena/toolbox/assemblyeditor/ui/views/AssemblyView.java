/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.assemblyeditor.ui.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.AddUIControlCallGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.ResourceChangeListener;
import org.eclipse.riena.toolbox.assemblyeditor.RidgetGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.SwtControl;
import org.eclipse.riena.toolbox.assemblyeditor.api.INodeFactory;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.AssemblyTreeViewer;
import org.eclipse.riena.toolbox.assemblyeditor.ui.DetailSection;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IDirtyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.ViewPart;


/**
 * View that shows the AssemblyTree on the left side and the DetailsSection with
 * the editor for the currently selected node on the right side.
 * 
 */
public class AssemblyView extends ViewPart implements ISaveablePart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.compeople.toolbox.assemblyeditor.ui.views.AssemblyView"; //$NON-NLS-1$
	private AssemblyTreeViewer assemblyTree;
	private boolean dirtyState = false;
	private IAction addAssemblyAction, addSubAppAction, addModuleGroupAction, addModuleAction, addSubModuleAction, deleteNodeAction,
			generateViewControllerAction, moveNodeUpAction, moveNodeDownAction, generateRidgetsActions, refreshAction;
	private DetailSection detailSection;
	private OpenControllerAction openControllerAction;
	private OpenViewAction openViewAction;
	private GenerateAddUIControlCallsAction generateAddUIControlCallsAction;
	private PluginXmlResourceChangeListener changeListener = new PluginXmlResourceChangeListener();
	private RegisterPerspectiveAction registerPerspectiveAction;

	public AssemblyView() {
		initActions();
		Activator.getDefault().getDataProvider().addResourceChangeListener(changeListener);
	}

	private void initActions() {
		INodeFactory nodeFactory = Activator.getDefault().getNodeFactory();

		addAssemblyAction = new AddAssemblyAction(nodeFactory);
		addSubAppAction = new AddSubApplication(nodeFactory);
		addModuleGroupAction = new AddModuleGroupAction(nodeFactory);
		addModuleAction = new AddModuleAction(nodeFactory);
		addSubModuleAction = new AddSubModuleAction(nodeFactory);
		deleteNodeAction = new DeleteNodeAction();
		generateViewControllerAction = new GenerateViewControllerAction();
		moveNodeUpAction = new MoveNodeUpAction();
		moveNodeDownAction = new MoveNodeDownAction();
		generateRidgetsActions = new GenerateRidgetsAction();
		openControllerAction = new OpenControllerAction();
		openViewAction = new OpenViewAction();
		generateAddUIControlCallsAction = new GenerateAddUIControlCallsAction();
		refreshAction = new RefreshAction();
		registerPerspectiveAction = new RegisterPerspectiveAction();
	}

	public AssemblyTreeViewer getAssemblyTree() {
		return assemblyTree;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

		getViewSite().getActionBars().getToolBarManager().add(refreshAction);
		getViewSite().getActionBars().getToolBarManager().add(new Separator());
		getViewSite().getActionBars().getToolBarManager().add(moveNodeUpAction);
		getViewSite().getActionBars().getToolBarManager().add(moveNodeDownAction);
		getViewSite().getActionBars().getToolBarManager().add(new Separator());

		new TreeComposite(sashForm);

		detailSection = new DetailSection(sashForm);
		sashForm.setWeights(new int[] { 30, 70 });

		detailSection.addDirtyListener(new IDirtyListener() {
			public void dirtyStateChanged(boolean isDirty) {
				setDirty(isDirty);
			}
		});
	}

	public void openClassInEditor(SubModuleNode submod, String className) {
		if (className == null) {
			System.err.println("Controller is null " + submod);
			return;
		}

		RidgetGenerator gen = new RidgetGenerator(submod.getBundle().getProject());

		try {
			ICompilationUnit unit = gen.findICompilationUnit(className);
			IEditorPart part = EditorUtility.openInEditor(unit, false);
			JavaUI.revealInEditor(part, (IJavaElement) unit);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void checkActionEnabledState() {
		AbstractAssemblyNode treeNode = getSelectedNode();

		if (null == treeNode) {
			return;
		}

		deleteNodeAction.setEnabled(!(treeNode instanceof BundleNode));

		if (treeNode instanceof BundleNode) {
			moveNodeDownAction.setEnabled(false);
			moveNodeUpAction.setEnabled(false);
		} else {
			moveNodeDownAction.setEnabled(treeNode.hasNextSibling());
			moveNodeUpAction.setEnabled(treeNode.hasPreviousSibling());
		}

		if (treeNode instanceof BundleNode) {
			addAssemblyAction.setEnabled(true);
			addSubAppAction.setEnabled(false);
			addModuleGroupAction.setEnabled(false);
			addModuleAction.setEnabled(false);
			addSubModuleAction.setEnabled(false);
			generateViewControllerAction.setEnabled(false);
			generateRidgetsActions.setEnabled(false);
			generateAddUIControlCallsAction.setEnabled(false);
			openControllerAction.setEnabled(false);
			openViewAction.setEnabled(false);
			registerPerspectiveAction.setEnabled(false);
		} else if (treeNode instanceof AssemblyNode) {
			boolean enabled = !treeNode.hasChildren();
			addAssemblyAction.setEnabled(false);

			addSubAppAction.setEnabled(enabled);
			addModuleGroupAction.setEnabled(enabled);
			addModuleAction.setEnabled(enabled);
			addSubModuleAction.setEnabled(enabled);
			generateViewControllerAction.setEnabled(false);
			generateRidgetsActions.setEnabled(false);
			generateAddUIControlCallsAction.setEnabled(false);
			openControllerAction.setEnabled(false);
			openViewAction.setEnabled(false);
			registerPerspectiveAction.setEnabled(false);
		} else if (treeNode instanceof SubApplicationNode) {
			addAssemblyAction.setEnabled(false);
			addSubAppAction.setEnabled(false);
			addModuleGroupAction.setEnabled(true);
			addModuleAction.setEnabled(false);
			addSubModuleAction.setEnabled(false);
			generateViewControllerAction.setEnabled(false);
			generateRidgetsActions.setEnabled(false);
			generateAddUIControlCallsAction.setEnabled(false);
			openControllerAction.setEnabled(false);
			openViewAction.setEnabled(false);
			registerPerspectiveAction.setEnabled(true); // FIXME only enable, when Perspective does not already exist
		} else if (treeNode instanceof ModuleGroupNode) {
			addAssemblyAction.setEnabled(false);
			addSubAppAction.setEnabled(false);
			addModuleGroupAction.setEnabled(false);
			addModuleAction.setEnabled(true);
			addSubModuleAction.setEnabled(false);
			generateViewControllerAction.setEnabled(false);
			generateRidgetsActions.setEnabled(false);
			generateAddUIControlCallsAction.setEnabled(false);
			openControllerAction.setEnabled(false);
			openViewAction.setEnabled(false);
			registerPerspectiveAction.setEnabled(false);
		} else if (treeNode instanceof ModuleNode) {
			addAssemblyAction.setEnabled(false);
			addSubAppAction.setEnabled(false);
			addModuleGroupAction.setEnabled(false);
			addModuleAction.setEnabled(false);
			addSubModuleAction.setEnabled(true);
			generateViewControllerAction.setEnabled(false);

			generateRidgetsActions.setEnabled(false);
			generateAddUIControlCallsAction.setEnabled(false);
			openControllerAction.setEnabled(false);
			openViewAction.setEnabled(false);
			registerPerspectiveAction.setEnabled(false);
		} else if (treeNode instanceof SubModuleNode) {
			addAssemblyAction.setEnabled(false);
			addSubAppAction.setEnabled(false);
			addModuleGroupAction.setEnabled(false);
			addModuleAction.setEnabled(false);
			addSubModuleAction.setEnabled(true);
			SubModuleNode subMod = (SubModuleNode) treeNode;

			boolean enableViewControllerAction = false;
			if (null != subMod.getRcpView()) {
				enableViewControllerAction = Util.isGiven(subMod.getRcpView().getId()) && Util.isGiven(subMod.getController());
			}

			generateViewControllerAction.setEnabled(!enableViewControllerAction);
			generateRidgetsActions.setEnabled(Util.isGiven(subMod.getController()));
			generateAddUIControlCallsAction.setEnabled(subMod.hasViewClass());

			openControllerAction.setEnabled(Util.isGiven(subMod.getController()));
			openViewAction.setEnabled(subMod.hasViewClass());
			registerPerspectiveAction.setEnabled(false);
		}
	}

	private final class PluginXmlResourceChangeListener implements ResourceChangeListener {
		public void pluginXmlChanged(IProject project) {
			final AssemblyModel model = Activator.getDefault().getDataProvider().getData();
			Activator.getDefault().setAssemblyModel(model);

			if (assemblyTree != null) {
				assemblyTree.getTree().getDisplay().asyncExec(new Runnable() {
					public void run() {
						assemblyTree.setModel(model);
						updateTreeAndDetailSection();
						setDirty(false);
					}
				});
			}
		}

		public void projectAdded(IProject project) {
		}
	}
	
	
	private class TreeComposite extends Composite {
		public TreeComposite(Composite parent) {
		super(parent, SWT.None);
			
			GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
			assemblyTree = new AssemblyTreeViewer(this, SWT.BORDER | SWT.VIRTUAL | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
			assemblyTree.setModel(Activator.getDefault().getAssemblyModel());

			assemblyTree.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					checkActionEnabledState();
					AbstractAssemblyNode node = getSelectedNode();
					detailSection.showDetails(node);
				}
			});
			
			
			getSite().setSelectionProvider(assemblyTree.getTreeViewer());

			assemblyTree.addDirtyListener(new IDirtyListener() {
				public void dirtyStateChanged(boolean isDirty) {
					setDirty(isDirty);
				}
			});

			GridDataFactory.fillDefaults().grab(true, true).applyTo(assemblyTree.getTree());
			
			
			MenuManager menuManager = new MenuManager();
			Menu menu = menuManager.createContextMenu(assemblyTree.getTree());

			menuManager.add(deleteNodeAction);
			menuManager.add(new Separator());
			menuManager.add(addAssemblyAction);
			menuManager.add(addSubAppAction);
			menuManager.add(addModuleGroupAction);
			menuManager.add(addModuleAction);
			menuManager.add(addSubModuleAction);
			menuManager.add(new Separator());
			menuManager.add(generateViewControllerAction);
			menuManager.add(generateRidgetsActions);
			menuManager.add(generateAddUIControlCallsAction);
			menuManager.add(registerPerspectiveAction);
			menuManager.add(new Separator());
			menuManager.add(openControllerAction);
			menuManager.add(openViewAction);
			
			assemblyTree.getTree().setMenu(menu);
		}
	}

	public void updateTreeAndDetailSection() {
		checkActionEnabledState();
		assemblyTree.rebuild();

		AbstractAssemblyNode selectedNode = getSelectedNode();
		if (null != selectedNode) {
			detailSection.update(selectedNode);
		}

	}

	public void expandNode(AbstractAssemblyNode parentNode) {
		assemblyTree.getTreeViewer().setExpandedState(parentNode, true);
	}

	public void selectNode(AbstractAssemblyNode node) {
		assemblyTree.getTreeViewer().expandToLevel(node, TreeViewer.ALL_LEVELS);
		assemblyTree.getTreeViewer().setSelection(new StructuredSelection(node));
	}

	@Override
	public void setFocus() {
		assemblyTree.getTree().setFocus();
	}

	public synchronized void doSave(IProgressMonitor monitor) {
		Activator.getDefault().getDataProvider().removeResourceChangeListener(changeListener);
		detailSection.unbindCurrentComposite();
		Activator.getDefault().getDataProvider().saveData(Activator.getDefault().getAssemblyModel());
		updateTreeAndDetailSection();

		setDirty(false);
		Activator.getDefault().getDataProvider().addResourceChangeListener(changeListener);
	}

	public AbstractAssemblyNode getSelectedNode() {
		ISelection sel = assemblyTree.getTreeViewer().getSelection();

		if (sel instanceof StructuredSelection) {
			StructuredSelection selTree = (StructuredSelection) sel;
			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) selTree.getFirstElement();
			return selectedNode;
		}
		return null;
	}

	public void doSaveAs() {
	}

	public void setDirty(boolean isDirty) {
		dirtyState = isDirty;
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}

	public boolean isDirty() {
		return dirtyState;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	private abstract class AbstractAddNodeAction extends Action {
		protected final INodeFactory nodeFactory;

		public AbstractAddNodeAction(INodeFactory nodeFactory) {
			this.nodeFactory = nodeFactory;
		}

		protected BundleNode findBundle() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			AbstractAssemblyNode treeNode = assemblyView.getSelectedNode();
			if (null == treeNode) {
				return null;
			}

			AbstractAssemblyNode current = treeNode;

			BundleNode bundle = current.getBundle();
			return bundle;
		}

		@Override
		public final void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			AbstractAssemblyNode parentNode = assemblyView.getSelectedNode();
			if (null == parentNode) {
				return;
			}

			AbstractAssemblyNode newChild = createNode(parentNode);
			parentNode.add(newChild);
			assemblyView.updateTreeAndDetailSection();
			assemblyView.expandNode(parentNode);
			assemblyView.selectNode(newChild);
			detailSection.showDetails(newChild);
		}

		protected abstract AbstractAssemblyNode createNode(AbstractAssemblyNode parent);
	}

	private static class DeleteNodeAction extends Action {
		private static final String TITLE = "Do you really want to delete the Node?";
		private static final String OPTION_UNREGISTER_VIEW = "Unregister View";
		private static final String OPTION_DELETE_CONTROLLER_CLASS = "Delete Controller Class";
		private static final String OPTION_DELETE_VIEW_CLASS = "Delete View Class";

		public DeleteNodeAction() {
			setText("Delete");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.deletenode.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(AssemblyView.ID);
			AbstractAssemblyNode selectedNode = assemblyView.getSelectedNode();

			if (selectedNode instanceof SubModuleNode) {
				SubModuleNode subMod = (SubModuleNode) selectedNode;

				String[] options = new String[] { OPTION_DELETE_VIEW_CLASS, OPTION_DELETE_CONTROLLER_CLASS, OPTION_UNREGISTER_VIEW };

				ListSelectionDialog dia = new ListSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), options,
						new ArrayContentProvider(), new LabelProvider(), TITLE);
				dia.open();
				List<Object> diaResult = Arrays.asList(dia.getResult());

				if (diaResult.contains(OPTION_DELETE_VIEW_CLASS)) {
					Activator.getDefault().getCodeGenerator().deleteViewClass(subMod);
				}

				if (diaResult.contains(OPTION_DELETE_CONTROLLER_CLASS)) {
					Activator.getDefault().getCodeGenerator().deleteControllerClass(subMod);
				}

				if (diaResult.contains(OPTION_UNREGISTER_VIEW)) {
					Activator.getDefault().getDataProvider().getXmlParser().unregisterView(subMod);
				}

				if (!diaResult.isEmpty()) {
					deleteNode(assemblyView, selectedNode);
				}
			} else {
				if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "", TITLE)) {
					deleteNode(assemblyView, selectedNode);
				}
			}
		}

		private void deleteNode(AssemblyView assemblyView, AbstractAssemblyNode treeNode) {
			treeNode.delete();
			assemblyView.updateTreeAndDetailSection();
			assemblyView.doSave(null);
		}
	}

	private class AddSubModuleAction extends AbstractAddNodeAction {
		public AddSubModuleAction(INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New SubModule");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.add.submodule.action"); //$NON-NLS-1$
		}

		@Override
		public AbstractAssemblyNode createNode(AbstractAssemblyNode parent) {
			return nodeFactory.createSubModule(parent, findBundle());
		}
	}

	private class AddModuleAction extends AbstractAddNodeAction {
		public AddModuleAction(INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New Module");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.add.module.action"); //$NON-NLS-1$
		}

		@Override
		public AbstractAssemblyNode createNode(AbstractAssemblyNode parent) {
			return nodeFactory.createModule(parent, findBundle());
		}
	}

	private class AddAssemblyAction extends AbstractAddNodeAction {
		public AddAssemblyAction(INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New Assembly");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.add.assembly.action"); //$NON-NLS-1$
		}

		@Override
		public AbstractAssemblyNode createNode(AbstractAssemblyNode parent) {
			return nodeFactory.createAssembly(findBundle());
		}
	}

	private class AddModuleGroupAction extends AbstractAddNodeAction {
		public AddModuleGroupAction(INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New ModuleGroup");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.add.modulegroup.action"); //$NON-NLS-1$
		}

		@Override
		public AbstractAssemblyNode createNode(AbstractAssemblyNode parent) {
			return nodeFactory.createModuleGroup(parent, findBundle());
		}
	}

	private class AddSubApplication extends AbstractAddNodeAction {
		public AddSubApplication(INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New SubApplication");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.add.subapplication.action"); //$NON-NLS-1$
		}

		@Override
		public AbstractAssemblyNode createNode(AbstractAssemblyNode parent) {
			return nodeFactory.createSubApplication(parent, findBundle());
		}
	}

	private class GenerateViewControllerAction extends Action {
		public GenerateViewControllerAction() {
			setText("Generate View/Controller");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.generateviewcontroller.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			SubModuleNode subMod = (SubModuleNode) ((IStructuredSelection) assemblyTree.getTreeViewer().getSelection()).getFirstElement();
			if (null == subMod) {
				return;
			}

			String controllerClassName = Activator.getDefault().getCodeGenerator().generateController(subMod);
			RCPView rcpView = Activator.getDefault().getCodeGenerator().generateView(subMod);
			Activator.getDefault().getDataProvider().getXmlRenderer().registerView(subMod.getBundle(), rcpView);
			subMod.setRcpView(rcpView);
			subMod.setController(controllerClassName);
			detailSection.update(subMod);
			doSave(null);
		}
	}

	private static class MoveNodeUpAction extends Action {
		public MoveNodeUpAction() {
			setImageDescriptor(Activator.getImageDescriptor("/icons/move_up.gif"));
			setId("de.compeople.toolbox.assemblyeditor.ui.views.movenodeup.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer().getSelection();

			if (null == sel) {
				return;
			}

			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) sel.getFirstElement();
			selectedNode.moveUp();
			assemblyView.updateTreeAndDetailSection();
		}
	}

	private static class MoveNodeDownAction extends Action {
		public MoveNodeDownAction() {
			setImageDescriptor(Activator.getImageDescriptor("/icons/move_down.gif"));
			setId("de.compeople.toolbox.assemblyeditor.ui.views.movenodedown.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer().getSelection();

			if (null == sel) {
				return;
			}

			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) sel.getFirstElement();
			selectedNode.moveDown();
			assemblyView.updateTreeAndDetailSection();
		}
	}

	private static class GenerateRidgetsAction extends Action {
		public GenerateRidgetsAction() {
			setId("de.compeople.toolbox.assemblyeditor.ui.views.generateridgets.action"); //$NON-NLS-1$
			setText("Generate configureRidgets");
		}

		@Override
		public void run() {

			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer().getSelection();

			if (null == sel) {
				return;
			}

			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				RCPView rcpView = ((SubModuleNode) selectedNode).getRcpView();

				if (null != rcpView) {
					System.err.println("No ViewClass found for node: " + selectedNode);
					String className = rcpView.getViewClass();
					RidgetGenerator generator = new RidgetGenerator(selectedNode.getBundle().getProject());

					List<SwtControl> controls = generator.findSwtControls(className);
					generator.generateConfigureRidgets(((SubModuleNode) selectedNode).getController(), controls);

					if (Platform.inDebugMode()) {
						for (SwtControl control : controls) {
							System.out.println("DEBUG: found control: " + control.getSwtControlClassName() + " ridgetId: "
									+ control.getRidgetId());
						}
					}
				}

			}
		}
	}

	private class OpenControllerAction extends Action {
		public OpenControllerAction() {
			setText("Open Controller-Class");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.opencontroller.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer().getSelection();

			if (null == sel) {
				return;
			}

			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				SubModuleNode submod = (SubModuleNode) selectedNode;
				openClassInEditor(submod, submod.getController());
			}
		}
	}

	private class OpenViewAction extends Action {
		public OpenViewAction() {
			setText("Open View-Class");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.openview.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(AssemblyView.ID);
			IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer().getSelection();

			if (null == sel) {
				return;
			}

			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				SubModuleNode submod = (SubModuleNode) selectedNode;
				if (null != submod.getRcpView() && null != submod.getRcpView().getViewClass()) {
					openClassInEditor(submod, submod.getRcpView().getViewClass());
				}
			}
		}
	}

	private static class GenerateAddUIControlCallsAction extends Action {
		public GenerateAddUIControlCallsAction() {
			setText("Generate missing addUIControl Calls");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.generateadduicontrolcalls.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer().getSelection();

			if (null == sel) {
				return;
			}

			AbstractAssemblyNode selectedNode = (AbstractAssemblyNode) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				SubModuleNode submod = (SubModuleNode) selectedNode;
				if (null != submod.getRcpView() && null != submod.getRcpView().getViewClass()) {
					AddUIControlCallGenerator generator = new AddUIControlCallGenerator(selectedNode.getBundle().getProject());
					boolean ret = generator.generateAddUIControlCalls(submod.getRcpView().getViewClass());
				}
			}
		}
	}

	private class RefreshAction extends Action {
		public RefreshAction() {
			setToolTipText("Refresh");
			setImageDescriptor(Activator.getImageDescriptor("/icons/refresh.png"));
			setId("de.compeople.toolbox.assemblyeditor.ui.views.refresh.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AbstractAssemblyNode selectedNode = getSelectedNode();

			AssemblyModel model = Activator.getDefault().getDataProvider().getData();
			Activator.getDefault().setAssemblyModel(model);

			AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
					AssemblyView.ID);
			assemblyView.getAssemblyTree().setModel(model);
			assemblyView.updateTreeAndDetailSection();

			// FIXME setSelection does not work
			if (null != selectedNode) {
				assemblyTree.getTreeViewer().setSelection(new StructuredSelection(selectedNode));
			}
		}
	}
	
	
	private class RegisterPerspectiveAction extends Action {
		public RegisterPerspectiveAction() {
			setText("Register Perspective");
			setId("de.compeople.toolbox.assemblyeditor.ui.views.registerperspective.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AbstractAssemblyNode selectedNode = getSelectedNode();
			
			if (selectedNode instanceof SubApplicationNode){
				SubApplicationNode subapp = (SubApplicationNode) selectedNode;
				
				if (!Util.isGiven(subapp.getPerspective())){
					RCPPerspective perspective = Activator.getDefault().getNodeFactory().createRcpPerspective(subapp);
					Activator.getDefault().getDataProvider().getXmlRenderer().registerPerspective(selectedNode.getBundle(), perspective);
					subapp.setPerspective(perspective.getId());
					detailSection.update(subapp);
					doSave(null);
					System.out.println("generate persp " + perspective);
				}
			}
		}
	}
}