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
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.eclipse.core.runtime.CoreException;

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

public class PluginXmlRenderer extends AbstractXmlProvider implements IPluginXmlRenderer {
	private Document doc;

	public PluginXmlRenderer() {
	}

	// FIXME redundant saveDocument-Method
	public void saveDocument(final BundleNode bundleNode) {
		try {
			final File pluginXml = new File(bundleNode.getPluginXml().getLocationURI());

			doc = builder.parse(pluginXml);
			removeOldAssemblies();

			// render the given BundleNode, unless it has no childelements
			if (bundleNode.getChildren() != null && !bundleNode.getChildren().isEmpty()) {
				final Element elmExt = doc.createElement(ELEM_EXTENSION);
				elmExt.setAttribute(ATTR_EXTENSION_POINT, VALUE_EXT_POINT_ASSEMBLIES);

				final NodeList nlPlugin = doc.getElementsByTagName(ELEM_PLUGIN);
				nlPlugin.item(0).appendChild(elmExt);
				renderModel(elmExt, bundleNode);
			}

			cleanWhiteSpace();

			final Transformer xformer = createTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(pluginXml));
			bundleNode.refreshPluginXml();
		} catch (final SAXException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final TransformerException e) {
			throw new RuntimeException(e);
		} catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Removes all blank lines between elements
	 */
	private void cleanWhiteSpace() {
		try {
			final XPathFactory xpathFactory = XPathFactory.newInstance();
			final XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']"); //$NON-NLS-1$
			final NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
				final Node emptyTextNode = emptyTextNodes.item(i);
				emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private void renderRcpView(final Element elmView, final RCPView view) {
		elmView.setAttribute(ATTR_VIEW_NAME, view.getName());
		elmView.setAttribute(ATTR_VIEW_ID, view.getId());
		elmView.setAttribute(ATTR_VIEW_CLASS, view.getViewClass());
		elmView.setAttribute(ATTR_VIEW_ALLOW_MULTIPLE, view.isAllowMultiple() + ""); //$NON-NLS-1$
	}

	private void renderRcpPerspective(final Element elmView, final RCPPerspective persp) {
		elmView.setAttribute(ATTR_PERSPECTIVE_CLASS, persp.getPerspectiveClass());
		elmView.setAttribute(ATTR_PERSPECTIVE_ID, persp.getId());
		elmView.setAttribute(ATTR_PERSPECTIVE_NAME, persp.getName());
	}

	public boolean registerPerspective(final BundleNode bundle, final RCPPerspective perspective) {
		final Document currentDoc = getDocument(bundle);

		if (currentDoc == null) {
			return false;
		}

		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlViewExtensions = (NodeList) xpath
					.evaluate(
							String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES), currentDoc, XPathConstants.NODESET); //$NON-NLS-1$

			Element elmPerspectiveExt = null;

			// if extensionpoint does not exist, create it
			if (nlViewExtensions.getLength() == 0) {
				elmPerspectiveExt = currentDoc.createElement(ELEM_EXTENSION);
				elmPerspectiveExt.setAttribute(ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES);

				final NodeList nlRoot = currentDoc.getElementsByTagName(ELEM_PLUGIN);
				nlRoot.item(0).appendChild(elmPerspectiveExt);
			} else {
				elmPerspectiveExt = (Element) nlViewExtensions.item(0);
			}

			if (!perspectiveAlreadyExists(currentDoc, perspective.getId())) {
				final Element elmPerspective = currentDoc.createElement(ELEM_PERSPECTIVE);
				renderRcpPerspective(elmPerspective, perspective);
				elmPerspectiveExt.appendChild(elmPerspective);
				saveDocument(currentDoc, bundleNode);
				return true;
			}
			return false;

		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean registerView(final BundleNode bundle, final RCPView view) {
		final Document currentDoc = getDocument(bundle);

		if (currentDoc == null) {
			return false;
		}

		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlViewExtensions = (NodeList) xpath
					.evaluate(
							String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS), currentDoc, XPathConstants.NODESET); //$NON-NLS-1$

			Element elmViewExt = null;

			// if extensionpoint does not exist, create it
			if (nlViewExtensions.getLength() == 0) {
				elmViewExt = currentDoc.createElement(ELEM_EXTENSION);
				elmViewExt.setAttribute(ELEM_POINT, VALUE_EXT_POINT_VIEWS);

				final NodeList nlRoot = currentDoc.getElementsByTagName(ELEM_PLUGIN);
				nlRoot.item(0).appendChild(elmViewExt);
			} else {
				elmViewExt = (Element) nlViewExtensions.item(0);
			}

			if (!viewAlreadyExists(currentDoc, view.getId())) {
				final Element elmView = currentDoc.createElement(ELEM_VIEW);
				renderRcpView(elmView, view);
				elmViewExt.appendChild(elmView);
				saveDocument(currentDoc, bundleNode);
				return true;
			}
			return false;

		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean viewAlreadyExists(final Document currentDoc, final String viewId) throws XPathExpressionException {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		// look for an element view with an ID = viewId anywhere under an
		// extensionpoint org.eclipse.ui.views
		final String xpathQuery = String.format(
				"//%s[@%s='%s']/view[@id='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS, viewId); //$NON-NLS-1$
		final NodeList nlViewExtensions = (NodeList) xpath.evaluate(xpathQuery, currentDoc, XPathConstants.NODESET);
		return nlViewExtensions.getLength() > 0;
	}

	private boolean perspectiveAlreadyExists(final Document currentDoc, final String perspectiveId)
			throws XPathExpressionException {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		// look for an element perspective with an ID = perspectiveId anywhere
		// under an extensionpoint org.eclipse.ui.perspectives
		final String xpathQuery = String
				.format("//%s[@%s='%s']/view[@id='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES, perspectiveId); //$NON-NLS-1$
		final NodeList nlViewExtensions = (NodeList) xpath.evaluate(xpathQuery, currentDoc, XPathConstants.NODESET);
		return nlViewExtensions.getLength() > 0;
	}

	private void renderModel(final Element elmExtension, final BundleNode bundleNode) {
		for (final AssemblyNode ass : bundleNode.getChildren()) {
			final Element elmAss = renderAssembly(elmExtension, ass);

			for (final AbstractAssemblyNode<?> child : ass.getChildren()) {
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

	private Element renderAssembly(final Element elmExtension, final AssemblyNode ass) {
		final Element elm = doc.createElement(ELEM_ASSEMBLY);
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

	private void renderSubApplication(final Element parent, final SubApplicationNode ass) {
		final Element elm = doc.createElement(ELEM_SUBAPP);

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

		for (final ModuleGroupNode mod : ass.getChildren()) {
			renderModuleGroup(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void renderModuleGroup(final Element parent, final ModuleGroupNode ass) {
		final Element elm = doc.createElement(ELEM_MODULE_GROUP);

		if (Util.isGiven(ass.getName())) {
			elm.setAttribute(ATTR_MODGROUP_NAME, ass.getName());
		}

		if (Util.isGiven(ass.getNodeId())) {
			elm.setAttribute(ATTR_MODGROUP_NODE_ID, ass.getNodeId());
		}

		for (final ModuleNode mod : ass.getChildren()) {
			renderModule(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void renderModule(final Element parent, final ModuleNode ass) {
		final Element elm = doc.createElement(ELEM_MODULE);

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

		for (final SubModuleNode mod : ass.getChildren()) {
			renderSubModule(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void renderSubModule(final Element parent, final SubModuleNode subMod) {
		final Element elm = doc.createElement(ELEM_SUBMODULE);

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

		elm.setAttribute(ATTR_SUBMOD_SHARED_VIEW, subMod.isSharedView() + ""); //$NON-NLS-1$

		elm.setAttribute(ATTR_SUBMOD_REQUIRES_PREPARATION, subMod.isRequiresPreparation() + ""); //$NON-NLS-1$

		elm.setAttribute(ATTR_SUBMOD_EXPANDED, subMod.isExpanded() + ""); //$NON-NLS-1$
		elm.setAttribute(ATTR_SUBMOD_VISIBLE, subMod.isVisible() + ""); //$NON-NLS-1$

		for (final SubModuleNode mod : subMod.getChildren()) {
			renderSubModule(elm, mod);
		}

		parent.appendChild(elm);
	}

	private void removeOldAssemblies() {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlExt = (NodeList) xpath
					.evaluate(
							String.format(
									"//%s[@%s='%s']", ELEM_EXTENSION, ATTR_EXTENSION_POINT, VALUE_EXT_POINT_ASSEMBLIES), doc, XPathConstants.NODESET); //$NON-NLS-1$
			for (int i = 0; i < nlExt.getLength(); i++) {
				final Node noAss = nlExt.item(i);
				noAss.getParentNode().removeChild(noAss);
			}
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}
	}
}
