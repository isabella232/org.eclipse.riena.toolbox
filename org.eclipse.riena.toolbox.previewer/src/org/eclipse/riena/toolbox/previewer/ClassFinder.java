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

public class ClassFinder {

	private static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$
	private ISelectionService selectionService;

	public ClassFinder() {

	}

	public ICompilationUnit getSelectionFromPackageExplorer() {
		selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ITreeSelection selection = (ITreeSelection) selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer"); //$NON-NLS-1$

		Object firstSelection = selection.getFirstElement();

		if (!(firstSelection instanceof ICompilationUnit)) {
			return null;
		}

		return (ICompilationUnit) firstSelection;
	}

	public ViewPartInfo loadClass(ICompilationUnit comp) {

		IPath path = comp.getPath();

		if (path.segmentCount() < 2) {
			return null;
		}

		StringBuilder className = new StringBuilder();
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

		IPreviewCustomizer contrib = getContributedPreviewCustomizer();

		Class<?> parentClass = ViewPart.class;
		if (null != contrib && null != contrib.getParentClass()) {
			 parentClass = contrib.getParentClass();
		}

		URLClassLoader classLoader = createClassloader(parentClass.getClassLoader(), comp.getJavaProject());

		try {
			if (null != contrib) {
				contrib.beforeClassLoad(classLoader);
			}

			Class<?> viewClass = classLoader.loadClass(className.toString());
			if (!isValidType(viewClass)) {
				return null;
			}
			return new ViewPartInfo(className.toString(), viewClass, comp);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static IPreviewCustomizer getContributedPreviewCustomizer() {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				Activator.getDefault().getBundle().getSymbolicName() + ".previewCustomizer"); //$NON-NLS-1$
		for (IConfigurationElement elm : config) {
			try {
				IPreviewCustomizer listenerContrib = (IPreviewCustomizer) elm.createExecutableExtension("class"); //$NON-NLS-1$
				return listenerContrib;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param type
	 */
	public boolean isValidType(Class<?> type) {
		return (Composite.class.isAssignableFrom(type) || ViewPart.class.isAssignableFrom(type));

	}

	private URLClassLoader createClassloader(ClassLoader parentClass, IJavaProject project) {
		try {

			String[] classPathEntries = null;
			try {
				classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
			} catch (JavaModelException ex) {
				return null;
			}

			List<URL> urlList = new ArrayList<URL>();
			for (String entry : classPathEntries) {
				urlList.add(new Path(entry).toFile().toURI().toURL());
			}

			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			return new URLClassLoader(urls, parentClass);

		} catch (CoreException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
