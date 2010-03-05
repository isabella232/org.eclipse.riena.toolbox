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

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IconSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class ModuleComposite extends AbstractDetailComposite<ModuleNode>{
	private Text txtInstanceId;
	private VerifyTypeIdText txtTypeId;
	private IconSelectorText txtIcon;
	private Button btnUncloseable;
	private Text txtName;
	private Text txtLabel;

	public ModuleComposite(Composite parent) {
		super(parent, "module_li.png","module_re.png");
	}

	@Override
	public void bind(final ModuleNode node) {
		this.node = node;
		txtInstanceId.setText(getTextSave(node.getInstanceId()));
		txtTypeId.getText().setText(getTextSave(node.getTypeId()));
		txtTypeId.setIgnoreNode(node);
		txtIcon.getText().setText(getTextSave(node.getIcon()));
		txtIcon.setProject(node.getBundle().getProject());
		btnUncloseable.setSelection(node.isUncloseable());
		txtName.setText(getTextSave(node.getName()));
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (null == node.getPrefix()){
					return;
				}
				String simpleName = txtName.getText().trim();
				txtLabel.setText(simpleName);
				txtTypeId.getText().setText(node.getPrefix()+simpleName+node.getSuffix());
			}
		});
		
		
		txtLabel.setText(getTextSave(node.getLabel()));
	}
	
	@Override
	public boolean setFocus() {
		return txtName.setFocus();
	}

	@Override
	public void unbind() {
		node.setInstanceId(txtInstanceId.getText());
		node.setTypeId(txtTypeId.getText().getText());
		node.setIcon(txtIcon.getText().getText());
		node.setUncloseable(btnUncloseable.getSelection());
		node.setName(txtName.getText());
		node.setLabel(txtLabel.getText());
	}

	@Override
	protected void createWorkarea(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name");
		txtLabel = createLabeledText(parent,"Label");
		txtTypeId = createLabeledVerifyText(parent,"TypeId");
		txtInstanceId = createLabeledText(parent,"InstanceId");
		txtIcon = createLabeledIconSelector(parent, "Icon");
		btnUncloseable = createLabeledCheckbox(parent,"Unclosable");
	}
}
