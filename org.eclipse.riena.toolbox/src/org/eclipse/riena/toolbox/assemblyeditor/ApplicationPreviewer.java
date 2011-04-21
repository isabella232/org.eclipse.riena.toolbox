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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import org.eclipse.osgi.framework.internal.core.EquinoxLauncher;

/**
 *
 */
@SuppressWarnings("restriction")
public class ApplicationPreviewer {

	private final static String TP = "reference:file:/C:/build/targets/Riena-target-201104130523-win32/eclipse/plugins/"; //$NON-NLS-1$

	private final List<String> targetPlatformBundles = Arrays.asList(new String[] {//
			"com.caucho.hessian_3.2.0.jar", // 
					"org.eclipse.core.databinding_1.4.0.I20110111-0800.jar",//
					"org.eclipse.core.databinding.beans_1.2.100.I20100824-0800.jar",//
					"org.eclipse.core.databinding.property_1.4.0.I20110222-0800.jar",//
					"org.eclipse.riena.beans.common_3.0.0.HEAD.jar", //
					"org.eclipse.riena.communication.console_3.0.0.HEAD.jar",// 
					"org.eclipse.riena.communication.core_3.0.0.HEAD.jar",//
					"org.eclipse.riena.communication.factory.hessian_3.0.0.HEAD.jar",// 
					"org.eclipse.riena.core_3.0.0.HEAD.jar",// 
					"org.eclipse.riena.ui.common_3.0.0.HEAD",//
					"org.eclipse.riena.ui.core_3.0.0.HEAD",// 
					"org.eclipse.riena.ui.filter_3.0.0.HEAD",//
					"org.eclipse.riena.ui.ridgets_3.0.0.HEAD",//
					"org.eclipse.riena.ui.ridgets.swt_3.0.0.HEAD",//
					"org.eclipse.riena.navigation_3.0.0.HEAD",// 
					"org.eclipse.riena.navigation.ui_3.0.0.HEAD",//
					"org.eclipse.riena.navigation.ui.swt_3.0.0.HEAD",//
					"org.apache.oro_2.0.8.v200903061218.jar" });

	public void start() throws BundleException {
		final Map<String, String> configuration = new HashMap<String, String>();
		final EquinoxLauncher launcher = new EquinoxLauncher(configuration);
		launcher.start();
		final BundleContext ctx = launcher.getBundleContext();

		//		for (final String tpBundle : targetPlatformBundles) {
		//			ctx.installBundle(TP + tpBundle);
		//			System.out.println(">> " + tpBundle);
		//
		//			for (final Bundle installed : ctx.getBundles()) {
		//				System.out.println("installed " + installed.getSymbolicName());
		//			}
		//		}

		final Bundle minimalBundle = ctx
				.installBundle("reference:file:C:/build/workspaces/toolbox_runtime/org.eclipse.riena.toolbox.minimal"); //$NON-NLS-1$
		minimalBundle.start();

	}

}
