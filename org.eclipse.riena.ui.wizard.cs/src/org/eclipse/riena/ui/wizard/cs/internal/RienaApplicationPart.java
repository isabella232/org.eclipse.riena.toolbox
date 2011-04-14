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
package org.eclipse.riena.ui.wizard.cs.internal;

public enum RienaApplicationPart {
	COMMON(RienaWizardMessages.ProjectSuffixCommon, "common"), //$NON-NLS-1$
	SERVICE(RienaWizardMessages.ProjectSuffixService, "service"), //$NON-NLS-1$
	CLIENT(RienaWizardMessages.ProjectSuffixClient, "client"); //$NON-NLS-1$

	private final String suffix, packageSuffix;

	RienaApplicationPart(final String suffix, final String packageSuffix) {
		this.suffix = suffix;
		this.packageSuffix = packageSuffix;
	}

	public String getPackageSuffix() {
		return packageSuffix;
	}

	public String makeProjectFullName(final String projectBaseName) {
		return String.format("%s_%s", projectBaseName, suffix); //$NON-NLS-1$
	}
}
