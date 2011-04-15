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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractTypedNode;

public class VerifyTypeIdText extends Composite {
	private final Text text;
	private AbstractTypedNode<?> ignoreNode;
	private final ControlDecoration decoration;

	public VerifyTypeIdText(final Composite parent) {
		super(parent, SWT.None);
		setLayout(new FillLayout());
		text = UIControlsFactory.createText(this, true);

		decoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		decoration.setShowHover(true);
		final Image errorImage = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
		decoration.setDescriptionText("TypeId already exists");
		decoration.setImage(errorImage);
		decoration.hide();

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				isValid();
			}
		});
	}

	public boolean isValid() {
		final boolean valid = typeIdAlreadyExists(text.getText());

		if (valid) {
			decoration.show();
		} else {
			decoration.hide();
		}
		return !valid;
	}

	public AbstractTypedNode<?> getIgnoreNode() {
		return ignoreNode;
	}

	public void setIgnoreNode(final AbstractTypedNode<?> ignoreNode) {
		this.ignoreNode = ignoreNode;
	}

	private boolean typeIdAlreadyExists(final String text) {

		final Set<String> typeIds = Activator.getDefault().getModelService()
				.getAllTypeIds(Activator.getDefault().getAssemblyModel(), ignoreNode);
		final boolean ret = (typeIds.contains(text.trim()));
		return ret;
	}

	public Text getText() {
		return text;
	}
}
