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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;



/**
 * A Textfield with a Button thath shows a Dialog to pick a Id from a given List of Strings like View-Ids.
 *
 */
public class IdSelectorText extends TextButtonComposite{

	private List<String> ids;
	
	/**
	 * Sets the predefined value in the filter-textfield
	 */
	private String currentId;
	
	public IdSelectorText(final Composite parent, final Color background, final String title, final String message) {
		super(parent, background);
		
		getBrowseButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				org.eclipse.core.runtime.Assert.isNotNull(ids); 
				
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				ElementListSelectionDialog dia = new ElementListSelectionDialog(shell, new LabelProvider());
				
				if (Util.isGiven(currentId)){
					dia.setFilter(currentId);
				}
				
				dia.setTitle(title);
				dia.setMessage(message);
				dia.setElements(ids.toArray(new Object[ids.size()]));
				dia.open();
				Object[] result = dia.getResult();
				
				if (null != result) {
					for (Object obj : result) {
						getText().setText(obj.toString());
					}
				}
			}
			
		});
	}
	
	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public void setIds(List<String> viewIds) {
		this.ids = viewIds;
	}
}