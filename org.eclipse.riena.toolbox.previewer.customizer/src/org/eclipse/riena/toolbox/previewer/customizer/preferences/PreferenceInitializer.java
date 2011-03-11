package org.eclipse.riena.toolbox.previewer.customizer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.riena.toolbox.previewer.customizer.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.LNF_CLASS_NAME, "org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf");
		store.setDefault(PreferenceConstants.SHOW_RIDGET_IDS, true);
	}

}
