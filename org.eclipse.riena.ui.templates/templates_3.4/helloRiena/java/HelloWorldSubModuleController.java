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
package $packageName$;

import org.eclipse.riena.beans.common.Person;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.ITextRidget;

public class HelloWorldSubModuleController extends SubModuleController {

	private Person bean;

	public HelloWorldSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
		bean = new Person("Zuse", "Konrad");
	}
	
	public void configureRidgets() {
		ITextRidget txtFirst = getRidget(ITextRidget.class, "txtFirst");
		txtFirst.bindToModel(bean, "firstname");
		txtFirst.setMandatory(true);
		
		ITextRidget txtLast = getRidget(ITextRidget.class, "txtLast");
		txtLast.bindToModel(bean, "lastname");
		txtLast.setMandatory(true);
		
		updateAllRidgetsFromModel();
	}

}
