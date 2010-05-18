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
package org.eclipse.riena.toolbox.assemblyeditor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;

/**
 * Visitor to check, if a Call to getRidget("ridgetID") or getRidget(RidgetType.class, "ridgetID") already exists.
 *
 */
public class RidgetCallVisitor extends ASTVisitor{
	private boolean callExists;
	private final String ridgetId; 
	public RidgetCallVisitor(String ridgetId) {
		this.ridgetId = ridgetId;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		if (RidgetGenerator.METHOD_GET_RIDGET.equals(node.getName().getFullyQualifiedName())){
			if (!node.arguments().isEmpty()){
				// check which getRidget-method is called (1 or 2 arguments)
				// we just care for the last argument the ridgetId 
				Object obj = null;
				if (node.arguments().size() == 2){
					obj = node.arguments().get(1);
				}
				else{
					obj = node.arguments().get(0);
				}
				
				if (obj instanceof StringLiteral){
					StringLiteral arg = (StringLiteral) obj;
					if (ridgetId.equals(arg.getLiteralValue())){
						callExists = true;
						return true;
					}
				}
			}
		}
		return true;
	}

	public boolean isCallExists() {
		return callExists;
	}
}