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
import org.eclipse.swt.layout.FillLayout;
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
	private static final String VIEW_TITLE = "Previewer";

	public final static String ID = "org.eclipse.riena.toolbox.previewer.ui.Preview"; //$NON-NLS-1$

	private Composite parent;

	private CompResourceChangeListener changeListener;

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
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

	private class ViewSizeToolBar extends ContributionItem {

		private final Composite viewParent;

		public ViewSizeToolBar(final Composite viewParent) {
			this.viewParent = viewParent;
		}

		@Override
		public void fill(final ToolBar parent, final int index) {
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
	}

	/**
	 * @param viewPart
	 */
	private void updateView(final ViewPartInfo viewPart) {

		if (parent.isDisposed()) {
			return;
		}

		for (final Control child : parent.getChildren()) {
			child.dispose();
		}

		parent.layout(true);
		parent.redraw();
		setPartName(viewPart.getName());

		if (ViewPart.class.isAssignableFrom(viewPart.getType())) {
			final Object instance = ReflectionUtil.loadClass(viewPart);
			if (!ReflectionUtil.invokeMethod("createPartControl", instance, //$NON-NLS-1$
					parent)) {
				setPartName(VIEW_TITLE);
				return;
			}
		} else {
			final Control instance = (Control) ReflectionUtil.newInstance(viewPart.getType(), parent);
			if (null == instance) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Warning",
						"Can not instantiate Composite " + viewPart.getType().getName()
								+ "\nNo valid SWT-style constructor found");
				return;
			}
		}

		final IPreviewCustomizer contribution = WorkspaceClassLoader.getInstance().getContributedPreviewCustomizer();
		if (null != contribution) {
			contribution.afterCreation(parent);
		}

		parent.layout(true);
		parent.redraw();
	}

	@Override
	public void setFocus() {
		parent.setFocus();
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
