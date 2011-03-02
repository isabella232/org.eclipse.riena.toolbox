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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IconSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IdSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.UIControlsFactory;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;

public class SubApplicationComposite extends AbstractDetailComposite<SubApplicationNode> {
	private Text txtName;
	private VerifyTypeIdText txtNodeId;
	private IconSelectorText txtIcon;
	private IdSelectorText txtPerspective;

	public SubApplicationComposite(final Composite parent) {
		super(parent, "subapplication_li.png", "subapplication_re.png"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void bind(final SubApplicationNode node) {
		this.node = node;
		txtName.setText(getTextSave(node.getName()));
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (null == node.getPrefix()) {
					return;
				}
				final String simpleName = txtName.getText().trim();
				txtNodeId.getText().setText(node.getPrefix() + simpleName + node.getSuffix());
			}
		});

		if (Util.isGiven(node.getPerspective())) {
			txtPerspective.setCurrentId(node.getPerspective());
		} else {
			txtPerspective.setCurrentId(""); //$NON-NLS-1$
		}

		txtNodeId.getText().setText(getTextSave(node.getNodeId()));
		txtNodeId.setIgnoreNode(node);
		txtIcon.getText().setText(getTextSave(node.getIcon()));
		txtIcon.setProject(node.getBundle().getProject());
		txtPerspective.getText().setText(getTextSave(node.getPerspective()));
		txtPerspective.setIds(Activator.getDefault().getAssemblyModel().getRcpPerspectiveIds());
	}

	@Override
	public boolean setFocus() {
		return txtName.setFocus();
	}

	@Override
	public void unbind() {
		node.setName(txtName.getText());
		node.setNodeId(txtNodeId.getText().getText());
		node.setIcon(txtIcon.getText().getText());
		node.setPerspective(txtPerspective.getText().getText());
	}

	@Override
	protected void createWorkarea(final Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name");
		txtNodeId = createLabeledVerifyText(parent, "NodeId");
		txtIcon = createLabeledIconSelector(parent, "Icon");
		buildViewSection(parent);
	}

	private void buildViewSection(final Composite parent) {
		final Label lblPersp = UIControlsFactory.createLabel(parent, "PerspectiveId");
		GridDataFactory.swtDefaults().applyTo(lblPersp);
		txtPerspective = new IdSelectorText(parent, workareaBackground, "Perspective Selection",
				"Select a Perspective (* = any string, ? = any char):");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtPerspective);
	}
}
