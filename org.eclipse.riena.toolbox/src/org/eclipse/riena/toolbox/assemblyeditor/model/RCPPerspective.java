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
package org.eclipse.riena.toolbox.assemblyeditor.model;

public class RCPPerspective {
	private String id;
	private String perspectiveClass;
	private String name;
	
	/**
	 * Default Classname for all Riena-Perspectives.
	 */
	public final static String PERSPECTIVE_CLASS_NAME = "org.eclipse.riena.navigation.ui.swt.views.SubApplicationView"; //$NON-NLS-1$
	
	public RCPPerspective() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPerspectiveClass() {
		return perspectiveClass;
	}

	public void setPerspectiveClass(String perspectiveClass) {
		this.perspectiveClass = perspectiveClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RCPPerspective other = (RCPPerspective) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RCPPerspective [id=" + id + ", perspectiveClass=" + perspectiveClass + ", name=" + name + "]";
	}
}
