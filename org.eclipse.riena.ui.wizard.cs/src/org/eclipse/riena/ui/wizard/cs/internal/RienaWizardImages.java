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
package org.eclipse.riena.ui.wizard.cs.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;

public class RienaWizardImages {

	private final static String ICONS = "icons"; //$NON-NLS-1$
	private final static String WIZBAN = "wizban"; //$NON-NLS-1$

	public static final ImageDescriptor DESC_NEW_APPLICATION_WIZARD = descriptor(WIZBAN, "newapp_wizard.png"); //$NON-NLS-1$

	private static ImageDescriptor descriptor(String prefix, String name) {
		return ImageDescriptor.createFromURL(url(prefix, name));
	}

	private static URL url(String prefix, String name) {
		
		Path path = new Path(String.format("/%s/%s/%s", ICONS, prefix, name)); //$NON-NLS-1$
		return FileLocator.find(RienaWizardPlugin.getDefault().getBundle(), path, null);
	}
}
