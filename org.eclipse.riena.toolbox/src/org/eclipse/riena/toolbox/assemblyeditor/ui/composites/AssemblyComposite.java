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
package org.eclipse.riena.toolbox.assemblyeditor.ui.composites;

import java.util.Set;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class AssemblyComposite extends AbstractDetailComposite<AssemblyNode>{
	private Text txtId;
	private Text txtParentTypeId;
	private Text txtName;
	private Text txtAssembler;
	private Text txtAutostartsequence;
	private ContentProposalAdapter contentProposalTypeId;

	public AssemblyComposite(Composite parent) {
		super(parent, "", "ass_re.png");
	}

	@Override
	public void bind(AssemblyNode node) {
		this.node = node;
		
		txtId.setText(getTextSave(node.getId()));
		txtAssembler.setText(getTextSave(node.getAssembler()));
		txtParentTypeId.setText(getTextSave(node.getNodeTypeId()));
		
		Set<String> typeIds = Activator.getDefault().getModelService().getAllParentTypeIds(Activator.getDefault().getAssemblyModel()); 
		String[] typeIdsArray = typeIds.toArray(new String[typeIds.size()]);
		((SimpleContentProposalProvider)contentProposalTypeId.getContentProposalProvider()).setProposals(typeIdsArray);
				
		txtName.setText(getTextSave(node.getName()));
		
		if (null != node.getAutostartSequence()){
			txtAutostartsequence.setText(getTextSave(node.getAutostartSequence()+""));
		}
		else{
			txtAutostartsequence.setText("");
		}
	}
	
	@Override
	public boolean setFocus() {
		return txtId.setFocus();
	}

	@Override
	public void unbind() {
		node.setId(txtId.getText());
		node.setAssembler(txtAssembler.getText());
		node.setNodeTypeId(txtParentTypeId.getText());
		node.setName(txtName.getText());
		
		if (Util.isGiven(txtAutostartsequence.getText())){
			node.setAutostartSequence(Integer.valueOf(txtAutostartsequence.getText()));
		} else{
			node.setAutostartSequence(null);
		}
	}

	@Override
	protected void createWorkarea(Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name");
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (null == node.getPrefix()){
					return;
				}
				String simpleName = txtName.getText().trim();
				txtId.setText(node.getPrefix()+simpleName+node.getSuffix());
			}
		});
		
		txtId = createLabeledText(parent, "Id");
		txtAssembler = createLabeledText(parent, "Assembler");
		txtParentTypeId = createLabeledText(parent, "ParentNodeId");
		contentProposalTypeId = new ContentProposalAdapter(
				txtParentTypeId, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(null),
				null, 
				null);
		contentProposalTypeId.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		txtAutostartsequence = createLabeledText(parent, "StartOrder");
	}
}
