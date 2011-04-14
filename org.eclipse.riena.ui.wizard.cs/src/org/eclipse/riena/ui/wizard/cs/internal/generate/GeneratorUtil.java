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
package org.eclipse.riena.ui.wizard.cs.internal.generate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GeneratorUtil {
	// true if URL points to real file
	public static boolean isFile(final URL url) throws IOException {
		InputStream is = null;

		try {
			// check if we can read from the url, to
			//determine between a file and a directory
			is = url.openStream();
			return is.read() > -1;
		} catch (final IOException aEx) {
			return false;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
