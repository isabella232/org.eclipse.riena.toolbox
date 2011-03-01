package org.eclipse.riena.toolbox.previewer.customizer;

import org.eclipse.riena.toolbox.previewer.IPreviewCustomizer;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;
import org.eclipse.swt.widgets.Composite;

public class RienaPreviewCustomizer implements IPreviewCustomizer {

	/**
	 * @param info
	 */
	private void updateLnf(ClassLoader classLoader) {
		String lnf = "org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf";
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
		
	}
}
