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
package org.eclipse.riena.ui.wizard.cs.internal.generate.preprocessor;

import java.io.InputStream;

public class NopPreprocessor implements Preprocessor {
	public InputStream process(InputStream input, String tag) {
		return input;
	}

	public String getChangedFileName() {
		return null;
	}
}
