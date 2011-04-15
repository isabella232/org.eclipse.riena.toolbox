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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class UIControlsFactory {
	public static Label createLabel(final Composite parent, final String text) {
		final Label lbl = new Label(parent, SWT.None);
		lbl.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		lbl.setText(text);
		return lbl;
	}

	public static Text createText(final Composite parent) {
		return createText(parent, false);
	}

	public static Text createText(final Composite parent, final boolean mandatory) {
		final Text txt = new Text(parent, SWT.BORDER);
		if (mandatory) {
			txt.addModifyListener(new MandatoryModifyListener());
		}
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

	private static class MandatoryModifyListener implements ModifyListener {
		public void modifyText(final ModifyEvent e) {
			final Text control = (Text) e.widget;
			if (org.eclipse.riena.core.util.StringUtils.isGiven(control.getText())) {
				control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			} else {
				control.setBackground(new org.eclipse.swt.graphics.Color(control.getDisplay(), 255, 255, 175));
			}

		}
	}

	private UIControlsFactory() {
		// private
	}
}
