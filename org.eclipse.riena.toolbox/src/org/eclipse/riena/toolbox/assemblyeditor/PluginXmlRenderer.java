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
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.api.IPluginXmlRenderer;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PluginXmlRenderer extends AbstractXmlProvider implements IPluginXmlRenderer {
	private Document doc;

	public PluginXmlRenderer() {
	}

	// FIXME redundant saveDocument-Method
	public void saveDocument(BundleNode bundleNode) {
		try {
			File pluginXml = new File(bundleNode.getPluginXml().getLocationURI());

			doc = builder.parse(pluginXml);
			removeOldAssemblies();
			
			// render the given BundleNode, unless it has no childelemetns
			if (bundleNode.getChildren() != null && !bundleNode.getChildren().isEmpty()) {
				Element elmExt = doc.createElement(ELEM_EXTENSION);
				elmExt.setAttribute(ATTR_EXTENSION_POINT, VALUE_EXT_POINT_ASSEMBLIES);

				NodeList nlPlugin = doc.getElementsByTagName(ELEM_PLUGIN);
				nlPlugin.item(0).appendChild(elmExt);
				System.out.println("saveDocument " + bundleNode);
				renderModel(elmExt, bundleNode);
			}
			
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(new DOMSource(doc), new StreamResult(pluginXml));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	private void renderRcpView(Element elmView, RCPView view) {
		elmView.setAttribute(ATTR_VIEW_NAME, view.getName());
		elmView.setAttribute(ATTR_VIEW_ID, view.getId());
		elmView.setAttribute(ATTR_VIEW_CLASS, view.getViewClass());
		elmView.setAttribute(ATTR_VIEW_ALLOW_MULTIPLE, view.isAllowMultiple() + ""); //$NON-NLS-1$
	}

	/*
	 * <perspective
	 * class="org.eclipse.riena.navigation.ui.swt.views.SubApplicationView"
	 * id="org.eclipse.riena.toolbox.rienademo.mainSubapp" name="nameMain"/>
	 */

	private void renderRcpPerspective(Element elmView, RCPPerspective persp) {
		elmView.setAttribute("class", persp.getPerspectiveClass());
		elmView.setAttribute("id", persp.getId());
		elmView.setAttribute("name", persp.getName());
	}

	public boolean registerPerspective(BundleNode bundle, RCPPerspective perspective) {
		Document currentDoc = getDocument(bundle);

		if (currentDoc == null) {
			return false;
		}

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nlViewExtensions = (NodeList) xpath.evaluate(String.format(
					"//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES), currentDoc, XPathConstants.NODESET); //$NON-NLS-1$

			Element elmPerspectiveExt = null;

			// if extensionpoint does not exist, create it
			if (nlViewExtensions.getLength() == 0) {
				elmPerspectiveExt = currentDoc.createElement(ELEM_EXTENSION);
				elmPerspectiveExt.setAttribute(ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES);

				NodeList nlRoot = currentDoc.getElementsByTagName(ELEM_PLUGIN);
				nlRoot.item(0).appendChild(elmPerspectiveExt);
			} else {
				elmPerspectiveExt = (Element) nlViewExtensions.item(0);
			}

			if (!perspectiveAlreadyExists(currentDoc, perspective.getId())) {
				Element elmPerspective = currentDoc.createElement(ELEM_PERSPECTIVE);
				renderRcpPerspective(elmPerspective, perspective);
				elmPerspectiveExt.appendChild(elmPerspective);
				saveDocument(currentDoc, bundleNode);
				return true;
			}
			return false;

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean registerView(BundleNode bundle, RCPView view) {
		Document currentDoc = getDocument(bundle);

		if (currentDoc == null) {
			return false;
		}

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nlViewExtensions = (NodeList) xpath.evaluate(String.format(
					"//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS), currentDoc, XPathConstants.NODESET); //$NON-NLS-1$

			Element elmViewExt = null;

			// if extensionpoint does not exist, create it
			if (nlViewExtensions.getLength() == 0) {
				elmViewExt = currentDoc.createElement(ELEM_EXTENSION);
				elmViewExt.setAttribute(ELEM_POINT, VALUE_EXT_POINT_VIEWS);

				NodeList nlRoot = currentDoc.getElementsByTagName(ELEM_PLUGIN);
				nlRoot.item(0).appendChild(elmViewExt);
			} else {
				elmViewExt = (Element) nlViewExtensions.item(0);
			}

			if (!viewAlreadyExists(currentDoc, view.getId())) {
				Element elmView = currentDoc.createElement(ELEM_VIEW);
				renderRcpView(elmView, view);
				elmViewExt.appendChild(elmView);
				saveDocument(currentDoc, bundleNode);
				return true;
			}
			return false;

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean viewAlreadyExists(Document currentDoc, String viewId) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		// look for an element view with an ID = viewId anywhere under an
		// extensionpoint org.eclipse.ui.views
		String xpathQuery = String.format("//%s[@%s='%s']/view[@id='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS, viewId); //$NON-NLS-1$
		NodeList nlViewExtensions = (NodeList) xpath.evaluate(xpathQuery, currentDoc, XPathConstants.NODESET);
		return nlViewExtensions.getLength() > 0;
	}

	private boolean perspectiveAlreadyExists(Document currentDoc, String perspectiveId) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		// look for an element perspective with an ID = perspectiveId anywhere
		// under an extensionpoint org.eclipse.ui.perspectives
		String xpathQuery = String.format(
				"//%s[@%s='%s']/view[@id='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES, perspectiveId); //$NON-NLS-1$
		NodeList nlViewExtensions = (NodeList) xpath.evaluate(xpathQuery, currentDoc, XPathConstants.NODESET);
		return nlViewExtensions.getLength() > 0;
	}

	private void renderModel(Element elmExtension, BundleNode bundleNode) {
		for (AssemblyNode ass : bundleNode.getChildren()) {
			Element elmAss = renderAssembly(elmExtension, ass);

			for (AbstractAssemblyNode child : ass.getChildren()) {
				if (child instanceof SubApplicationNode) {
					renderSubApplication(elmAss, (SubApplicationNode) child);
				} else if (child instanceof ModuleGroupNode) {
					renderModuleGroup(elmAss, (ModuleGroupNode) child);
				} else if (child instanceof ModuleNode) {
					renderModule(elmAss, (ModuleNode) child);
				} else if (child instanceof SubModuleNode) {
					renderSubModule(elmAss, (SubModuleNode) child);
				}
			}
		}
	}

	private Element renderAssembly(Element elmExtension, AssemblyNode ass) {
		Element elm = doc.createElement(ELEM_ASSEMBLY);
		elm.setAttribute(ATTR_ASSEMBLY_ID, ass.getId());

		if (Util.isGiven(ass.getAssembler())) {
			elm.setAttribute(ATTR_ASSEMBLY_ASSEMBLER, ass.getAssembler());
		}

		if (Util.isGiven(ass.getNodeTypeId())) {
			elm.setAttribute(ATTR_ASSEMBLY_PARENT_NODE_ID, ass.getNodeTypeId());
		}

		if (Util.isGiven(ass.getName())) {
			elm.setAttribute(ATTR_ASSEMBLY_NAME, ass.getName());
		}

		if (null != ass.getAutostartSequence()) {
			elm.setAttribute(ATTR_ASSEMBLY_START_ORDER, ass.getAutostartSequence().intValue() + ""); //$NON-NLS-1$
		}

		elmExtension.appendChild(elm);
		return elm;

	}

	private void renderSubApplication(Element parent, SubApplicationNode ass) {
		Element elm = doc.createElement(ELEM_SUBAPP);

		if (Util.isGiven(ass.getName())) {
			elm.setAttribute(ATTR_SUBAPP_NAME, ass.getName());
		}

		if (Util.isGiven(ass.getNodeId())) {
			elm.setAttribute(ATTR_SUBAPP_NODE_ID, ass.getNodeId());
		}

		if (Util.isGiven(ass.getIcon())) {
			elm.setAttribute(ATTR_SUBAPP_ICON, ass.getIcon());
		}

		if (Util.isGiven(ass.getPerspective())) {
			elm.setAttribute(ATTR_SUBAPP_PERSPECTIVE_ID, ass.getPerspective());
		}

		for (ModuleGroupNode mod : ass.getChildren()) {
			renderModuleGroup(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void renderModuleGroup(Element parent, ModuleGroupNode ass) {
		Element elm = doc.createElement(ELEM_MODULE_GROUP);

		if (Util.isGiven(ass.getName())) {
			elm.setAttribute(ATTR_MODGROUP_NAME, ass.getName());
		}

		if (Util.isGiven(ass.getNodeId())) {
			elm.setAttribute(ATTR_MODGROUP_NODE_ID, ass.getNodeId());
		}


		for (ModuleNode mod : ass.getChildren()) {
			renderModule(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void renderModule(Element parent, ModuleNode ass) {
		Element elm = doc.createElement(ELEM_MODULE);


		if (Util.isGiven(ass.getName())) {
			elm.setAttribute(ATTR_MODULE_NAME, ass.getName());
		}

		if (Util.isGiven(ass.getNodeId())) {
			elm.setAttribute(ATTR_MODULE_NODE_ID, ass.getNodeId());
		}


		if (Util.isGiven(ass.getIcon())) {
			elm.setAttribute(ATTR_MODULE_ICON, ass.getIcon());
		}

		elm.setAttribute(ATTR_MODULE_CLOSABLE, ass.isCloseable() + ""); //$NON-NLS-1$

		for (SubModuleNode mod : ass.getChildren()) {
			renderSubModule(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void renderSubModule(Element parent, SubModuleNode subMod) {
		Element elm = doc.createElement(ELEM_SUBMODULE);

		if (Util.isGiven(subMod.getNodeId())) {
			elm.setAttribute(ATTR_SUBMOD_NODE_ID, subMod.getNodeId());
		}
		
		if (Util.isGiven(subMod.getController())) {
			elm.setAttribute(ATTR_SUBMOD_CONTROLLER, subMod.getController());
		}

		if (Util.isGiven(subMod.getName())) {
			elm.setAttribute(ATTR_SUBMOD_NAME, subMod.getName());
		}

		elm.setAttribute(ATTR_SUBMOD_SELECTABLE, subMod.isSelectable() + ""); //$NON-NLS-1$

		if (Util.isGiven(subMod.getRcpView().getId())) {
			elm.setAttribute(ATTR_SUBMOD_VIEW, subMod.getRcpView().getId());
		}

		if (Util.isGiven(subMod.getIcon())) {
			elm.setAttribute(ATTR_SUBMOD_ICON, subMod.getIcon());
		}

		elm.setAttribute(ATTR_SUBMOD_SHARED, subMod.isShared() + ""); //$NON-NLS-1$
		
		elm.setAttribute(ATTR_SUBMOD_REQUIRES_PREPARATION, subMod.isRequiresPreparation() + ""); //$NON-NLS-1$

		for (SubModuleNode mod : subMod.getChildren()) {
			renderSubModule(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void removeOldAssemblies() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nlExt = (NodeList) xpath.evaluate(String.format(
					"//%s[@%s='%s']", ELEM_EXTENSION, ATTR_EXTENSION_POINT, VALUE_EXT_POINT_ASSEMBLIES), doc, XPathConstants.NODESET); //$NON-NLS-1$
			for (int i = 0; i < nlExt.getLength(); i++) {
				Node noAss = nlExt.item(i);
				noAss.getParentNode().removeChild(noAss);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
}
