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
package org.eclipse.riena.ui.wizard.cs.internal.generate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

import org.eclipse.riena.ui.wizard.cs.internal.generate.preprocessor.Preprocessor;

public class Generator {
	private final String path;
	private final Bundle bundle;
	private final Properties properties;
	private final Preprocessor macro;

	public Generator(final String path, final Bundle bundle, final Preprocessor macro, final Properties properties) {
		this.path = path;
		this.bundle = bundle;
		this.macro = macro;
		this.properties = properties;
	}

	public void generate(final IProject project, final IProgressMonitor monitor) throws CoreException {
		final IJavaProject javaProject = JavaCore.create(project);

		String src = properties.getProperty(GeneratorProperties.SOURCE_FOLDER);
		if (src == null) {
			src = "src"; //$NON-NLS-1$
		}

		final IFolder sourceFolder = project.getFolder(src); // source folder in the target project
		if (!sourceFolder.exists()) {
			sourceFolder.create(true, true, monitor);
		}

		String _package = properties.getProperty(GeneratorProperties.PACKAGE);
		if (_package == null) {
			_package = ""; //$NON-NLS-1$
		}

		final String sourcePrefix = String.format("/%s/%s", path, src); // source folder in the template project //$NON-NLS-1$

		copy(String.format("/%s", path), project, sourcePrefix, monitor); // copy everything except for sources from the root to the root //$NON-NLS-1$
		copy(sourcePrefix, createSourceFolder(sourceFolder, monitor, _package), null, monitor); // copy sources from src to src

		javaProject.setRawClasspath(classpath(sourceFolder), monitor); // include source folder to classpath
	}

	private void copy(final String source, final IContainer destination, final String skip,
			final IProgressMonitor monitor) throws CoreException {
		final Enumeration<URL> entries = bundle.findEntries(source, "*", true); //$NON-NLS-1$

		while (entries.hasMoreElements()) {
			final URL element = entries.nextElement();
			String entry = element.getPath();

			if (entry.startsWith(source) && !entry.contains("/CVS/") && !(skip != null && entry.startsWith(skip))) { //$NON-NLS-1$
				entry = entry.substring(source.length() + 1); // skip prefix

				try {
					if (GeneratorUtil.isFile(element)) { // if real file, not folder
						String fileFolder = ""; //$NON-NLS-1$
						String fileName = null;

						final int ix = entry.lastIndexOf('/');
						if (ix > 0) { // see if it has a folder prefix (a/b/c in /a/b/c/MyFile.java)
							fileFolder = entry.substring(0, ix);
							fileName = entry.substring(ix + 1);
							createSourceFolder(destination, monitor, fileFolder); // create folder for prefix
						} else {
							fileName = entry;
						}

						InputStream is = null;

						try {
							is = macro.process(element.openStream(), entry);
							if (macro.getChangedFileName() != null) {
								fileName = macro.getChangedFileName();
							}

							final IFile file = destination.getFile(new Path(fileFolder).append(fileName)); // get entry for file, relative to destination  folder
							file.create(is, true, monitor); // copy it
						} catch (final Throwable t) {
							t.printStackTrace();
							throw new RuntimeException(t);
						} finally {
							if (is != null) {
								is.close();
							}
						}
					} else {
						createSourceFolder(destination, monitor, entry); // only create folder
					}
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private IFolder createSourceFolder(final IContainer source, final IProgressMonitor monitor, final String _package)
			throws CoreException {
		IFolder current = null;

		final StringTokenizer segments = new StringTokenizer(_package, "./"); //$NON-NLS-1$
		while (segments.hasMoreTokens()) {
			current = current != null ? current.getFolder(segments.nextToken()) : source.getFolder(new Path(segments
					.nextToken()));

			if (!current.exists()) {
				current.create(true, true, monitor);
			}
		}

		return current;
	}

	private static IPath getEEPath(final String ee) {
		IPath path = null;
		if (ee != null) {
			final IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
			final IExecutionEnvironment env = manager.getEnvironment(ee);
			if (env != null) {
				path = JavaRuntime.newJREContainerPath(env);
			}
		}
		if (path == null) {
			path = JavaRuntime.newDefaultJREContainerPath();
		}
		return path;
	}

	private IClasspathEntry[] classpath(final IFolder source) throws JavaModelException {
		final ArrayList<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

		entries.add(JavaCore.newSourceEntry(source.getFullPath()));

		final String ee = properties.getProperty(GeneratorProperties.EXECUTION_ENVIRONMENT);
		if (ee != null) {
			entries.add(JavaCore.newContainerEntry(getEEPath(ee)));
		} else {
			entries.add(JavaCore.newContainerEntry(JavaRuntime.newDefaultJREContainerPath()));
		}

		entries.add(JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins"))); //$NON-NLS-1$

		return entries.toArray(new IClasspathEntry[] {});
	}
}
