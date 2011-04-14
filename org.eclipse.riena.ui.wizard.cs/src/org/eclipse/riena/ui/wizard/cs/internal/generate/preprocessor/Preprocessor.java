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

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;

public interface Preprocessor {
	InputStream process(InputStream input, String tag) throws CoreException;

	String getChangedFileName();
}
