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

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Textfield with a Button, that opens a IconSelectorDialog. All Images in the
 * current Bundle (JavaProject) that have the Extension (.gif, .jpg, .png) are
 * selectable by the user.
 * 
 */
@SuppressWarnings("restriction")
public class IconSelectorText extends TextButtonComposite {

	private IProject project;

	public IProject getProject() {
		return project;
	}

	public void setProject(final IProject project) {
		this.project = project;
	}

	public IconSelectorText(final Composite parent, final Color background) {
		super(parent, background, false);

		getBrowseButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (null == project) {
					return;
				}

				final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(parent.getShell(),
						new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());

				dialog.setTitle("Icon Selection");
				dialog.setHelpAvailable(false);
				dialog.setMessage("Select an icon from the list:");
				dialog.setInput(project);
				dialog.addFilter(new ImageFilter());
				dialog.open();

				final Object[] result = dialog.getResult();

				if (null != result) {
					for (final Object obj : result) {
						final File file = (File) obj;
						getText().setText(file.getName());
					}
				}
			}

		});
	}

	private static class ImageFilter extends ViewerFilter {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (element instanceof File) {
				final File file = (File) element;
				return (file.getName().endsWith(".gif") || file.getName().endsWith(".jpg") || file.getName().endsWith( //$NON-NLS-1$ //$NON-NLS-2$
						".png")); //$NON-NLS-1$
			}
			return true;
		}
	}

}
