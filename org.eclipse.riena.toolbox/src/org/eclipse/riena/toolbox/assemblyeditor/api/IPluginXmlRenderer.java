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
package org.eclipse.riena.toolbox.assemblyeditor.api;

import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;

public interface IPluginXmlRenderer {
	void saveDocument(BundleNode bundle);
	boolean registerView(BundleNode bundle, RCPView view);
	boolean registerPerspective(BundleNode bundle, RCPPerspective perspective);
}
