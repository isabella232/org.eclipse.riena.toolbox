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
package org.eclipse.riena.toolbox.previewer.customizer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.riena.toolbox.previewer.customizer.Activator;

public class PreviewCustomizerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreviewCustomizerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Riena Previewer");
	}

	@Override
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.LNF_CLASS_NAME, "Look and Feel Classname",
				getFieldEditorParent()));
		//addField(new BooleanFieldEditor(PreferenceConstants.SHOW_RIDGET_IDS, "Show Ridget Ids as Tooltip", getFieldEditorParent()));
		// FIXME show RidgetIds does not work at the moment
	}

	public void init(final IWorkbench workbench) {
	}

}