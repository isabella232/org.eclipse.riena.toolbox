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

import org.eclipse.riena.toolbox.Util;

/**
 * A Textfield with a Button thath shows a Dialog to pick a Id from a given List
 * of Strings like View-Ids.
 * 
 */
public class IdSelectorText extends TextButtonComposite {

	/**
	 * Sets the predefined value in the filter-textfield
	 */
	private String currentId;

	private IDataProvider dataProvider;

	public IdSelectorText(final Composite parent, final Color background, final String title, final String message) {
		super(parent, background);

		getBrowseButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				org.eclipse.core.runtime.Assert.isNotNull(dataProvider);
				final List<String> ids = dataProvider.getData();
				org.eclipse.core.runtime.Assert.isNotNull(ids);

				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final ElementListSelectionDialog dia = new ElementListSelectionDialog(shell, new LabelProvider());

				if (Util.isGiven(currentId)) {
					dia.setFilter(currentId);
				}

				dia.setTitle(title);
				dia.setMessage(message);
				dia.setElements(ids.toArray(new Object[ids.size()]));
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

	public void setCurrentId(final String currentId) {
		this.currentId = currentId;
	}

	/**
	 * @return the dataProvider
	 */
	public IDataProvider getDataProvider() {
		return dataProvider;
	}

	/**
	 * @param dataProvider
	 *            the dataProvider to set
	 */
	public void setDataProvider(final IDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public interface IDataProvider {
		List<String> getData();
	}
}
