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
package org.eclipse.riena.ui.wizard.cs.internal.generate.preprocessor;

import org.eclipse.riena.ui.wizard.cs.internal.RienaApplicationPart;

public class PackageBean {
	private final String base;
	private final RienaApplicationPart part;

	public PackageBean(final String base, final RienaApplicationPart part) {
		this.base = base;
		this.part = part;
	}

	@Override
	public String toString() {
		return String.format("%s.%s", base, part.getPackageSuffix()); //$NON-NLS-1$
	}

	public String getBase() {
		return base;
	}

	public String getCommon() {
		return String.format("%s.%s", base, RienaApplicationPart.COMMON.getPackageSuffix()); //$NON-NLS-1$
	}

	public String getClient() {
		return String.format("%s.%s", base, RienaApplicationPart.CLIENT.getPackageSuffix()); //$NON-NLS-1$
	}

	public String getService() {
		return String.format("%s.%s", base, RienaApplicationPart.SERVICE.getPackageSuffix()); //$NON-NLS-1$
	}
}
