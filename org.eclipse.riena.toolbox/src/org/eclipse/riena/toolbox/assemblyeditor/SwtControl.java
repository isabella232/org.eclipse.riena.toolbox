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

/**
 * Containerclass that consists of: - the Name of the SwtControlClass in die
 * SubModuleView
 * <p>
 * - the ridgetId - the MainInterface of the Ridget for example TextRidget =>
 * ITextRidget
 * 
 */
public class SwtControl {
	private String swtControlClassName;
	private String ridgetId;
	private final Class<?> ridgetClass;

	public SwtControl(final String swtControlClassName, final String ridgetId, final Class<?> ridgetClass) {
		super();
		this.swtControlClassName = swtControlClassName;
		this.ridgetId = ridgetId;
		this.ridgetClass = ridgetClass;
	}

	public String getSwtControlClassName() {
		return swtControlClassName;
	}

	public void setSwtControlClassName(final String swtControlClassName) {
		this.swtControlClassName = swtControlClassName;
	}

	public String getRidgetId() {
		return ridgetId;
	}

	public void setRidgetId(final String ridgetId) {
		this.ridgetId = ridgetId;
	}

	public Class<?> getRidgetClass() {
		return ridgetClass;
	}

	public String getRidgetClassName() {
		return ridgetClass.getSimpleName();
	}

	public String getFullyQualifiedRidgetClassName() {
		return ridgetClass.getName();
	}

	@Override
	public String toString() {
		return "SwtControl [ridgetClass=" + ridgetClass + ", ridgetId=" //$NON-NLS-1$ //$NON-NLS-2$
				+ ridgetId + ", swtControlClassName=" + swtControlClassName //$NON-NLS-1$
				+ "]"; //$NON-NLS-1$
	}
}
