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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.riena.toolbox.assemblyeditor.api.IWorkbenchService;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;

/**
 *
 */
@SuppressWarnings("restriction")
public class WorkbenchService implements IWorkbenchService {

	public String getCompilationUnitClassNameOfActiveEditor(final IEditorPart part) {
		final CompilationUnitEditor edi = (CompilationUnitEditor) part;
		final FileEditorInput inp = (FileEditorInput) edi.getEditorInput();
		final IFile file = inp.getFile();

		// FIXME get src folder from projectsettings
		final Pattern pattern = Pattern.compile(BundleNode.SRC_FOLDER + "(.*?)\\.java"); //$NON-NLS-1$
		final Matcher matcher = pattern.matcher(file.getProjectRelativePath().toOSString());
		if (matcher.matches()) {
			final String cleanClassName = matcher.group(1).substring(1).replace("\\", "."); //$NON-NLS-1$ //$NON-NLS-2$
			return cleanClassName;
		}

		return null;
	}

}
