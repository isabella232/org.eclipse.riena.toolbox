package org.eclipse.riena.toolbox.previewer.customizer;

import org.eclipse.riena.toolbox.previewer.IPreviewCustomizer;
import org.eclipse.riena.toolbox.previewer.customizer.preferences.PreferenceConstants;
import org.eclipse.riena.ui.swt.lnf.LnFUpdater;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;
import org.eclipse.riena.ui.swt.utils.SWTControlFinder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class RienaPreviewCustomizer implements IPreviewCustomizer {

	/**
	 * @param info
	 */
	private void updateLnf(ClassLoader classLoader) {
		
		final String customLnfClass = Activator.getDefault().getPreferenceStore()
		.getString(PreferenceConstants.LNF_CLASS_NAME);

		String lnf = Activator.getDefault().getPreferenceStore().getDefaultString(PreferenceConstants.LNF_CLASS_NAME);
		if (null != customLnfClass){
			lnf = customLnfClass;
		}
		
		setLnf(classLoader, lnf);
	}

	/**
	 * @param classLoader
	 */
	private void setLnf(ClassLoader classLoader, String lnfName) {
		try{ 
			Class<?> loadClass = classLoader.loadClass(lnfName);
			RienaDefaultLnf lnf = (RienaDefaultLnf) loadClass.newInstance();
			
			LnfManager.getLnf().uninitialize();
			LnfManager.setDefaultLnf(lnf);
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public void beforeClassLoad(ClassLoader project) {
		updateLnf(project);
	}

	public Class<?> getParentClass() {
		return RienaDefaultLnf.class;
	}

	public void afterCreation(Composite parent) {
		if (!Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_RIDGET_IDS)){
			return;
		}
		
		
		LnFUpdater up =  LnFUpdater.getInstance();
		up.updateUIControls(parent, true);
		
		SWTControlFinder finder = new SWTControlFinder(parent) {
			@Override
			public void handleBoundControl(Control control, String bindingProperty) {
				control.setToolTipText(String.format("RidgetId: '%s'", bindingProperty));
				control.redraw();
			}
			
			@Override
			public void handleControl(Control control) {
				control.redraw();
			}
		};
		finder.run();
		
		
		
	}
}
