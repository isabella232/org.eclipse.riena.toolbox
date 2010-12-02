package org.eclipse.riena.toolbox.assemblyeditor.ui.preferences;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.riena.toolbox.Activator;

public class ToolboxConfigurationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ToolboxConfigurationPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General Settings for the AssemblyEditor");
	}

	public static final String CONST_GENERATE_VIEW_PACKAGE_NAME = "Generate View packagename"; //$NON-NLS-1$
	public static final String CONST_GENERATE_CONTROLLER_PACKAGE_NAME = "Generate Controller packagename"; //$NON-NLS-1$

	private Group createGroup(final String text) {
		final Group groupUIControls = new Group(getFieldEditorParent(), SWT.None);
		groupUIControls.setText(text);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(groupUIControls);
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).applyTo(groupUIControls);
		return groupUIControls;
	}

	@Override
	public void createFieldEditors() {

		final Group groupUIControls = createGroup("Custom UIControlsFactory");
		addField(new StringFieldEditor(PreferenceConstants.CONST_CUSTOM_UI_CONTROLS_FACTORY, "&Classname",
				groupUIControls));

		final Group groupGenerateClasses = createGroup("Generate configureRidgets");
		addField(new StringFieldEditor(PreferenceConstants.CONST_GENERATE_CONTROLLER_PACKAGE_NAME,
				"Controller Packagename", groupGenerateClasses));

		addField(new StringFieldEditor(PreferenceConstants.CONST_GENERATE_VIEW_PACKAGE_NAME, "View Packagename",
				groupGenerateClasses));

		addField(new Blacklist(getFieldEditorParent()));

		final GridLayout gdl = (GridLayout) getFieldEditorParent().getLayout();
		gdl.marginBottom = 0;
		gdl.marginTop = 0;
		gdl.marginHeight = 0;
		gdl.marginLeft = 0;
		gdl.marginRight = 0;
		gdl.marginWidth = 0;
		gdl.horizontalSpacing = 10;
	}

	public void init(final IWorkbench workbench) {
	}

	private class Blacklist extends ListEditor {
		/**
		 * @param groupBlacklist
		 */
		public Blacklist(final Composite groupBlacklist) {
			super(PreferenceConstants.CONST_CONFIGURE_RIDGETS_BLACKLIST, "configureRidgets Blacklist", groupBlacklist);
		}

		@Override
		protected String createList(final String[] items) {
			final StringBuilder bob = new StringBuilder();
			for (int i = 0; i < items.length; i++) {
				final String item = items[i];
				bob.append(item);
				if (i < items.length - 1) {
					bob.append(";");
				}
			}
			return bob.toString();
		}

		@Override
		protected String getNewInputObject() {
			final InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "",
					"Enter a fullyqualified Classname", "", null);
			if (dlg.open() == Window.OK) {
				return dlg.getValue();
			}
			return null;
		}

		@Override
		protected String[] parseString(final String stringList) {
			return stringList.split(";");
		}
	}
}