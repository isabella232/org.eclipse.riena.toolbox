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
package $packageName$;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;

/**
 * Riena Hello World Sample.
 * <p>
 * A very simple application with only one sub application, one module group, one
 * module and one sub module.
 */
public class $applicationClass$ extends SwtApplication {

	/**
	 * Creates the model of the application "Hello world".
	 */
	@Override
	protected IApplicationNode createModel() {
		return new ApplicationNode("Hello World Application");
	}

}
