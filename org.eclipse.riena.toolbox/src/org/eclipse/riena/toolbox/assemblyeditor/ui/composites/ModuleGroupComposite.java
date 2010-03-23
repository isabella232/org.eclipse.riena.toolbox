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
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class ModuleGroupComposite extends AbstractDetailComposite<ModuleGroupNode>{
	private Text txtNodeId;
	private Text txtName;

	public ModuleGroupComposite(Composite parent) {
		super(parent,"modulegroup_li.png","modulegroup_re.png");
	}

	@Override
	public void bind(final ModuleGroupNode node) {
		this.node = node;
		txtName.setText(getTextSave(node.getName()));
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (null == node.getPrefix()){
					return;
				}
				String simpleName = txtName.getText().trim();
				txtNodeId.setText(node.getPrefix()+simpleName+node.getSuffix());
			}
		});
		
		txtNodeId.setText(getTextSave(node.getNodeId()));
	}
	
	@Override
	public boolean setFocus() {
		return txtName.setFocus();
	}

	@Override
	public void unbind() {
		node.setName(txtName.getText());
		node.setNodeId(txtNodeId.getText());
	}

	@Override
	protected void createWorkarea(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent,"Name");
		txtNodeId = createLabeledText(parent,"NodeId");
	}
}
