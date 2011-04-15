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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.eclipse.core.resources.IFile;

import org.eclipse.riena.core.util.Nop;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;

public class AbstractXmlProvider {

	// ######### SubModule
	protected static final String ELEM_SUBMODULE = "subModule"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_SELECTABLE = "selectable"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_VIEW = "viewId"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_NODE_ID = "nodeId"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_SHARED_VIEW = "sharedView"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_ICON = "icon"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_CONTROLLER = "controller"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_REQUIRES_PREPARATION = "requiresPreparation"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_EXPANDED = "expanded"; //$NON-NLS-1$
	protected static final String ATTR_SUBMOD_VISIBLE = "visible"; //$NON-NLS-1$

	// ##### Module
	protected static final String ELEM_MODULE = "module"; //$NON-NLS-1$
	protected static final String ATTR_MODULE_CLOSABLE = "closable"; //$NON-NLS-1$
	protected static final String ATTR_MODULE_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_MODULE_ICON = "icon"; //$NON-NLS-1$
	protected static final String ATTR_MODULE_NODE_ID = "nodeId"; //$NON-NLS-1$

	// ########## ModuleGroup
	protected static final String ELEM_MODULE_GROUP = "moduleGroup"; //$NON-NLS-1$
	protected static final String ATTR_MODGROUP_NODE_ID = "nodeId"; //$NON-NLS-1$
	protected static final String ATTR_MODGROUP_NAME = "name"; //$NON-NLS-1$

	// ##########   Subapplication
	protected static final String ELEM_SUBAPP = "subApplication"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_PERSPECTIVE_ID = "perspectiveId"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_NODE_ID = "nodeId"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_ICON = "icon"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_INSTANCE_ID = "instanceId"; //$NON-NLS-1$
	protected static final String ATTR_SUBAPP_NAME = "name"; //$NON-NLS-1$

	// ######## Assembly
	protected static final String ELEM_ASSEMBLY = "assembly2"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_ID = "id"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_PARENT_NODE_ID = "parentNodeId"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_START_ORDER = "startOrder"; //$NON-NLS-1$
	protected static final String ATTR_ASSEMBLY_ASSEMBLER = "assembler"; //$NON-NLS-1$

	protected static final String ATTR_EXTENSION_POINT = "point"; //$NON-NLS-1$

	// ###### RCP-View
	protected static final String ATTR_VIEW_ALLOW_MULTIPLE = "allowMultiple"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_CLASS = "class"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_VIEW_ID = "id"; //$NON-NLS-1$

	// ##### RCP-Perspective
	protected static final String ATTR_PERSPECTIVE_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTR_PERSPECTIVE_ID = "id"; //$NON-NLS-1$
	protected static final String ATTR_PERSPECTIVE_CLASS = "class"; //$NON-NLS-1$

	protected static final String ELEM_PLUGIN = "plugin"; //$NON-NLS-1$
	protected static final String ELEM_EXTENSION = "extension"; //$NON-NLS-1$
	protected static final String ELEM_VIEW = "view"; //$NON-NLS-1$
	protected static final String ELEM_PERSPECTIVE = "perspective"; //$NON-NLS-1$
	protected static final String ELEM_POINT = "point"; //$NON-NLS-1$

	// ###### Extensionpoint Values
	protected static final String VALUE_EXT_POINT_ASSEMBLIES = "org.eclipse.riena.navigation.assemblies2"; //$NON-NLS-1$
	protected static final String VALUE_EXT_POINT_VIEWS = "org.eclipse.ui.views"; //$NON-NLS-1$
	protected static final String VALUE_EXT_POINT_PERSPECTIVES = "org.eclipse.ui.perspectives"; //$NON-NLS-1$

	protected DocumentBuilder builder;
	protected BundleNode bundleNode;

	public AbstractXmlProvider() {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(false);
		dbf.setIgnoringElementContentWhitespace(true);

		try {
			builder = dbf.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	protected File convertToFile(final IFile pluginXml) {
		return new File(bundleNode.getPluginXml().getLocationURI());
	}

	protected Transformer createTransformer() throws TransformerException {
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();

		try {
			transformerFactory.setAttribute("indent-number", new Integer(4)); //$NON-NLS-1$
		} catch (final IllegalArgumentException exception) {
			Nop.reason("empty"); //$NON-NLS-1$
		}

		final Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$

		// Unless a width is set, there will be only line breaks but no indentation.
		// The IBM JDK and the Sun JDK don't agree on the property name,
		// so we set them both.
		//
		transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
		return transformer;
	}

	protected void saveDocument(final Document doc, final BundleNode bundleNode) {
		try {
			final Transformer xformer = createTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(convertToFile(bundleNode.getPluginXml())));
		} catch (final TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	protected Document getDocument(final BundleNode bundleNode) {
		this.bundleNode = bundleNode;
		try {
			return builder.parse(new FileInputStream(convertToFile(bundleNode.getPluginXml())));
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static boolean parseBoolean(final Element elm, final String attributeName, final boolean defaultValue) {
		final String attr = elm.getAttribute(attributeName);
		if (null != attr && attr.length() > 0) {
			return "true".equals(attr); //$NON-NLS-1$
		}
		return defaultValue;
	}

	protected static Integer parseInteger(final Element elm, final String attributeName) {
		final String attr = elm.getAttribute(attributeName);
		if (Util.isGiven(attr)) {
			return new Integer(Integer.parseInt(attr));
		}
		return null;
	}

	protected Element getFirstChild(final Element rootElement, final String childElementName) {
		final NodeList elementList = rootElement.getChildNodes();

		for (int i = 0; i < elementList.getLength(); i++) {
			final Node node = elementList.item(i);

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
		private final List<String> childElementNames;

		public NodeIterator(final Element element, final String... childElements) {
			this.rootElement = element;
			this.childElementNames = Arrays.asList(childElements);
		}

		public void iterate() {
			final NodeList elementList = rootElement.getChildNodes();
			for (int i = 0; i < elementList.getLength(); i++) {
				final Node childNode = elementList.item(i);
				if (childNode instanceof Element && childElementNames.contains(childNode.getNodeName())) {
					final Element elm = (Element) childNode;
					handle(elm);
				}
			}
		}

		public abstract void handle(Element childElement);
	}
}
