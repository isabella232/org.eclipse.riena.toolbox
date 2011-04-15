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
package org.eclipse.riena.toolbox.assemblyeditor;

import org.eclipse.riena.navigation.model.ModuleGroupNode;

/**
 *
 */
@SuppressWarnings("restriction")
public class ApplicationPreviewer {

	public void start(final ModuleGroupNode moduleGroup) {
		//		final String className = "org.eclipse.equinox.frameworkadmin.equinox.internal.EquinoxFrameworkAdminFactory";
		//		//only this final part is dependent final on the target final framework implementation.
		//
		//		FrameworkAdmin fwAdmin = null;
		//		try {
		//			fwAdmin = FrameworkAdminFactory.getInstance(className);
		//		} catch (final InstantiationException e1) {
		//			e1.printStackTrace();
		//		} catch (final IllegalAccessException e1) {
		//			e1.printStackTrace();
		//		} catch (final ClassNotFoundException e1) {
		//			e1.printStackTrace();
		//		}
		//
		//		//After instanciating FrameworkAdmin object, completely same code can be used
		//		//as the case that you get the object from a service registry on OSGi framework.
		//		final Manipulator manipulator = fwAdmin.getManipulator();
		//		final ConfigData configData = manipulator.getConfigData();
		//		final LauncherData launcherData = manipulator.getLauncherData();
		//
		//		//1. Set Parameters to LaunchData.
		//		//launcherData.setJvm(new File("C:\Java\jre1.5.0_09\bin\java.exe"));
		//		launcherData.setJvmArgs(new String[] { "-Dms40" });
		//		launcherData.setFwPersistentDataLocation(new File("C:/eclipse/configuration"), true);
		//		launcherData.setFwJar(new File("C:/eclipse/plugins/org.eclipse.osgi_3.3.0.v20070208.jar"));
		//		launcherData.setFwConfigLocation(new File("C:/eclipse/configuration"));
		//
		//		//2. Set Parameters to ConfigData.
		//		final URI bundleLocation = null;
		//		final int startlevel = 4;
		//		final boolean markedAsStartedOrNot = true;
		//		configData.addBundle(new BundleInfo(bundleLocation, startlevel, markedAsStartedOrNot));
		//
		//		configData.setBeginningFwStartLevel(6);
		//		configData.setInitialBundleStartLevel(5);
		//		//configData.setFwDependentProp("osgi.console","9000");
		//
		//		//3. Save them.
		//		try {
		//			manipulator.save(false);
		//			final Process process = fwAdmin.launch(manipulator, new File("C:/eclipse"));
		//		} catch (final FrameworkAdminRuntimeException e) {
		//			e.printStackTrace();
		//		} catch (final IOException e) {
		//			e.printStackTrace();
		//		}
		//
		//		final EquinoxLauncher equinoxLauncher = new EquinoxLauncher(null);

		//4. Launch it.

	}

}
