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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextButtonComposite extends Composite {
	private final Text text;
	private final Button browseButton;

	public TextButtonComposite(final Composite parent, final Color background, final boolean mandatory) {
		super(parent, SWT.None);

		setBackground(background);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(this);

		text = UIControlsFactory.createText(this, mandatory);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);

		browseButton = new Button(this, SWT.PUSH);
		browseButton.setText("Browse ...");

		GridDataFactory.swtDefaults().hint(100, SWT.DEFAULT).applyTo(browseButton);
	}

	public Text getText() {
		return text;
	}

	public Button getBrowseButton() {
		return browseButton;
	}
}
