package org.eclipse.riena.toolbox.previewer.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.riena.toolbox.previewer.ClassFinder;
import org.eclipse.riena.toolbox.previewer.IPreviewCustomizer;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class Preview extends ViewPart{
	public final static String ID = "org.eclipse.riena.toolbox.previewer.ui.Preview"; //$NON-NLS-1$

	private Composite parent;

	private CompResourceChangeListener changeListener;

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		parent.setLayout(new FillLayout());
		setPartName("Previewer");
		
		
		getViewSite().getActionBars().getToolBarManager().add(new ViewSizeToolBar(parent));
		
		changeListener = new CompResourceChangeListener(parent.getDisplay());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(changeListener, IResourceChangeEvent.POST_CHANGE);
	}

	public void showView(final ViewPartInfo viewPart) {
		updateView(viewPart);
		changeListener.setViewPart(viewPart);
	}
	
	private class ViewSizeToolBar extends ContributionItem{
		
		private final Composite viewParent;

		public ViewSizeToolBar(Composite viewParent) {
			this.viewParent = viewParent;
		}
		
		@Override
		public void fill(ToolBar parent, int index) {
			
			final Text txtSize = createText(parent);
			
			ToolItem toolApply = new ToolItem(parent, SWT.PUSH);
			toolApply.setWidth(60);
			toolApply.setText("Reset");
			toolApply.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					//viewParent.setSize(1024, 768);
					if (getSite().getShell() != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()){
						Rectangle oldBounds = getSite().getShell().getBounds();
						oldBounds.width = 1024;
						oldBounds.height = 768;
						getSite().getShell().setBounds(oldBounds);
					}
				}
			});
			
			viewParent.addListener(SWT.Resize, new Listener() {
			    public void handleEvent(Event e) {
			        txtSize.setText(viewParent.getSize().x+"x"+viewParent.getSize().y); //$NON-NLS-1$
			    }
			});
		}

		/**
		 * @param parent
		 * @return
		 */
		private Text createText(ToolBar parent) {
			ToolItem tool = new ToolItem(parent, SWT.SEPARATOR);
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
		
		if (parent.isDisposed()){
			return;
		}
		
		for (Control child : parent.getChildren()) {
			child.dispose();
		}

		setPartName(viewPart.getName());

		Object instance = null;
		if (ViewPart.class.isAssignableFrom(viewPart.getType())) {
			instance = ReflectionUtil.loadClass(viewPart);
			if (!ReflectionUtil.invokeMethod("createPartControl", instance, //$NON-NLS-1$
					parent)) {
			}
		} 
		else {
			instance = ReflectionUtil.newInstance(viewPart.getType(), parent);
		}
		
		
		IPreviewCustomizer contribution = ClassFinder.getContributedPreviewCustomizer();
		if (null !=contribution){
			contribution.afterCreation(parent);
		}

		parent.redraw();
		parent.layout(true);
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
		public CompilationUnitVisitor(ViewPartInfo compilationUnitClassName) {
			super();
			this.compilationUnitClassName = compilationUnitClassName;
		}

		public boolean visit(final IResourceDelta delta) throws CoreException {
			final IResource res = delta.getResource();
			

			if (res.getType() == IResource.FILE) {
					if ((compilationUnitClassName.getType().getSimpleName()+".class").equals(res.getName())) { //$NON-NLS-1$
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

		public CompResourceChangeListener(Display display) {
			this.display = display;
		}
		
		public void resourceChanged(IResourceChangeEvent event) {
			if (null == viewPart){
				return;
			}
			
			final CompilationUnitVisitor pluginXmlVisitor = new CompilationUnitVisitor(viewPart);
			
			try {
				event.getDelta().accept(pluginXmlVisitor);
				if (pluginXmlVisitor.isCompilationUnitChanged()){
					display.syncExec(new Runnable() {
						public void run() {
							updateView(new ClassFinder().loadClass(viewPart.getCompilationUnit()));
						}
					});
					
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		public void setViewPart(ViewPartInfo viewPart) {
			this.viewPart = viewPart;
		}
	}

}
