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
		store.setDefault(PreferenceConstants.CONST_CUSTOM_UI_CONTROLS_FACTORY, "");
		store.setDefault(PreferenceConstants.CONST_GENERATE_CONTROLLER_PACKAGE_NAME, "controller");
		store.setDefault(PreferenceConstants.CONST_GENERATE_VIEW_PACKAGE_NAME, "views");

		store.setDefault(PreferenceConstants.CONST_CONFIGURE_RIDGETS_BLACKLIST,
				"org.eclipse.swt.widgets.Label;org.eclipse.swt.widgets.Composite");
	}

}
