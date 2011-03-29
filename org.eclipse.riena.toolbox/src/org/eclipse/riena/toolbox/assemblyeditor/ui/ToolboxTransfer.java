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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import org.eclipse.swt.dnd.ByteArrayTransfer;

/**
 *
 */
public class ToolboxTransfer extends ByteArrayTransfer {

	@Override
	protected int[] getTypeIds() {
		return new int[] {};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { "FOO" }; //$NON-NLS-1$
	}
}
