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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class AbstractXmlProvider {
	protected static final String ATTR_SUBMOD_SELECTABLE = "selectable"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_VIEW = "view"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_TYPE_ID = "typeId"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_LABEL = "label"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_CONTROLLER = "controller"; //$NON-NLS-1$
	protected static final String ATTR_MODULE_UNCLOSABLE = "unclosable"; //$NON-NLS-1$
	protected static final String ATTR_MODULE_NAME = ATTR_SUBMOD_NAME;
	protected static final String ATTR_MODULE_LABEL = ATTR_SUBMOD_LABEL;
	protected static final String ATTR_MODGROUP_TYPE_ID = ATTR_SUBMOD_TYPE_ID;
	protected static final String ATTR_MODGROUP_NAME = ATTR_MODULE_NAME;
	protected static final String ATTR_SUBAPP_VIEW = ATTR_SUBMOD_VIEW;
	protected static final String ATTR_SUBAPP_TYPE_ID = ATTR_MODGROUP_TYPE_ID;
	protected static final String ATTR_SUBAPP_LABEL = ATTR_MODULE_LABEL;
	protected static final String ATTR_ASSEMBLY_ID = "id"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_PARENT_TYPE_ID = "parentTypeId"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_AUTOSTARTSEQUENCE = "autostartsequence"; //$NON-NLS-1$
	protected static final String ELEM_ASSEMBLY = "assembly"; //$NON-NLS-1$
	protected static final String ELEM_SUBAPPLICATION = "subapplication"; //$NON-NLS-1$
	protected static final String ELEM_MODULE_GROUP = "modulegroup"; //$NON-NLS-1$
	protected static final String ELEM_MODULE = "module"; //$NON-NLS-1$
	protected static final String ELEM_SUBMODULE = "submodule"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_REF = "ref"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_ASSEMBLER = "assembler"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_ICON = "icon"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_INSTANCE_ID = "instanceId"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_MODGROUP_INSTANCE_ID = ATTR_SUBAPP_INSTANCE_ID;
	protected static final String ATTR_MODULE_ICON = ATTR_SUBAPP_ICON;
	protected static final String ATTR_MODULE_INSTANCE_ID = ATTR_MODGROUP_INSTANCE_ID;
	protected static final String ATTR_MODULE_TYPE_ID = "typeId"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_SHARED = "shared"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_ICON = ATTR_MODULE_ICON;
	protected static final String ATTR_SUBMOD_INSTANCE_ID = ATTR_MODULE_INSTANCE_ID;
	protected static final String ATTR_EXTENSION_POINT = "point"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_ALLOW_MULTIPLE = "allowMultiple"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_CLASS = "class"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_ID = "id"; //$NON-NLS-1$
	
	
	protected static final String ELEM_PLUGIN = "plugin"; //$NON-NLS-1$
	protected static final String ELEM_EXTENSION = "extension"; //$NON-NLS-1$
	protected static final String ELEM_VIEW = "view"; //$NON-NLS-1$
	protected static final String ELEM_PERSPECTIVE = "perspective"; //$NON-NLS-1$
	protected static final String ELEM_POINT = "point"; //$NON-NLS-1$
	
	protected static final String VALUE_EXT_POINT_ORG_ASSEMBLIES = "org.eclipse.riena.navigation.assemblies"; //$NON-NLS-1$
	protected static final String VALUE_EXT_POINT_ASSEMBLIES = "org.eclipse.riena.navigation.assemblies"; //$NON-NLS-1$
	protected static final String VALUE_EXT_POINT_VIEWS = "org.eclipse.ui.views"; //$NON-NLS-1$
	protected static final String VALUE_EXT_POINT_PERSPECTIVES = "org.eclipse.ui.perspectives"; //$NON-NLS-1$
	
	
	
	protected DocumentBuilder builder;
	protected BundleNode bundleNode;

	public AbstractXmlProvider() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(false);
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected File convertToFile(IFile pluginXml){
		return new File(bundleNode.getPluginXml().getLocationURI());
	}
	
	
	protected void saveDocument(Document doc, BundleNode bundleNode){
		try {
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(new DOMSource(doc), new StreamResult(convertToFile(bundleNode.getPluginXml())));
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Document getDocument(BundleNode bundleNode){
		this.bundleNode = bundleNode;
		try {
			return builder.parse(new FileInputStream(convertToFile(bundleNode.getPluginXml())));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected static boolean parseBoolean(Element elm, String attributeName) {
		String attr = elm.getAttribute(attributeName);
		if (null != attr) {
			return "true".equals(attr); //$NON-NLS-1$
		}
		return false;
	}
	
	protected static Integer parseInteger(Element elm, String attributeName) {
		String attr = elm.getAttribute(attributeName);
		if (Util.isGiven(attr)) {
			return new Integer(Integer.parseInt(attr));
		}
		return null;
	}

	protected Element getFirstChild(Element rootElement, String childElementName) {
		NodeList elementList = rootElement.getChildNodes();

		for (int i = 0; i < elementList.getLength(); i++) {
			Node node = elementList.item(i);

			if (node instanceof Element) {
				if (node.getNodeName().equals(childElementName)) {
					return (Element) node;
				}
			}
		}
		return null;
	}

	protected abstract class NodeIterator {
		private final Element rootElement;
		private final String childElementName;

		public NodeIterator(Element element, String childElement) {
			this.rootElement = element;
			this.childElementName = childElement;
		}

		public void iterate() {
			NodeList elementList = rootElement.getChildNodes();
			for (int i = 0; i < elementList.getLength(); i++) {
				Node childNode =  elementList.item(i);
				if (childNode instanceof Element && childNode.getNodeName().equals(childElementName)){
					Element elm = (Element) childNode;
					handle(elm);
				}
			}
		}
		public abstract void handle(Element childElement);
	}
}
