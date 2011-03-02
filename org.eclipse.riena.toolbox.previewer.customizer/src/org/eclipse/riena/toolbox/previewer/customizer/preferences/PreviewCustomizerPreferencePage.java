package org.eclipse.riena.toolbox.previewer.customizer.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.riena.toolbox.previewer.customizer.Activator;


public class PreviewCustomizerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreviewCustomizerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Riena Previewer");
	}
	

	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.LNF_CLASS_NAME, "Look and Feel Classname", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}
	
}