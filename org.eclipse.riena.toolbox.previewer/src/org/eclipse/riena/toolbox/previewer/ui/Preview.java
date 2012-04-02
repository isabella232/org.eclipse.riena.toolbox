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
package org.eclipse.riena.toolbox.previewer.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.toolbox.previewer.IPreviewCustomizer;
import org.eclipse.riena.toolbox.previewer.WorkspaceClassLoader;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;

public class Preview extends ViewPart {
	public final static String ID = "org.eclipse.riena.toolbox.previewer.ui.Preview"; //$NON-NLS-1$

	private static final String VIEW_TITLE = "Previewer";

	private Composite globalParent;

	private CompResourceChangeListener changeListener;

	private Button showGridButton;

	private ViewPartInfo lastViewPart;

	private ShowGridPaintListener showGridPaintListener;

	@Override
	public void createPartControl(final Composite parent) {
		this.globalParent = parent;
		parent.setLayout(new FillLayout());
		setPartName(VIEW_TITLE);

		getViewSite().getActionBars().getToolBarManager().add(new ViewSizeToolBar(parent));

		changeListener = new CompResourceChangeListener(parent.getDisplay());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(changeListener, IResourceChangeEvent.POST_CHANGE);
	}

	public void showView(final ViewPartInfo viewPart) {
		updateView(viewPart);
		changeListener.setViewPart(viewPart);
	}

	/**
	 * @param viewPart
	 */
	private void updateView(final ViewPartInfo viewPart) {

		this.lastViewPart = viewPart;

		if (globalParent.isDisposed()) {
			return;
		}

		for (final Control child : globalParent.getChildren()) {
			child.removePaintListener(showGridPaintListener);
			child.dispose();
		}

		repaintView();
		setPartName(viewPart.getName());

		if (ViewPart.class.isAssignableFrom(viewPart.getType())) {
			final Object instance = ReflectionUtil.loadClass(viewPart);
			if (!ReflectionUtil.invokeMethod("createPartControl", instance, //$NON-NLS-1$
					globalParent)) {
				setPartName(VIEW_TITLE);
				return;
			}
		} else {
			final Control instance = (Control) ReflectionUtil.newInstance(viewPart.getType(), globalParent);
			if (null == instance) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Warning",
						"Can not instantiate Composite " + viewPart.getType().getName()
								+ "\nNo valid SWT-style constructor found");
				return;
			} else {
				instance.addPaintListener(showGridPaintListener);
			}
		}

		final IPreviewCustomizer contribution = WorkspaceClassLoader.getInstance().getContributedPreviewCustomizer();
		if (null != contribution) {
			contribution.afterCreation(globalParent);
		}

		repaintView();
	}

	/**
	 * 
	 */
	private void repaintView() {
		globalParent.layout(true, true);
		globalParent.redraw();
	}

	@Override
	public void setFocus() {
		globalParent.setFocus();
	}

	private class ViewSizeToolBar extends ContributionItem {
		private static final String LABEL_HIDE_GRID = "hide grid";
		private static final String LABEL_SHOW_GRID = "show grid";
		private final Composite viewParent;

		public ViewSizeToolBar(final Composite viewParent) {
			this.viewParent = viewParent;
		}

		@Override
		public void fill(final ToolBar parent, final int index) {
			createShowGridButton(parent);

			final Text txtSize = createText(parent);
			viewParent.addListener(SWT.Resize, new Listener() {
				public void handleEvent(final Event e) {
					txtSize.setText(viewParent.getSize().x + "x" + viewParent.getSize().y); //$NON-NLS-1$
				}
			});
		}

		/**
		 * @param parent
		 * @return
		 */
		private Text createText(final ToolBar parent) {
			final ToolItem tool = new ToolItem(parent, SWT.SEPARATOR);
			final Text text = new Text(parent, SWT.BORDER);
			tool.setWidth(80);
			text.setEditable(false);
			tool.setControl(text);
			return text;
		}

		private Button createShowGridButton(final ToolBar parent) {
			final ToolItem tool = new ToolItem(parent, SWT.SEPARATOR);
			showGridButton = new Button(parent, SWT.TOGGLE);
			showGridButton.setText(LABEL_SHOW_GRID);
			showGridButton.setSelection(false);
			showGridButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					if (null != lastViewPart && !ViewPart.class.isAssignableFrom(lastViewPart.getType())) {
						updateView(lastViewPart);
					} else {
						repaintView();
					}
					showGridButton.setText(showGridButton.getSelection() ? LABEL_HIDE_GRID : LABEL_SHOW_GRID);
				}
			});

			tool.setWidth(70);
			tool.setControl(showGridButton);
			showGridPaintListener = new ShowGridPaintListener();
			globalParent.addPaintListener(showGridPaintListener);
			return showGridButton;
		}
	}

	/**
	 * Paints horizontal and vertical lines between the controls, when the
	 * showGridButton is selected.
	 */
	private class ShowGridPaintListener implements PaintListener {
		public void paintControl(final PaintEvent e) {
			if (!showGridButton.getSelection()) {
				return;
			}

			if (!(e.widget instanceof Composite)) {
				return;
			}

			final Composite targetComposite = (Composite) e.widget;
			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_RED));

			for (final Control control : targetComposite.getChildren()) {
				final int width = control.getBounds().width + control.getBounds().x;
				// vertical line
				e.gc.drawLine(width, 0, width, targetComposite.getBounds().height);
				// horizontal line
				final int height = control.getBounds().height + control.getBounds().y;
				e.gc.drawLine(0, height, targetComposite.getBounds().width, height);
				if (control instanceof Composite) {
					control.addPaintListener(this);
				}
			}
		}
	}

	private static class CompilationUnitVisitor implements IResourceDeltaVisitor {
		private final ViewPartInfo compilationUnitClassName;
		private boolean compilationUnitChanged = false;

		/**
		 * @param receivedTimeStamps
		 */
		public CompilationUnitVisitor(final ViewPartInfo compilationUnitClassName) {
			super();
			this.compilationUnitClassName = compilationUnitClassName;
		}

		public boolean visit(final IResourceDelta delta) throws CoreException {
			final IResource res = delta.getResource();

			if (res.getType() == IResource.FILE) {
				if ((compilationUnitClassName.getType().getSimpleName() + ".class").equals(res.getName())) { //$NON-NLS-1$
					compilationUnitChanged = true;
					return false;
				}
			}
			return true;
		}

		public boolean isCompilationUnitChanged() {
			return compilationUnitChanged;
		}
	}

	private class CompResourceChangeListener implements IResourceChangeListener {

		private ViewPartInfo viewPart;
		private final Display display;

		public CompResourceChangeListener(final Display display) {
			this.display = display;
		}

		public void resourceChanged(final IResourceChangeEvent event) {
			if (null == viewPart) {
				return;
			}

			final CompilationUnitVisitor pluginXmlVisitor = new CompilationUnitVisitor(viewPart);

			try {
				event.getDelta().accept(pluginXmlVisitor);
				if (pluginXmlVisitor.isCompilationUnitChanged()) {
					display.syncExec(new Runnable() {
						public void run() {
							updateView(WorkspaceClassLoader.getInstance().loadClass(viewPart.getCompilationUnit()));
						}
					});

				}
			} catch (final CoreException e) {
				WorkbenchUtil.handleException(e);
				throw new RuntimeException(e);
			}
		}

		public void setViewPart(final ViewPartInfo viewPart) {
			this.viewPart = viewPart;
		}
	}
}
