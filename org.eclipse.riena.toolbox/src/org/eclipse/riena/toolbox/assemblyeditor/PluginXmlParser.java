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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.riena.toolbox.assemblyeditor.api.IPluginXmlParser;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractTypedNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.AssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleGroupNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.ModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPPerspective;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubApplicationNode;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;

public class PluginXmlParser extends AbstractXmlProvider implements IPluginXmlParser {

	/**
	 * 
	 */
	private static final String NODE_SEPARATOR = "."; //$NON-NLS-1$

	public List<AssemblyNode> parseDocument(final BundleNode bundleNode) {
		return parseDocument(bundleNode, getDocument(bundleNode));

	}

	public Set<RCPView> getRcpViews(final BundleNode bundleNode) {
		return parseViewIds(getDocument(bundleNode));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.riena.toolbox.assemblyeditor.api.IPluginXmlParser#
	 * getRcpPerspectives
	 * (org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode)
	 */
	public Set<RCPPerspective> getRcpPerspectives(final BundleNode bundleNode) {
		return parsePerspectiveIds(getDocument(bundleNode));
	}

	private Set<RCPPerspective> parsePerspectiveIds(final Document doc) {
		final Set<RCPPerspective> viewIds = new HashSet<RCPPerspective>();

		if (null == doc) {
			return viewIds;
		}

		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlViewExtensions = (NodeList) xpath
					.evaluate(
							String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES), doc, XPathConstants.NODESET); //$NON-NLS-1$

			for (int i = 0; i < nlViewExtensions.getLength(); i++) {
				final Element elmViewExtension = (Element) nlViewExtensions.item(i);
				final NodeList nlViews = elmViewExtension.getElementsByTagName(ELEM_PERSPECTIVE);

				for (int j = 0; j < nlViews.getLength(); j++) {
					final Element elmPersp = (Element) nlViews.item(j);

					final RCPPerspective persp = parsePerspective(elmPersp);
					if (RCPPerspective.PERSPECTIVE_CLASS_NAME.equals(persp.getPerspectiveClass())) {
						viewIds.add(persp);
					}
				}
			}
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		return viewIds;
	}

	private Set<RCPView> parseViewIds(final Document doc) {
		final Set<RCPView> viewIds = new HashSet<RCPView>();

		if (null == doc) {
			return viewIds;
		}

		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlViewExtensions = (NodeList) xpath
					.evaluate(
							String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS), doc, XPathConstants.NODESET); //$NON-NLS-1$

			for (int i = 0; i < nlViewExtensions.getLength(); i++) {
				final Element elmViewExtension = (Element) nlViewExtensions.item(i);
				final NodeList nlViews = elmViewExtension.getElementsByTagName(ELEM_VIEW);

				for (int j = 0; j < nlViews.getLength(); j++) {
					final Element elmView = (Element) nlViews.item(j);

					final RCPView view = parseView(elmView);
					viewIds.add(view);
				}
			}
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		return viewIds;
	}

	private RCPPerspective parsePerspective(final Element elm) {
		final RCPPerspective view = new RCPPerspective();
		view.setId(elm.getAttribute(ATTR_VIEW_ID));
		view.setPerspectiveClass(elm.getAttribute(ATTR_VIEW_CLASS));
		view.setName(elm.getAttribute(ATTR_VIEW_NAME));
		return view;
	}

	private RCPView parseView(final Element elm) {
		final RCPView view = new RCPView();
		view.setId(elm.getAttribute(ATTR_VIEW_ID));
		view.setViewClass(elm.getAttribute(ATTR_VIEW_CLASS));
		view.setName(elm.getAttribute(ATTR_VIEW_NAME));
		return view;
	}

	private List<AssemblyNode> parseDocument(final BundleNode parent, final Document doc) {
		final List<AssemblyNode> assemblyList = new ArrayList<AssemblyNode>();

		if (null == doc) {
			return assemblyList;

		}

		final NodeList lstAssembly = doc.getElementsByTagName(ELEM_ASSEMBLY);
		for (int i = 0; i < lstAssembly.getLength(); i++) {
			final Element elmAssembly = (Element) lstAssembly.item(i);
			final AssemblyNode assemblyNode = parseAssembly(parent, elmAssembly);

			new NodeIterator(elmAssembly, ELEM_SUBAPP, ELEM_MODULE_GROUP, ELEM_MODULE, ELEM_SUBMODULE) {
				@Override
				public void handle(final Element childElement) {
					if (ELEM_SUBAPP.equals(childElement.getNodeName())) {
						parseSubApplication(assemblyNode, childElement);
					} else if (ELEM_MODULE_GROUP.equals(childElement.getNodeName())) {
						parseModuleGroup(assemblyNode, childElement);
					} else if (ELEM_MODULE.equals(childElement.getNodeName())) {
						parseModule(assemblyNode, childElement);
					} else if (ELEM_SUBMODULE.equals(childElement.getNodeName())) {
						parseSubModule(assemblyNode, childElement);
					}
				}
			}.iterate();
			assemblyList.add(assemblyNode);
		}
		return assemblyList;
	}

	private void computePreSuffixe(final AssemblyNode typedNode) {
		final String name = typedNode.getName();
		final String typeId = typedNode.getId();

		if (null == typeId) {
			return;
		}

		// dont calculate preSuffixe if there is a varname in the 'name' property
		if (name.contains("${")) { //$NON-NLS-1$
			return;
		}

		final Pattern pattern = Pattern.compile("(.*?)\\." + name + "\\.(.*?)"); //$NON-NLS-1$ //$NON-NLS-2$
		final Matcher matcher = pattern.matcher(typeId);
		if (matcher.matches()) {
			final String prefix = matcher.group(1);
			final String suffix = matcher.group(2);
			typedNode.setPrefix(prefix + NODE_SEPARATOR);
			typedNode.setSuffix(NODE_SEPARATOR + suffix);
		}

	}

	/**
	 * Tries to compute the pre- and suffix by splitting the typeId in prefix -
	 * name - suffix parts.
	 * 
	 * @param typedNode
	 */
	private void computePreSuffixe(final AbstractTypedNode<?> typedNode) {
		final String name = typedNode.getName();
		final String nodeId = typedNode.getNodeId();

		if (null == nodeId) {
			return;
		}

		// dont calculate preSuffixe if there is a varname in the 'name' property
		if (name.contains("${")) { //$NON-NLS-1$
			return;
		}

		final Pattern pattern = Pattern.compile("(.*?)\\." + name + "\\.(.*?)"); //$NON-NLS-1$ //$NON-NLS-2$
		final Matcher matcher = pattern.matcher(nodeId);
		if (matcher.matches()) {
			final String prefix = matcher.group(1);
			final String suffix = matcher.group(2);
			typedNode.setPrefix(prefix + NODE_SEPARATOR);
			typedNode.setSuffix(NODE_SEPARATOR + suffix);
		}
	}

	private AssemblyNode parseAssembly(final BundleNode parent, final Element elm) {
		final AssemblyNode assemblyNode = new AssemblyNode(parent);
		assemblyNode.setId(elm.getAttribute(ATTR_ASSEMBLY_ID));
		assemblyNode.setAssembler(elm.getAttribute(ATTR_ASSEMBLY_ASSEMBLER));
		assemblyNode.setNodeTypeId(elm.getAttribute(ATTR_ASSEMBLY_PARENT_NODE_ID));
		assemblyNode.setName(elm.getAttribute(ATTR_ASSEMBLY_NAME));
		assemblyNode.setAutostartSequence(parseInteger(elm, ATTR_ASSEMBLY_START_ORDER));
		assemblyNode.setBundle(bundleNode);
		computePreSuffixe(assemblyNode);
		return assemblyNode;
	}

	private SubApplicationNode parseSubApplication(final AbstractAssemblyNode parent, final Element elm) {
		final SubApplicationNode subapp = new SubApplicationNode(parent);
		subapp.setNodeId(elm.getAttribute(ATTR_SUBAPP_NODE_ID));
		subapp.setPerspective(elm.getAttribute(ATTR_SUBAPP_PERSPECTIVE_ID));
		subapp.setName(elm.getAttribute(ATTR_SUBAPP_NAME));
		subapp.setIcon(elm.getAttribute(ATTR_SUBAPP_ICON));
		subapp.setBundle(bundleNode);
		computePreSuffixe(subapp);
		parent.add(subapp);

		new NodeIterator(elm, ELEM_MODULE_GROUP) {
			@Override
			public void handle(final Element childElement) {
				parseModuleGroup(subapp, childElement);
			}
		}.iterate();
		return subapp;
	}

	private ModuleGroupNode parseModuleGroup(final AbstractAssemblyNode parent, final Element elm) {
		final ModuleGroupNode moduleGroup = new ModuleGroupNode(parent);
		moduleGroup.setNodeId(elm.getAttribute(ATTR_MODGROUP_NODE_ID));
		moduleGroup.setName(elm.getAttribute(ATTR_MODGROUP_NAME));
		moduleGroup.setBundle(bundleNode);
		computePreSuffixe(moduleGroup);
		parent.add(moduleGroup);

		new NodeIterator(elm, ELEM_MODULE) {
			@Override
			public void handle(final Element childElement) {
				parseModule(moduleGroup, childElement);
			}
		}.iterate();
		return moduleGroup;
	}

	private ModuleNode parseModule(final AbstractAssemblyNode parent, final Element elm) {
		final ModuleNode module = new ModuleNode(parent);
		module.setNodeId(elm.getAttribute(ATTR_MODULE_NODE_ID));
		module.setName(elm.getAttribute(ATTR_MODULE_NAME));
		module.setIcon(elm.getAttribute(ATTR_MODULE_ICON));
		module.setCloseable(parseBoolean(elm, ATTR_MODULE_CLOSABLE, true));

		module.setBundle(bundleNode);
		computePreSuffixe(module);
		parent.add(module);

		new NodeIterator(elm, ELEM_SUBMODULE) {
			@Override
			public void handle(final Element childElement) {
				parseSubModule(module, childElement);
			}
		}.iterate();

		return module;
	}

	private SubModuleNode parseSubModule(final AbstractAssemblyNode parent, final Element elm) {
		final SubModuleNode sub = new SubModuleNode(parent);
		sub.setName(elm.getAttribute(ATTR_SUBMOD_NAME));
		sub.setNodeId(elm.getAttribute(ATTR_SUBMOD_NODE_ID));
		sub.setController(elm.getAttribute(ATTR_SUBMOD_CONTROLLER));
		sub.setShared(parseBoolean(elm, ATTR_SUBMOD_SHARED, false));
		sub.setIcon(elm.getAttribute(ATTR_SUBMOD_ICON));
		sub.setSelectable(parseBoolean(elm, ATTR_SUBMOD_SELECTABLE, true));
		sub.setRequiresPreparation(parseBoolean(elm, ATTR_SUBMOD_REQUIRES_PREPARATION, false));
		sub.setVisible(parseBoolean(elm, ATTR_SUBMOD_VISIBLE, true));
		sub.setExpanded(parseBoolean(elm, ATTR_SUBMOD_EXPANDED, false));

		sub.setBundle(bundleNode);
		computePreSuffixe(sub);

		final String rcpViewId = elm.getAttribute(ATTR_SUBMOD_VIEW);
		if (null != rcpViewId) {
			sub.setRcpView(new RCPView(elm.getAttribute(ATTR_SUBMOD_VIEW)));
			final RCPView rcpView = parent.getBundle().findRcpView(rcpViewId);

			if (null != rcpView) {
				sub.setRcpView(rcpView);
			} else {
				System.err.println("rcpView not found for subModule " + sub);
			}
		}

		parent.add(sub);

		new NodeIterator(elm, ELEM_SUBMODULE) {
			@Override
			public void handle(final Element childElement) {
				parseSubModule(sub, childElement);
			}
		}.iterate();
		return sub;
	}

	public boolean unregisterView(final SubModuleNode subModule) {
		final Document doc = getDocument(subModule.getBundle());

		if (null == doc) {
			return false;
		}

		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlViewExtensions = (NodeList) xpath
					.evaluate(
							String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS), doc, XPathConstants.NODESET); //$NON-NLS-1$

			for (int i = 0; i < nlViewExtensions.getLength(); i++) {
				final Element elmViewExtension = (Element) nlViewExtensions.item(i);
				final NodeList nlViews = elmViewExtension.getElementsByTagName(ELEM_VIEW);

				for (int j = 0; j < nlViews.getLength(); j++) {
					final Element elmView = (Element) nlViews.item(j);
					final String viewId = elmView.getAttribute(ATTR_VIEW_ID);

					if (null != viewId && viewId.equals(subModule.getRcpView().getId())) {
						elmViewExtension.removeChild(elmView);
						saveDocument(doc, bundleNode);
						return true;
					}
				}
			}
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	public boolean unregisterPerspective(final SubApplicationNode subApplication) {
		final Document doc = getDocument(subApplication.getBundle());

		if (null == doc) {
			return false;
		}

		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final NodeList nlPerspectivesExtensions = (NodeList) xpath
					.evaluate(
							String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES), doc, XPathConstants.NODESET); //$NON-NLS-1$

			for (int i = 0; i < nlPerspectivesExtensions.getLength(); i++) {
				final Element elmPerspectiveExtension = (Element) nlPerspectivesExtensions.item(i);
				final NodeList nlPerspectives = elmPerspectiveExtension.getElementsByTagName(ELEM_PERSPECTIVE);

				for (int j = 0; j < nlPerspectives.getLength(); j++) {
					final Element elmPerspective = (Element) nlPerspectives.item(j);
					final String perspectiveId = elmPerspective.getAttribute(ATTR_PERSPECTIVE_ID);

					if (null != perspectiveId && perspectiveId.equals(subApplication.getPerspective())) {
						elmPerspectiveExtension.removeChild(elmPerspective);
						saveDocument(doc, bundleNode);
						return true;
					}
				}
			}
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		return false;
	}
}
