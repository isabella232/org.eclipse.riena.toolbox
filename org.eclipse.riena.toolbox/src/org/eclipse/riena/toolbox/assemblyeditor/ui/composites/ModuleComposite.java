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
package org.eclipse.riena.toolbox.assemblyeditor.ui.composites;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IconSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;

public class ModuleComposite extends AbstractDetailComposite<ModuleNode> {
	private VerifyTypeIdText txtNodeId;
	private IconSelectorText txtIcon;
	private Button btnUncloseable;
	private Text txtName;

	public ModuleComposite(final Composite parent) {
		super(parent, "module_li.png", "module_re.png"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void bind(final ModuleNode node) {
		this.node = node;
		txtNodeId.getText().setText(getTextSave(node.getNodeId()));
		txtNodeId.setIgnoreNode(node);
		txtIcon.getText().setText(getTextSave(node.getIcon()));
		txtIcon.setProject(node.getBundle().getProject());
		btnUncloseable.setSelection(node.isCloseable());
		txtName.setText(getTextSave(node.getName()));
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (null == node.getPrefix()) {
					return;
				}

				// if this Module has children, don't update the NodeId
				if (node.hasChildren()) {
					return;
				}
				final String simpleName = Util.cleanNodeId(txtName.getText().trim(), false);
				txtNodeId.getText().setText(node.getPrefix() + simpleName + node.getSuffix());
			}
		});
	}

	@Override
	public boolean setFocus() {
		return txtName.setFocus();
	}

	@Override
	public void unbind() {
		node.setNodeId(txtNodeId.getText().getText());
		node.setIcon(txtIcon.getText().getText());
		node.setCloseable(btnUncloseable.getSelection());
		node.setName(txtName.getText());
	}

	@Override
	protected void createWorkarea(final Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name");
		txtNodeId = createLabeledVerifyText(parent, "NodeId");
		txtIcon = createLabeledIconSelector(parent, "Icon");
		btnUncloseable = createLabeledCheckbox(parent, "Closable");
	}
}
