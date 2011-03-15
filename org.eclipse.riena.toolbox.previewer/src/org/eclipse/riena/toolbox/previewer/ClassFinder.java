package org.eclipse.riena.toolbox.previewer;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.toolbox.internal.previewer.Activator;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.riena.toolbox.previewer.ui.WorkbenchUtil;

public class ClassFinder {

	private static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$
	private ISelectionService selectionService;

	public ClassFinder() {

	}

	public ICompilationUnit getSelectionFromPackageExplorer() {
		selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		final ITreeSelection selection = (ITreeSelection) selectionService
				.getSelection("org.eclipse.jdt.ui.PackageExplorer"); //$NON-NLS-1$

		final Object firstSelection = selection.getFirstElement();

		if (!(firstSelection instanceof ICompilationUnit)) {
			return null;
		}

		return (ICompilationUnit) firstSelection;
	}

	public ViewPartInfo loadClass(final ICompilationUnit comp) {

		final IPath path = comp.getPath();

		if (path.segmentCount() < 2) {
			return null;
		}

		final StringBuilder className = new StringBuilder();
		for (int i = 2; i < path.segmentCount(); i++) {
			String segment = path.segment(i);

			if (segment.endsWith(EXTENSION_JAVA)) {
				segment = segment.replace(EXTENSION_JAVA, ""); //$NON-NLS-1$
			}
			className.append(segment);

			if (i < path.segmentCount() - 1) {
				className.append("."); //$NON-NLS-1$
			}
		}

		final IPreviewCustomizer contrib = getContributedPreviewCustomizer();

		Class<?> parentClass = ViewPart.class;
		if (null != contrib && null != contrib.getParentClass()) {
			parentClass = contrib.getParentClass();
		}

		final URLClassLoader classLoader = createClassloader(parentClass.getClassLoader(), comp.getJavaProject());

		try {
			if (null != contrib) {
				contrib.beforeClassLoad(classLoader);
			}

			final Class<?> viewClass = classLoader.loadClass(className.toString());
			if (!isValidType(viewClass)) {
				return null;
			}
			return new ViewPartInfo(className.toString(), viewClass, comp);
		} catch (final ClassNotFoundException e) {
			WorkbenchUtil.handleException(e);
		} catch (final IllegalArgumentException e) {
			WorkbenchUtil.handleException(e);
		} catch (final SecurityException e) {
			WorkbenchUtil.handleException(e);
		}
		return null;
	}

	public static IPreviewCustomizer getContributedPreviewCustomizer() {
		final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				Activator.getDefault().getBundle().getSymbolicName() + ".previewCustomizer"); //$NON-NLS-1$
		for (final IConfigurationElement elm : config) {
			try {
				final IPreviewCustomizer listenerContrib = (IPreviewCustomizer) elm.createExecutableExtension("class"); //$NON-NLS-1$
				return listenerContrib;
			} catch (final CoreException e) {
				WorkbenchUtil.handleException(e);
			}
		}
		return null;
	}

	/**
	 * @param type
	 */
	public boolean isValidType(final Class<?> type) {
		return (Composite.class.isAssignableFrom(type) || ViewPart.class.isAssignableFrom(type));

	}

	private URLClassLoader createClassloader(final ClassLoader parentClass, final IJavaProject project) {
		try {

			String[] classPathEntries = null;
			try {
				classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
			} catch (final JavaModelException ex) {
				WorkbenchUtil.handleException(ex);
				return null;
			}

			final List<URL> urlList = new ArrayList<URL>();
			for (final String entry : classPathEntries) {
				urlList.add(new Path(entry).toFile().toURI().toURL());
			}

			final URL[] urls = urlList.toArray(new URL[urlList.size()]);
			return new URLClassLoader(urls, parentClass);

		} catch (final CoreException e) {
			WorkbenchUtil.handleException(e);
		} catch (final MalformedURLException e) {
			WorkbenchUtil.handleException(e);
		}
		return null;
	}
}
