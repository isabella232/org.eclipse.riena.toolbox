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
package org.eclipse.riena.toolbox.assemblyeditor.ui.views;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.AddUIControlCallGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.ResourceChangeListener;
import org.eclipse.riena.toolbox.assemblyeditor.RidgetGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.api.INodeFactory;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractTypedNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyModel;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SwtControl;
import org.eclipse.riena.toolbox.assemblyeditor.ui.AssemblyTreeViewer;
import org.eclipse.riena.toolbox.assemblyeditor.ui.DetailSection;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IDirtyListener;
import org.eclipse.riena.ui.swt.MessageBox;

/**
 * View that shows the AssemblyTree on the left side and the DetailsSection with
 * the editor for the currently selected node on the right side.
 * 
 */
@SuppressWarnings("restriction")
public class AssemblyView extends ViewPart implements ISaveablePart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.riena.toolbox.assemblyeditor.ui.views.AssemblyView"; //$NON-NLS-1$
	private AssemblyTreeViewer assemblyTree;
	private boolean dirtyState = false;
	private IAction addAssemblyAction, addSubAppAction, addModuleGroupAction, addModuleAction, addSubModuleAction,
			deleteNodeAction, generateViewControllerAction, moveNodeUpAction, moveNodeDownAction,
			generateRidgetsActions, refreshAction;
	private DetailSection detailSection;
	private OpenControllerAction openControllerAction;
	private OpenViewAction openViewAction;
	private GenerateAddUIControlCallsAction generateAddUIControlCallsAction;
	private final PluginXmlResourceChangeListener changeListener = new PluginXmlResourceChangeListener();
	private RegisterPerspectiveAction registerPerspectiveAction;
	private ShowPreview showApplicationPreview;

	public AssemblyView() {
		initActions();
		Activator.getDefault().getDataProvider().addResourceChangeListener(changeListener);
	}

	private void initActions() {
		final INodeFactory nodeFactory = Activator.getDefault().getNodeFactory();

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
		showApplicationPreview = new ShowPreview();
	}

	public AssemblyTreeViewer getAssemblyTree() {
		return assemblyTree;
	}

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

		getViewSite().getActionBars().getToolBarManager().add(refreshAction);
		//getViewSite().getActionBars().getToolBarManager().add(showApplicationPreview);
		new TreeComposite(sashForm);

		detailSection = new DetailSection(sashForm);
		sashForm.setWeights(new int[] { 30, 70 });

		detailSection.addDirtyListener(new IDirtyListener() {
			public void dirtyStateChanged(final AbstractAssemblyNode<?> node, final boolean isDirty) {
				node.getBundle().setDirty(true);
				setDirty(isDirty);
			}
		});
	}

	public void openClassInEditor(final SubModuleNode submod, final String className) {
		if (className == null) {
			Util.logWarning("Controller is null " + submod); //$NON-NLS-1$
			return;
		}

		final RidgetGenerator gen = new RidgetGenerator(submod.getBundle().getProject());

		try {
			final ICompilationUnit unit = gen.findICompilationUnit(className);
			final IEditorPart part = EditorUtility.openInEditor(unit, false);
			JavaUI.revealInEditor(part, (IJavaElement) unit);
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	private void checkActionEnabledState() {
		final AbstractAssemblyNode<?> treeNode = getSelectedNode();

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
			final AssemblyNode assNode = (AssemblyNode) treeNode;

			// if the assemblyNode has a NodeBuilder, than it can't have any childNodes
			if (Util.isGiven(assNode.getAssembler())) {
				addSubAppAction.setEnabled(false);
				addModuleGroupAction.setEnabled(false);
				addModuleAction.setEnabled(false);
				addSubModuleAction.setEnabled(false);
			} else {

				// if the assemblyNode already has a child, only child nodes of the same type are allowed
				if (!assNode.getChildren().isEmpty()) {
					final AbstractTypedNode<?> firstChild = assNode.getChildren().get(0);

					if (firstChild instanceof SubApplicationNode) {
						addSubAppAction.setEnabled(true);
						addModuleGroupAction.setEnabled(false);
						addModuleAction.setEnabled(false);
						addSubModuleAction.setEnabled(false);
					} else if (firstChild instanceof ModuleGroupNode) {
						addSubAppAction.setEnabled(false);
						addModuleGroupAction.setEnabled(true);
						addModuleAction.setEnabled(false);
						addSubModuleAction.setEnabled(false);
					} else if (firstChild instanceof ModuleNode) {
						addSubAppAction.setEnabled(false);
						addModuleGroupAction.setEnabled(false);
						addModuleAction.setEnabled(true);
						addSubModuleAction.setEnabled(false);
					} else if (firstChild instanceof SubModuleNode) {
						addSubAppAction.setEnabled(false);
						addModuleGroupAction.setEnabled(false);
						addModuleAction.setEnabled(false);
						addSubModuleAction.setEnabled(true);
					}
				} else {
					addSubAppAction.setEnabled(true);
					addModuleGroupAction.setEnabled(true);
					addModuleAction.setEnabled(true);
					addSubModuleAction.setEnabled(true);
				}
			}

			addAssemblyAction.setEnabled(false);
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

			// RegisterPerspectiveAction is active, when a persepectiveId is given and it is not yet registered
			final String perspectiveId = ((SubApplicationNode) treeNode).getPerspective();
			if (Util.isGiven(perspectiveId)) {
				final boolean isPerspectiveRgistered = treeNode.getBundle().getRegisteredRcpPerspectives()
						.contains(new RCPPerspective(perspectiveId));
				registerPerspectiveAction.setEnabled(!isPerspectiveRgistered);
			} else {
				registerPerspectiveAction.setEnabled(true);
			}
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
			final SubModuleNode subMod = (SubModuleNode) treeNode;

			boolean enableViewControllerAction = false;
			if (null != subMod.getRcpView()) {
				enableViewControllerAction = Util.isGiven(subMod.getName());
			}

			generateViewControllerAction.setEnabled(enableViewControllerAction);
			generateRidgetsActions.setEnabled(Util.isGiven(subMod.getController()));
			generateAddUIControlCallsAction.setEnabled(subMod.hasViewClass());

			openControllerAction.setEnabled(Util.isGiven(subMod.getController()));
			openViewAction.setEnabled(subMod.hasViewClass());
			registerPerspectiveAction.setEnabled(false);
		}
	}

	private final class PluginXmlResourceChangeListener implements ResourceChangeListener {
		public void pluginXmlChanged(final IProject project) {
			final AssemblyModel model = Activator.getDefault().getDataProvider().createData();
			Activator.getDefault().setAssemblyModel(model);

			if (assemblyTree != null) {
				assemblyTree.getTree().getDisplay().asyncExec(new Runnable() {
					public void run() {
						assemblyTree.setModel(model, false);
						updateTreeAndDetailSection();
						setDirty(false);
					}
				});
			}
		}

		public void projectAdded(final IProject project) {
		}
	}

	private class TreeComposite extends Composite {
		public TreeComposite(final Composite parent) {
			super(parent, SWT.None);

			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);
			assemblyTree = new AssemblyTreeViewer(this, SWT.BORDER | SWT.VIRTUAL | SWT.SINGLE | SWT.H_SCROLL
					| SWT.V_SCROLL);
			assemblyTree.setModel(Activator.getDefault().getAssemblyModel(), true);

			assemblyTree.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(final SelectionChangedEvent event) {
					checkActionEnabledState();
					final AbstractAssemblyNode<?> node = getSelectedNode();
					detailSection.showDetails(node);
				}
			});

			final ToolBar tb = new ToolBar(this, SWT.VERTICAL);
			final ToolBarManager toolbarManager = new ToolBarManager(tb);
			toolbarManager.add(moveNodeUpAction);
			toolbarManager.add(moveNodeDownAction);
			toolbarManager.update(true);

			getSite().setSelectionProvider(assemblyTree.getTreeViewer());

			assemblyTree.addDirtyListener(new IDirtyListener() {
				public void dirtyStateChanged(final AbstractAssemblyNode<?> node, final boolean isDirty) {
					setDirty(isDirty);
				}
			});

			assemblyTree.getTree().addKeyListener(new KeyListener() {
				public void keyReleased(final KeyEvent e) {
				}

				public void keyPressed(final KeyEvent e) {
					if (e.keyCode == SWT.DEL) {
						deleteNodeAction.run();
					}
				}
			});

			GridDataFactory.fillDefaults().grab(true, true).applyTo(assemblyTree.getTree());

			final MenuManager menuManager = new MenuManager();
			final Menu menu = menuManager.createContextMenu(assemblyTree.getTree());

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

		final AbstractAssemblyNode<?> selectedNode = getSelectedNode();
		if (null != selectedNode) {
			detailSection.update(selectedNode);
		}

	}

	public void expandNode(final AbstractAssemblyNode<?> parentNode) {
		assemblyTree.getTreeViewer().setExpandedState(parentNode, true);
	}

	public void selectNode(final AbstractAssemblyNode<?> node) {
		assemblyTree.getTreeViewer().expandToLevel(node, AbstractTreeViewer.ALL_LEVELS);
		assemblyTree.getTreeViewer().setSelection(new StructuredSelection(node));
	}

	@Override
	public void setFocus() {
		assemblyTree.getTree().setFocus();
	}

	public synchronized void doSave(final IProgressMonitor monitor) {
		Activator.getDefault().getDataProvider().removeResourceChangeListener(changeListener);
		detailSection.unbindCurrentComposite();
		Activator.getDefault().getDataProvider().saveData(Activator.getDefault().getAssemblyModel());
		updateTreeAndDetailSection();

		setDirty(false);
		Activator.getDefault().getDataProvider().addResourceChangeListener(changeListener);
	}

	public AbstractAssemblyNode<?> getSelectedNode() {
		final ISelection sel = assemblyTree.getTreeViewer().getSelection();

		if (sel instanceof StructuredSelection) {
			final StructuredSelection selTree = (StructuredSelection) sel;
			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) selTree.getFirstElement();
			return selectedNode;
		}
		return null;
	}

	public void doSaveAs() {
	}

	public void setDirty(final boolean isDirty) {
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

		public AbstractAddNodeAction(final INodeFactory nodeFactory) {
			this.nodeFactory = nodeFactory;
		}

		protected BundleNode findBundle() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final AbstractAssemblyNode<?> treeNode = assemblyView.getSelectedNode();
			if (null == treeNode) {
				return null;
			}

			final AbstractAssemblyNode<?> current = treeNode;

			final BundleNode bundle = current.getBundle();
			return bundle;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public final void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final AbstractAssemblyNode parentNode = assemblyView.getSelectedNode();
			if (null == parentNode) {
				return;
			}

			final AbstractAssemblyNode newChild = createNode(parentNode);
			parentNode.add(newChild);
			assemblyView.updateTreeAndDetailSection();
			assemblyView.expandNode(parentNode);
			assemblyView.selectNode(newChild);
			detailSection.showDetails(newChild);
			checkActionEnabledState();
		}

		protected abstract AbstractAssemblyNode createNode(AbstractAssemblyNode parent);
	}

	private static class DeleteNodeAction extends Action {
		private static final String TITLE = "Do you really want to delete the Node?";
		private static final String OPTION_UNREGISTER_VIEW = "Unregister View";
		private static final String OPTION_UNREGISTER_PERSPECTIVE = "Unregister Perspective";
		private static final String OPTION_DELETE_CONTROLLER_CLASS = "Delete Controller Class";
		private static final String OPTION_DELETE_VIEW_CLASS = "Delete View Class";
		private static final String OPTION_DELETE_NODE = "Delete Node";

		public DeleteNodeAction() {
			setText("Delete");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.deletenode.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final AbstractAssemblyNode<?> selectedNode = assemblyView.getSelectedNode();

			if (selectedNode instanceof SubModuleNode) {
				deleteSubModuleNode(assemblyView, selectedNode);

			} else if (selectedNode instanceof SubApplicationNode) {
				deleteSubApplicationNode(assemblyView, selectedNode);
			} else {
				if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "", //$NON-NLS-1$
						TITLE)) {
					selectedNode.getBundle().setDirty(true);
					deleteNode(assemblyView, selectedNode);
				}
			}
		}

		/**
		 * @param assemblyView
		 * @param selectedNode
		 */
		private void deleteSubApplicationNode(final AssemblyView assemblyView,
				final AbstractAssemblyNode<?> selectedNode) {
			final SubApplicationNode subApp = (SubApplicationNode) selectedNode;

			final String[] options = new String[] { OPTION_DELETE_NODE, OPTION_UNREGISTER_PERSPECTIVE };

			final ListSelectionDialog dia = new ListSelectionDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), options, new ArrayContentProvider(), new LabelProvider(),
					TITLE);
			dia.open();
			final List<Object> diaResult = Arrays.asList(dia.getResult());

			boolean isBundleDirty = false;

			if (diaResult.contains(OPTION_UNREGISTER_PERSPECTIVE)) {
				Activator.getDefault().getDataProvider().getXmlParser().unregisterPerspective(subApp);
				isBundleDirty = true;
			}

			if (diaResult.contains(OPTION_DELETE_NODE)) {
				isBundleDirty = true;
				selectedNode.getBundle().setDirty(isBundleDirty);
				deleteNode(assemblyView, selectedNode);
			}
		}

		/**
		 * @param assemblyView
		 * @param selectedNode
		 */
		private void deleteSubModuleNode(final AssemblyView assemblyView, final AbstractAssemblyNode<?> selectedNode) {
			final SubModuleNode subMod = (SubModuleNode) selectedNode;

			final String[] options = new String[] { OPTION_DELETE_NODE, OPTION_DELETE_VIEW_CLASS,
					OPTION_DELETE_CONTROLLER_CLASS, OPTION_UNREGISTER_VIEW };

			final ListSelectionDialog dia = new ListSelectionDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), options, new ArrayContentProvider(), new LabelProvider(),
					TITLE);
			dia.open();
			final List<Object> diaResult = Arrays.asList(dia.getResult());

			boolean isBundleDirty = false;
			if (diaResult.contains(OPTION_DELETE_VIEW_CLASS)) {
				Activator.getDefault().getCodeGenerator().deleteViewClass(subMod);
				isBundleDirty = true;
			}

			if (diaResult.contains(OPTION_DELETE_CONTROLLER_CLASS)) {
				Activator.getDefault().getCodeGenerator().deleteControllerClass(subMod);
				isBundleDirty = true;
			}

			if (diaResult.contains(OPTION_UNREGISTER_VIEW)) {
				Activator.getDefault().getDataProvider().getXmlParser().unregisterView(subMod);
				isBundleDirty = true;
			}

			if (diaResult.contains(OPTION_DELETE_NODE)) {
				isBundleDirty = true;
				selectedNode.getBundle().setDirty(isBundleDirty);
				deleteNode(assemblyView, selectedNode);
			}
		}

		private void deleteNode(final AssemblyView assemblyView, final AbstractAssemblyNode<?> treeNode) {
			// save the selection, to reset it after the tree is rebuilt
			AbstractAssemblyNode<?> newSelection = treeNode.getParent();
			if (treeNode.hasPreviousSibling()) {
				newSelection = (AbstractAssemblyNode<?>) treeNode.getPreviousSibling();
			}

			treeNode.delete();
			assemblyView.updateTreeAndDetailSection();
			assemblyView.doSave(null);
			assemblyView.getAssemblyTree().getTreeViewer().setSelection(new StructuredSelection(newSelection));
		}
	}

	private class AddSubModuleAction extends AbstractAddNodeAction {
		public AddSubModuleAction(final INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New SubModule");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.add.submodule.action"); //$NON-NLS-1$
		}

		@Override
		public SubModuleNode createNode(@SuppressWarnings("rawtypes")
		final AbstractAssemblyNode parent) {
			return nodeFactory.createSubModule(parent, findBundle());
		}
	}

	private class AddModuleAction extends AbstractAddNodeAction {
		public AddModuleAction(final INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New Module");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.add.module.action"); //$NON-NLS-1$
		}

		@Override
		public ModuleNode createNode(@SuppressWarnings("rawtypes")
		final AbstractAssemblyNode parent) {
			return nodeFactory.createModule(parent, findBundle());
		}
	}

	private class AddAssemblyAction extends AbstractAddNodeAction {
		public AddAssemblyAction(final INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New Assembly");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.add.assembly.action"); //$NON-NLS-1$
		}

		@Override
		public AssemblyNode createNode(@SuppressWarnings("rawtypes")
		final AbstractAssemblyNode parent) {
			return nodeFactory.createAssembly(findBundle());
		}
	}

	private class AddModuleGroupAction extends AbstractAddNodeAction {
		public AddModuleGroupAction(final INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New ModuleGroup");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.add.modulegroup.action"); //$NON-NLS-1$
		}

		@Override
		public ModuleGroupNode createNode(@SuppressWarnings("rawtypes")
		final AbstractAssemblyNode parent) {
			return nodeFactory.createModuleGroup(parent, findBundle());
		}
	}

	private class AddSubApplication extends AbstractAddNodeAction {
		public AddSubApplication(final INodeFactory nodeFactory) {
			super(nodeFactory);
			setText("New SubApplication");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.add.subapplication.action"); //$NON-NLS-1$
		}

		@Override
		public SubApplicationNode createNode(@SuppressWarnings("rawtypes")
		final AbstractAssemblyNode parent) {
			return nodeFactory.createSubApplication(parent, findBundle());
		}
	}

	private class GenerateViewControllerAction extends Action {
		public GenerateViewControllerAction() {
			setText("Generate View/Controller");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.generateviewcontroller.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final SubModuleNode subMod = (SubModuleNode) ((IStructuredSelection) assemblyTree.getTreeViewer()
					.getSelection()).getFirstElement();
			if (null == subMod) {
				return;
			}

			final String subModuleName = subMod.getName();
			if (!Util.isGiven(subModuleName)) {
				final MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				mb.show("Warning",
						"Could not generate the View/Controller classes, because the SubModuleName was not given",
						SWT.ERROR, new String[] { "OK" });
				return;
			}

			final String controllerClassName = Activator.getDefault().getCodeGenerator().generateController(subMod);
			final RCPView rcpView = Activator.getDefault().getCodeGenerator().generateView(subMod);
			Activator.getDefault().getDataProvider().getXmlRenderer().registerView(subMod.getBundle(), rcpView);
			subMod.setRcpView(rcpView);
			subMod.setController(controllerClassName);
			detailSection.update(subMod);
			doSave(null);
		}
	}

	private static class MoveNodeUpAction extends Action {
		public MoveNodeUpAction() {
			setImageDescriptor(Activator.getImageDescriptor("/icons/move_up.gif")); //$NON-NLS-1$
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.movenodeup.action"); //$NON-NLS-1$
			setToolTipText("Moves the selected node up");
		}

		@Override
		public void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
					.getSelection();

			if (null == sel) {
				return;
			}

			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) sel.getFirstElement();
			selectedNode.moveUp();
			assemblyView.updateTreeAndDetailSection();
		}
	}

	private static class MoveNodeDownAction extends Action {
		public MoveNodeDownAction() {
			setImageDescriptor(Activator.getImageDescriptor("/icons/move_down.gif")); //$NON-NLS-1$
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.movenodedown.action"); //$NON-NLS-1$
			setToolTipText("Moves the selected node down");
		}

		@Override
		public void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
					.getSelection();

			if (null == sel) {
				return;
			}

			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) sel.getFirstElement();
			selectedNode.moveDown();
			assemblyView.updateTreeAndDetailSection();
		}
	}

	private static class GenerateRidgetsAction extends Action {
		public GenerateRidgetsAction() {
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.generateridgets.action"); //$NON-NLS-1$
			setText("Generate configureRidgets");
		}

		@Override
		public void run() {

			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
					.getSelection();

			if (null == sel) {
				return;
			}

			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				final RCPView rcpView = ((SubModuleNode) selectedNode).getRcpView();

				if (null != rcpView) {
					final String className = rcpView.getViewClass();
					final RidgetGenerator generator = new RidgetGenerator(selectedNode.getBundle().getProject());

					final List<SwtControl> controls = generator.findSwtControlsReflectionStyle(className);
					generator.generateConfigureRidgets(((SubModuleNode) selectedNode).getController(), controls);

					if (Platform.inDebugMode()) {
						for (final SwtControl control : controls) {
							System.out.println("DEBUG: found control: " + control.getSwtControlClassName()
									+ " ridgetId: " + control.getRidgetId());
						}
					}
				} else {
					System.err.println("No ViewClass found for node: " + selectedNode);
				}

			}
		}
	}

	private class OpenControllerAction extends Action {
		public OpenControllerAction() {
			setText("Open Controller-Class");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.opencontroller.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
					.getSelection();

			if (null == sel) {
				return;
			}

			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				final SubModuleNode submod = (SubModuleNode) selectedNode;
				openClassInEditor(submod, submod.getController());
			}
		}
	}

	private class OpenViewAction extends Action {
		public OpenViewAction() {
			setText("Open View-Class");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.openview.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
					.getSelection();

			if (null == sel) {
				return;
			}

			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				final SubModuleNode submod = (SubModuleNode) selectedNode;
				if (null != submod.getRcpView() && null != submod.getRcpView().getViewClass()) {
					openClassInEditor(submod, submod.getRcpView().getViewClass());
				}
			}
		}
	}

	private static class GenerateAddUIControlCallsAction extends Action {
		public GenerateAddUIControlCallsAction() {
			setText("Generate missing addUIControl calls");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.generateadduicontrolcalls.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			final IStructuredSelection sel = (IStructuredSelection) assemblyView.getAssemblyTree().getTreeViewer()
					.getSelection();

			if (null == sel) {
				return;
			}

			final AbstractAssemblyNode<?> selectedNode = (AbstractAssemblyNode<?>) sel.getFirstElement();

			if (selectedNode instanceof SubModuleNode) {
				final SubModuleNode submod = (SubModuleNode) selectedNode;
				if (null != submod.getRcpView() && null != submod.getRcpView().getViewClass()) {
					final AddUIControlCallGenerator generator = new AddUIControlCallGenerator(selectedNode.getBundle()
							.getProject());
					generator.generateAddUIControlCalls(submod.getRcpView().getViewClass());
				}
			}
		}
	}

	private class RefreshAction extends Action {
		public RefreshAction() {
			setToolTipText("Reload all plugin.xml");
			setImageDescriptor(Activator.getImageDescriptor("/icons/refresh.png")); //$NON-NLS-1$
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.refresh.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final AbstractAssemblyNode<?> selectedNode = getSelectedNode();

			final AssemblyModel model = Activator.getDefault().getDataProvider().createData();
			Activator.getDefault().setAssemblyModel(model);

			final AssemblyView assemblyView = (AssemblyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(AssemblyView.ID);
			assemblyView.getAssemblyTree().setModel(model, false);
			assemblyView.updateTreeAndDetailSection();

			// FIXME setSelection does not work
			if (null != selectedNode) {
				assemblyTree.getTreeViewer().setSelection(new StructuredSelection(selectedNode));
			}
		}
	}

	private class RegisterPerspectiveAction extends Action {
		public RegisterPerspectiveAction() {
			setText("Generate/Register Perspective");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.registerperspective.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			final AbstractAssemblyNode<?> selectedNode = getSelectedNode();

			if (selectedNode instanceof SubApplicationNode) {
				final SubApplicationNode subapp = (SubApplicationNode) selectedNode;

				if (!Util.isGiven(subapp.getPerspective())) {
					final RCPPerspective perspective = Activator.getDefault().getNodeFactory()
							.createRcpPerspective(subapp);
					Activator.getDefault().getDataProvider().getXmlRenderer()
							.registerPerspective(selectedNode.getBundle(), perspective);
					subapp.setPerspective(perspective.getId());

					final Set<RCPPerspective> persp = Activator.getDefault().getDataProvider().getXmlParser()
							.getRcpPerspectives(selectedNode.getBundle());
					Activator.getDefault().getAssemblyModel().addAllRcpPerspectives(persp);
					detailSection.update(subapp);
					doSave(null);
					checkActionEnabledState();
				}
			}
		}
	}

	private class ShowPreview extends Action {
		public ShowPreview() {
			setText("Preview Application");
			setId("org.eclipse.riena.toolbox.assemblyeditor.ui.views.showpreview.action"); //$NON-NLS-1$
		}

		@Override
		public void run() {
			//new ApplicationPreviewer().start();
		}
	}
}