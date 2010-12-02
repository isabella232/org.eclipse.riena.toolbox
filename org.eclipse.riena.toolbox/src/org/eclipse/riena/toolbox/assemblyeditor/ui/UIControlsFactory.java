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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UIControlsFactory {
	public static Label createLabel(final Composite parent, final String text) {
		final Label lbl = new Label(parent, SWT.None);
		lbl.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		lbl.setText(text);
		return lbl;
	}

	public static Text createText(final Composite parent) {
		final Text txt = new Text(parent, SWT.BORDER);
		return txt;
	}

	public static VerifyTypeIdText createCheckTypeIdText(final Composite parent) {
		final VerifyTypeIdText txt = new VerifyTypeIdText(parent);
		return txt;
	}

	public static Button createCheckbox(final Composite parent) {
		final Button butt = new Button(parent, SWT.CHECK);
		return butt;
	}

	public static Button createCombo(final Composite parent) {
		final Button butt = new Button(parent, SWT.CHECK);
		return butt;
	}

	public static OpenClassLink createOpenClassLink(final Composite parent, final String text) {
		final OpenClassLink lnk = new OpenClassLink(parent, text);
		lnk.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return lnk;
	}
}
