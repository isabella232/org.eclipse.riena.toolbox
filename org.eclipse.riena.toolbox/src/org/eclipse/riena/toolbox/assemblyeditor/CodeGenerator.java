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
package org.eclipse.riena.toolbox.assemblyeditor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.ui.CodeGeneration;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.api.ICodeGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.preferences.PreferenceConstants;

@SuppressWarnings("restriction")
public class CodeGenerator implements ICodeGenerator {
	/**
	 *
	 */
	private static final String CONST_CLASS_IDENT = "public class"; //$NON-NLS-1$
	private static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$
	private static final String DIR_TEMPLATES = "templates"; //$NON-NLS-1$
	private static final String PACKAGE_SEPARATOR = "."; //$NON-NLS-1$
	private static final String VIEW_SUFFIX = "View"; //$NON-NLS-1$
	private static final String CONTROLLER_SUFFIX = "Controller"; //$NON-NLS-1$
	private static final String TEMPLATE_SUB_MODULE_VIEW = "SubModuleView.java"; //$NON-NLS-1$
	private static final String TEMPLATE_SUB_MODULE_CONTROLLER = "SubModuleController.java"; //$NON-NLS-1$
	private static final String VAR_CLASS_NAME = "ClassName"; //$NON-NLS-1$
	private static final String VAR_PACKAGE_NAME = "PackageName"; //$NON-NLS-1$

	private VelocityEngine velocityEngine;
	private final String baseAbsolutePath;

	public CodeGenerator() {
		final Properties p = new Properties();
		baseAbsolutePath = getBaseDir();
		if (baseAbsolutePath.contains(".jar!")) { //$NON-NLS-1$
			p.setProperty("resource.loader", "url"); //$NON-NLS-1$ //$NON-NLS-2$
			p.setProperty("url.resource.loader.class", "org.apache.velocity.runtime.resource.loader.URLResourceLoader "); //$NON-NLS-1$ //$NON-NLS-2$
			p.setProperty("url.resource.loader.root", baseAbsolutePath); //$NON-NLS-1$
		} else {
			p.setProperty("resource.loader", "file"); //$NON-NLS-1$ //$NON-NLS-2$
			p.setProperty(
					"class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader"); //$NON-NLS-1$ //$NON-NLS-2$
			p.setProperty("file.resource.loader.path", baseAbsolutePath); //$NON-NLS-1$
		}

		try {
			velocityEngine = new VelocityEngine(p);
			velocityEngine.init();
		} catch (final Exception e) {
			Util.showError(e);
		}
	}

	private String getPreference(final String key) {
		String defaultPackageController = Activator.getDefault().getPreferenceStore().getString(key);
		if (!Util.isGiven(defaultPackageController)) {
			defaultPackageController = Activator.getDefault().getPreferenceStore().getDefaultString(key);
		}
		return defaultPackageController;
	}

	public String generateController(final SubModuleNode subModule) {
		final String defaultPackageController = getPreference(PreferenceConstants.CONST_GENERATE_CONTROLLER_PACKAGE_NAME);
		String packageName = subModule.getBundle().getName() + PACKAGE_SEPARATOR + defaultPackageController;
		packageName = packageName.toLowerCase();
		final String className = Util.cleanNodeId(subModule.getName(), false) + CONTROLLER_SUFFIX;

		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(VAR_CLASS_NAME, className);
		properties.put(VAR_PACKAGE_NAME, packageName);
		final String fullClassName = generateClass(packageName, className, subModule, TEMPLATE_SUB_MODULE_CONTROLLER,
				properties);
		return fullClassName;
	}

	public RCPView generateView(final SubModuleNode subModule) {
		final String defaultPackageView = getPreference(PreferenceConstants.CONST_GENERATE_VIEW_PACKAGE_NAME);
		String packageName = subModule.getBundle().getName() + PACKAGE_SEPARATOR + defaultPackageView;
		packageName = packageName.toLowerCase();

		final String className = Util.cleanNodeId(subModule.getName(), false) + VIEW_SUFFIX;

		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(VAR_CLASS_NAME, className);
		properties.put(VAR_PACKAGE_NAME, packageName);
		final String classFileName = generateClass(packageName, className, subModule, TEMPLATE_SUB_MODULE_VIEW,
				properties);

		final RCPView view = new RCPView();
		view.setViewClass(classFileName);
		view.setId(classFileName);
		view.setName(classFileName);
		return view;
	}

	private boolean createFile(final IFile outFile, final String data) {
		final ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());

		if (!outFile.exists()) {
			try {
				outFile.create(bis, true, null);
				return true;
			} catch (final CoreException e) {
				Util.showError(e);
			}
		}
		return false;
	}

	private boolean modifyFile(final IFile outFile, final String data) {
		final ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());

		if (!outFile.exists()) {
			return createFile(outFile, data);
		}

		try {
			outFile.setContents(bis, IFile.FORCE, null);
			return true;
		} catch (final CoreException e) {
			Util.showError(e);
		}
		return false;
	}

	private String getBaseDir() {
		try {
			final File bundle = FileLocator.getBundleFile(Activator.getDefault().getBundle());
			String bundleAbsolutePath = bundle.getAbsolutePath();
			if (bundleAbsolutePath.endsWith(".jar")) { //$NON-NLS-1$
				bundleAbsolutePath = bundleAbsolutePath.replace('\\', '/');
				if (bundleAbsolutePath.startsWith("/")) { //$NON-NLS-1$
					bundleAbsolutePath = bundleAbsolutePath.substring(1);
				}
				bundleAbsolutePath = "jar:file:/" + bundleAbsolutePath + "!/" + DIR_TEMPLATES + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return bundleAbsolutePath;
			}
			return bundleAbsolutePath + "/" + DIR_TEMPLATES; //$NON-NLS-1$
		} catch (final IOException e) {
			Util.showError(e);
			throw new RuntimeException(e);
		}
	}

	private String generateClass(final String packageName, final String className, final SubModuleNode subModule,
			final String templateName, final Map<String, String> properties) {
		StringWriter writer = null;
		try {
			final Template t = velocityEngine.getTemplate(templateName);
			final VelocityContext context = new VelocityContext();

			for (final Entry<String, String> entry : properties.entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}

			writer = new StringWriter();
			t.merge(context, writer);

		} catch (final Exception e) {
			Util.showError(e);
			throw new RuntimeException("exception in generateClass: basePath =" + baseAbsolutePath, e); //$NON-NLS-1$
		}

		final IFolder packageFolder = subModule
				.getBundle()
				.getProject()
				.getFolder(
						subModule.getBundle().getSourceFolder() + File.separator
								+ packageName.replace(PACKAGE_SEPARATOR, File.separator));

		createFolder(packageFolder);
		final IFile classFile = packageFolder.getFile(className + EXTENSION_JAVA);
		createFile(classFile, writer.toString());

		final String fullClassName = packageName + PACKAGE_SEPARATOR + className;
		generateComments(classFile, subModule, fullClassName, writer);
		return fullClassName;
	}

	private void generateComments(final IFile classFile, final SubModuleNode subModule, final String fullClassName,
			final StringWriter writer) {
		final RidgetGenerator ridgetGenerator = new RidgetGenerator(subModule.getBundle().getProject());
		final ICompilationUnit newClassCompilationUnit = ridgetGenerator.findICompilationUnit(fullClassName);

		final IJavaProject javaProject = JavaCore.create(subModule.getBundle().getProject());
		final String lineDelimiter = StubUtility.getLineDelimiterUsed(javaProject);

		try {
			final String[] typeParamNames = new String[0];
			final String comment = getTypeComment(fullClassName, newClassCompilationUnit, lineDelimiter, typeParamNames);
			final String fileComment = getFileComment(newClassCompilationUnit, lineDelimiter);
			final StringBuffer classContent = writer.getBuffer().insert(0, fileComment + lineDelimiter);

			final String cleanContent = classContent.toString().replace(CONST_CLASS_IDENT,
					comment + lineDelimiter + CONST_CLASS_IDENT);

			String formattedContent = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, cleanContent,
					0, lineDelimiter, javaProject);
			formattedContent = Strings.trimLeadingTabsAndSpaces(formattedContent);
			modifyFile(classFile, formattedContent);
		} catch (final CoreException e) {
			Util.showError(e);
		}
	}

	private String getTypeComment(final String fullClassName, final ICompilationUnit newClassCompilationUnit,
			final String lineDelimiter, final String[] typeParamNames) throws CoreException {

		try {
			return CodeGeneration.getTypeComment(newClassCompilationUnit, fullClassName, typeParamNames, lineDelimiter);
		} catch (final NullPointerException e) {
			return ""; //$NON-NLS-1$
		}
	}

	private String getFileComment(final ICompilationUnit newClassCompilationUnit, final String lineDelimiter)
			throws CoreException {

		try {
			return CodeGeneration.getFileComment(newClassCompilationUnit, lineDelimiter);
		} catch (final NullPointerException e) {
			return ""; //$NON-NLS-1$
		}
	}

	private void createFolder(final IFolder folder) {
		final IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}
		if (!folder.exists()) {
			try {
				folder.create(true, true, null);
			} catch (final CoreException e) {
				Util.showError(e);
			}
		}
	}

	private boolean deleteSourceFile(final SubModuleNode subModule, final String className) {
		if (!Util.isGiven(className)) {
			Util.logWarning("ClassName is null subModule " + subModule); //$NON-NLS-1$
			return false;
		}

		final IProject project = subModule.getBundle().getProject();
		final String fileName = subModule.getBundle().getSourceFolder() + File.separator
				+ className.replace(PACKAGE_SEPARATOR, File.separator) + EXTENSION_JAVA;

		final IFile file = project.getFile(fileName);

		if (file.exists()) {
			try {
				file.delete(true, null);
				return true;
			} catch (final CoreException e) {
				Util.showError(e);
			}
		}
		return false;
	}

	public void deleteControllerClass(final SubModuleNode subModule) {
		if (null == subModule) {
			return;
		}

		deleteSourceFile(subModule, subModule.getController());
	}

	public void deleteViewClass(final SubModuleNode subModule) {
		if (null == subModule) {
			return;
		}

		if (null == subModule.getRcpView()) {
			return;
		}

		deleteSourceFile(subModule, subModule.getRcpView().getViewClass());
	}
}
