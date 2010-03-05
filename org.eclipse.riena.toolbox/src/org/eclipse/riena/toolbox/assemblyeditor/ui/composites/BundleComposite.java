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
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class BundleComposite extends AbstractDetailComposite<BundleNode>{
	private Text txtName;

	public BundleComposite(Composite parent) {
		super(parent, "", "");
	}

	@Override
	public void bind(BundleNode node) {
		this.node = node;
		txtName.setText(getTextSave(node.getName()));
	}

	@Override
	public void unbind() {
		node.setName(txtName.getText());
	}

	@Override
	protected void createWorkarea(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name");
		txtName.setEnabled(false);
	}
}
