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

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ViewSelectorText extends TextButtonComposite {

	private List<String> viewIds;

	public ViewSelectorText(final Composite parent, final Color background) {
		super(parent, background);

		getBrowseButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				org.eclipse.core.runtime.Assert.isNotNull(viewIds);

				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final ElementListSelectionDialog dia = new ElementListSelectionDialog(shell, new LabelProvider());
				dia.setTitle("View Selection");
				dia.setMessage("Select a View (* = any string, ? = any char):");
				dia.setElements(viewIds.toArray(new Object[viewIds.size()]));
				dia.open();
				final Object[] result = dia.getResult();

				if (null != result) {
					for (final Object obj : result) {
						getText().setText(obj.toString());
					}
				}
			}

		});
	}

	public List<String> getViewIds() {
		return viewIds;
	}

	public void setViewIds(final List<String> viewIds) {
		this.viewIds = viewIds;
	}

}
