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
package org.eclipse.riena.toolbox.assemblyeditor.ui.composites;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IconSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IdSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.OpenClassLink;
import org.eclipse.riena.toolbox.assemblyeditor.ui.TextButtonComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.UIControlsFactory;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


public class SubModuleComposite extends AbstractDetailComposite<SubModuleNode> {

	private Text txtName;
	private Text txtLabel;
	private VerifyTypeIdText txtTypeId;
	private Text txtInstanceId;
	private IdSelectorText txtView;
	private BrowseControllerComposite txtController;
	private OpenClassLink lnkController;
	private OpenClassLink lnkView;
	private Button btnShared;
	private IconSelectorText txtIcon;
	private Button btnSelectable;

	public SubModuleComposite(Composite parent) {
		super(parent, "submodule_li.png","submodule_re.png");
	}

	@Override
	public void bind(final SubModuleNode node) {
		this.node = node;
		txtName.setText(getTextSave(node.getName()));
		txtLabel.setText(getTextSave(node.getLabel()));
		txtTypeId.getText().setText(getTextSave(node.getTypeId()));
		txtTypeId.setIgnoreNode(node);
		txtInstanceId.setText(getTextSave(node.getInstanceId()));

		if (null != node.getRcpView()) {
			txtView.getText().setText(getTextSave(node.getRcpView().getId()));
			txtView.setCurrentId(node.getRcpView().getId());
		} else {
			txtView.getText().setText("");
			txtView.setCurrentId("");
		}

		txtController.getText().setText(getTextSave(node.getController()));
		txtController.setControllerName(node.getController());
		txtController.setProject(node.getBundle().getProject());
		
		lnkController.setSubModule(node);
		lnkController.setClassName(node.getController());

		lnkView.setSubModule(node);

		if (node.getRcpView() != null) {
			lnkView.setClassName(node.getRcpView().getViewClass());
		}

		btnShared.setSelection(node.isShared());
		txtIcon.getText().setText(getTextSave(node.getIcon()));
		txtIcon.setProject(node.getBundle().getProject());
		btnSelectable.setSelection(node.isSelectable());
	}

	@Override
	public boolean setFocus() {
		return txtName.setFocus();
	}

	@Override
	public void unbind() {
		node.setName(txtName.getText());
		node.setLabel(txtLabel.getText());
		node.setTypeId(txtTypeId.getText().getText());
		node.setInstanceId(txtInstanceId.getText());

		if (null == node.getRcpView()) {
			node.setRcpView(new RCPView());
		}

		node.getRcpView().setId(txtView.getText().getText());
		node.setController(txtController.getText().getText());
		node.setShared(btnShared.getSelection());
		node.setIcon(txtIcon.getText().getText());
		node.setSelectable(btnSelectable.getSelection());
	}

	@Override
	protected void createWorkarea(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name");
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (null == node.getPrefix()) {
					return;
				}
				String simpleName = txtName.getText().trim();
				txtLabel.setText(simpleName);
				txtTypeId.getText().setText(node.getPrefix() + simpleName + node.getSuffix());
			}
		});
		txtLabel = createLabeledText(parent, "Label");
		txtTypeId = createLabeledVerifyText(parent, "TypeId");
		txtInstanceId = createLabeledText(parent, "InstanceId");

		buildViewSection(parent);
		buildControllerSection(parent);

		btnShared = createLabeledCheckbox(parent, "Shared");
		txtIcon = createLabeledIconSelector(parent, "Icon");
		btnSelectable = createLabeledCheckbox(parent, "Selectable");
	}

	private void buildViewSection(Composite parent) {
		lnkView = new OpenClassLink(parent, "View");
		lnkView.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lnkView);
		txtView = new IdSelectorText(parent, workareaBackground, "View Selection", "Select a View (* = any string, ? = any char):");
		txtView.setIds(Activator.getDefault().getAssemblyModel().getRcpViewIds()); // FIXME
																						// use
																						// RCPViews
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtView);
	}

	private void buildControllerSection(Composite parent) {
		lnkController = UIControlsFactory.createOpenClassLink(parent, "Controller");
		txtController = new BrowseControllerComposite(parent, workareaBackground);

		GridDataFactory.swtDefaults().applyTo(lnkController);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtController);
	}

	private static class BrowseControllerComposite extends TextButtonComposite {

		private IProject project;
		private String controllerName;

		public BrowseControllerComposite(Composite parent, Color background) {
			super(parent, background);

			getBrowseButton().addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();

					FilteredTypesSelectionDialog dia = new FilteredTypesSelectionDialog(shell, false, (IRunnableContext) null, searchScope,
							IJavaSearchConstants.CLASS_AND_ENUM);
					
					if (Util.isGiven(controllerName)){
						dia.setInitialPattern(controllerName, FilteredTypesSelectionDialog.FULL_SELECTION);
					}
					else{
						dia.setInitialPattern(project.getName()+".controller.", FilteredTypesSelectionDialog.FULL_SELECTION);
					}
					
					dia.open();
					Object[] result = dia.getResult();

					if (null != result) {
						for (Object obj : result) {
							SourceType source = (SourceType) obj;
							getText().setText(source.getFullyQualifiedName());
						}
					}
				}
			});
		}

		
		
		public String getControllerName() {
			return controllerName;
		}

		public void setControllerName(String controllerName) {
			this.controllerName = controllerName;
		}

		public IProject getProject() {
			return project;
		}

		public void setProject(IProject project) {
			this.project = project;
		}
	}
}
