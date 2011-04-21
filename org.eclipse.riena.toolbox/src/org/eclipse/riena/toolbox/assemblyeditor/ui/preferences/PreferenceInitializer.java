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
package org.eclipse.riena.toolbox.assemblyeditor.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.riena.toolbox.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.CONST_CUSTOM_UI_CONTROLS_FACTORY, ""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.CONST_GENERATE_CONTROLLER_PACKAGE_NAME, "controller"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.CONST_GENERATE_VIEW_PACKAGE_NAME, "views"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.CONST_CONFIGURE_RIDGETS_BLACKLIST,
				"org.eclipse.swt.widgets.Label;org.eclipse.swt.widgets.Composite"); //$NON-NLS-1$

		store.setDefault(PreferenceConstants.CONST_LINK_WITH_EDITOR, true);
		store.setDefault(PreferenceConstants.CONST_ONLY_SHOW_PROJECTS_WITH_ASSEMBLIES, false);
	}

}
