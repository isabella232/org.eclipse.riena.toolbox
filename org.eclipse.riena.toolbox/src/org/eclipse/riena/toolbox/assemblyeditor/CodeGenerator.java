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
package org.eclipse.riena.toolbox.assemblyeditor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.api.ICodeGenerator;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;


public class CodeGenerator implements ICodeGenerator {
	private static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$
	private static final String DIR_TEMPLATES = "templates"; //$NON-NLS-1$
	private static final String PACKAGE_SEPARATOR = "."; //$NON-NLS-1$
	private static final String VIEW_SUFFIX = "View"; //$NON-NLS-1$
	private static final String CONTROLLER_SUFFIX = "Controller"; //$NON-NLS-1$
	private static final String DEFAULT_PACKAGE_VIEWS = "views"; //$NON-NLS-1$
	private static final String DEFAULT_PACKAGE_CONTROLLER = "controller"; //$NON-NLS-1$
	private static final String TEMPLATE_SUB_MODULE_VIEW = "SubModuleView.java"; //$NON-NLS-1$
	private static final String TEMPLATE_SUB_MODULE_CONTROLLER = "SubModuleController.java"; //$NON-NLS-1$
	private static final String VAR_CLASS_NAME = "ClassName"; //$NON-NLS-1$
	private static final String VAR_PACKAGE_NAME = "PackageName"; //$NON-NLS-1$

	private VelocityEngine velocityEngine;

	public CodeGenerator() {
		Properties p = new Properties();
		p.setProperty("resource.loader", "file"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("file.resource.loader.path", getBaseDir().getAbsolutePath()); //$NON-NLS-1$

		try {
			velocityEngine = new VelocityEngine(p);
			velocityEngine.init();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public String generateController(SubModuleNode subModule) {
		String packageName = subModule.getBundle().getName() + PACKAGE_SEPARATOR + DEFAULT_PACKAGE_CONTROLLER;
		packageName = packageName.toLowerCase();
		String className = subModule.getName()+CONTROLLER_SUFFIX;
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(VAR_CLASS_NAME, className); 
		properties.put(VAR_PACKAGE_NAME, packageName);
		String fullClassName = generateClass(packageName, className,  subModule, TEMPLATE_SUB_MODULE_CONTROLLER, properties);
		return fullClassName;
	}

	public RCPView generateView(SubModuleNode subModule) {
		String packageName = subModule.getBundle().getName() + PACKAGE_SEPARATOR + DEFAULT_PACKAGE_VIEWS;
		packageName = packageName.toLowerCase();
		
		String className = subModule.getName()+VIEW_SUFFIX;
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(VAR_CLASS_NAME, className);
		properties.put(VAR_PACKAGE_NAME, packageName);
		String classFileName = generateClass(packageName, className, subModule, TEMPLATE_SUB_MODULE_VIEW, properties);
		
		RCPView view = new RCPView();
		view.setViewClass(classFileName);
		view.setId(classFileName);
		view.setName(classFileName);
		return view;
	}

	private boolean createFile(IFile outFile, String data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
		
		if (!outFile.exists()){
			try {
				outFile.create(bis, true, null);
				return true;
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		return false;
	}
	
	private File getBaseDir() {
		try {
			File bundle = FileLocator.getBundleFile(Activator.getDefault().getBundle());
			File template = new File(bundle.getAbsolutePath() + File.separator+ DIR_TEMPLATES);
			return template;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateClass(String packageName, String className, SubModuleNode subModule, String templateName, Map<String, String> properties) {
		StringWriter writer = null;
		try {
			Template t = velocityEngine.getTemplate(templateName);
			VelocityContext context = new VelocityContext();

			for (Iterator<Entry<String, String>> it = properties.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				context.put(entry.getKey(), entry.getValue());
			}

			writer = new StringWriter();
			t.merge(context, writer);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		IFolder packageFolder = subModule.getBundle().getProject().getFolder(subModule.getBundle().getSourceFolder()+File.separator + packageName.replace(PACKAGE_SEPARATOR, File.separator));
		createFolder(packageFolder);
		IFile classFile = packageFolder.getFile(className + EXTENSION_JAVA);
		createFile(classFile, writer.toString());
		return packageName+PACKAGE_SEPARATOR+className;
	}
	
	private void createFolder(IFolder folder)
	{
	  IContainer parent = folder.getParent();
	  if (parent instanceof IFolder){
	    createFolder((IFolder)parent);
	  }
	  if (!folder.exists())
	  {
	    try {
			folder.create(true, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	  }
	}
	
	private boolean deleteSourceFile(SubModuleNode subModule, String className){
		if (!Util.isGiven(className)){
			System.err.println("ClassName is null subModule " + subModule);
			return false;
		}
		 
		
		IProject project = subModule.getBundle().getProject();
		String fileName  = subModule.getBundle().getSourceFolder()+File.separator+className.replace(PACKAGE_SEPARATOR, File.separator)+EXTENSION_JAVA;
		
		IFile file = project.getFile(fileName);
		
		if (file.exists()){
			try {
				file.delete(true, null);
				return true;
			} catch (CoreException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return false;
	}

	public void deleteControllerClass(SubModuleNode subModule) {
		if (null == subModule){
			return;
		}
		
		deleteSourceFile(subModule, subModule.getController());
	}

	public void deleteViewClass(SubModuleNode subModule) {
		if (null == subModule){
			return;
		}
		
		if (null == subModule.getRcpView()){
			return;
		}
		
		deleteSourceFile(subModule, subModule.getRcpView().getViewClass());
	}
}
