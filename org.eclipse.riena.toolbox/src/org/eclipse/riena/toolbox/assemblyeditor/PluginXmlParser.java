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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PluginXmlParser  extends AbstractXmlProvider implements IPluginXmlParser {
	
	public List<AssemblyNode> parseDocument(BundleNode bundleNode) {
		return parseDocument(bundleNode, getDocument(bundleNode));
				
	}
	
	public Set<RCPView> getRcpViews(BundleNode bundleNode) {
		return parseViewIds(getDocument(bundleNode));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.riena.toolbox.assemblyeditor.api.IPluginXmlParser#getRcpPerspectives(org.eclipse.riena.toolbox.assemblyeditor.model.BundleNode)
	 */
	public Set<RCPPerspective> getRcpPerspectives(BundleNode bundleNode) {
		return parsePerspectiveIds(getDocument(bundleNode));
	}
	
	private Set<RCPPerspective> parsePerspectiveIds(Document doc){
		Set<RCPPerspective> viewIds = new HashSet<RCPPerspective>();
		
		if (null == doc){
			return viewIds;
		}
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nlViewExtensions = (NodeList) xpath.evaluate(String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_PERSPECTIVES), doc, XPathConstants.NODESET); //$NON-NLS-1$
			
			for (int i=0; i < nlViewExtensions.getLength(); i++){
				Element elmViewExtension = (Element) nlViewExtensions.item(i);
				NodeList nlViews = elmViewExtension.getElementsByTagName(ELEM_PERSPECTIVE);
				
				for (int j=0; j < nlViews.getLength(); j++){
					Element elmPersp = (Element) nlViews.item(j);
					
					RCPPerspective persp = parsePerspective(elmPersp);
					if (RCPPerspective.PERSPECTIVE_CLASS_NAME.equals(persp.getPerspectiveClass())){
						viewIds.add(persp);
					}
				}
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		return viewIds;
	}
	
	private Set<RCPView> parseViewIds(Document doc){
		Set<RCPView> viewIds = new HashSet<RCPView>();
		
		if (null == doc){
			return viewIds;
		}
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nlViewExtensions = (NodeList) xpath.evaluate(String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS), doc, XPathConstants.NODESET); //$NON-NLS-1$
			
			for (int i=0; i < nlViewExtensions.getLength(); i++){
				Element elmViewExtension = (Element) nlViewExtensions.item(i);
				NodeList nlViews = elmViewExtension.getElementsByTagName(ELEM_VIEW);
				
				for (int j=0; j < nlViews.getLength(); j++){
					Element elmView = (Element) nlViews.item(j);
					
					RCPView view = parseView(elmView);
					viewIds.add(view);
				}
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		return viewIds;
	}
	
	
	private RCPPerspective parsePerspective(Element elm){
		RCPPerspective view = new RCPPerspective();
		view.setId(elm.getAttribute("id"));
		view.setPerspectiveClass(elm.getAttribute("class"));
		view.setName(elm.getAttribute("name"));
		return view;
	}

	
	private RCPView parseView(Element elm){
		RCPView view = new RCPView();
		view.setId(elm.getAttribute(ATTR_VIEW_ID));
		view.setViewClass(elm.getAttribute(ATTR_VIEW_CLASS));
		view.setName(elm.getAttribute(ATTR_VIEW_NAME));
		return view;
	}

	private List<AssemblyNode> parseDocument(BundleNode parent, Document doc) {
		List<AssemblyNode> assemblyList = new ArrayList<AssemblyNode>();
		
		if (null == doc){
			return assemblyList;
			
		}

		NodeList lstAssembly = doc.getElementsByTagName(ELEM_ASSEMBLY);
		for (int i = 0; i < lstAssembly.getLength(); i++) {
			Element elmAssembly = (Element) lstAssembly.item(i);

			final AssemblyNode assemblyNode = parseAssembly(parent, elmAssembly);

			Element elemSubApp = getFirstChild(elmAssembly, ELEM_SUBAPPLICATION);
			if (null != elemSubApp) {
				SubApplicationNode subAppNode = parseSubApplication(assemblyNode, elemSubApp);
				assemblyNode.add(subAppNode);
			}

			Element elemModuleGroup = getFirstChild(elmAssembly,ELEM_MODULE_GROUP);
			if (null != elemModuleGroup) {
				ModuleGroupNode moduleGroupNode = parseModuleGroup(assemblyNode, elemModuleGroup);
				assemblyNode.add(moduleGroupNode);
			}

			Element elemModule = getFirstChild(elmAssembly, ELEM_MODULE);
			if (null != elemModule) {
				ModuleNode moduleNode = parseModule(assemblyNode, elemModule);
				assemblyNode.add(moduleNode);
			}

			Element elemSubModule = getFirstChild(elmAssembly, ELEM_SUBMODULE);
			if (null != elemSubModule) {
				SubModuleNode subModuleNode = parseSubModule(assemblyNode,
						elemSubModule);
				assemblyNode.add(subModuleNode);
			}

			assemblyList.add(assemblyNode);
		}
		return assemblyList;
	}
	
	
	private void computePreSuffixe(AssemblyNode typedNode) {
		String name = typedNode.getName();
		String typeId = typedNode.getId();
		
		if (null == typeId){
			return;
		}
		
		Pattern pattern = Pattern.compile("(.*?)\\."+name+"\\.(.*?)");
		Matcher matcher = pattern.matcher(typeId);
		if (matcher.matches()){
			String prefix = matcher.group(1);
			String suffix = matcher.group(2);
			typedNode.setPrefix(prefix+".");
			typedNode.setSuffix("."+suffix);
		}
		
	}
	
	/**
	 * Trys to compute the pre- and suffix by splitting the typeId in prefix - name - suffix parts. 
	 * 
	 * @param typedNode
	 */
	private void computePreSuffixe(AbstractTypedNode typedNode){
		String name = typedNode.getName();
		String typeId = typedNode.getTypeId();
		
		if (null == typeId){
			return;
		}
		
		Pattern pattern = Pattern.compile("(.*?)\\."+name+"\\.(.*?)");
		Matcher matcher = pattern.matcher(typeId);
		if (matcher.matches()){
			String prefix = matcher.group(1);
			String suffix = matcher.group(2);
			typedNode.setPrefix(prefix+".");
			typedNode.setSuffix("."+suffix);
		}
	}

	private AssemblyNode parseAssembly(BundleNode parent, Element elm){
		final AssemblyNode assemblyNode = new AssemblyNode(parent);
		assemblyNode.setId(elm.getAttribute(ATTR_ASSEMBLY_ID));
		assemblyNode.setAssembler(elm.getAttribute(ATTR_ASSEMBLY_ASSEMBLER));
		assemblyNode.setParentTypeId(elm.getAttribute(ATTR_ASSEMBLY_PARENT_TYPE_ID));
		assemblyNode.setName(elm.getAttribute(ATTR_ASSEMBLY_NAME));
		assemblyNode.setAutostartSequence(parseInteger(elm, ATTR_ASSEMBLY_AUTOSTARTSEQUENCE));
		assemblyNode.setRef(elm.getAttribute(ATTR_ASSEMBLY_REF));
		assemblyNode.setBundle(bundleNode);
		computePreSuffixe(assemblyNode);
		return assemblyNode;
	}
	
	

	private SubApplicationNode parseSubApplication(AbstractAssemblyNode parent, Element elm) {
		final SubApplicationNode subapp = new SubApplicationNode(parent);
		subapp.setName(elm.getAttribute(ATTR_SUBAPP_NAME));
		subapp.setTypeId(elm.getAttribute(ATTR_SUBAPP_TYPE_ID));
		subapp.setInstanceId(elm.getAttribute(ATTR_SUBAPP_INSTANCE_ID));
		subapp.setLabel(elm.getAttribute(ATTR_SUBAPP_LABEL));
		subapp.setIcon(elm.getAttribute(ATTR_SUBAPP_ICON));
		subapp.setPerspective(elm.getAttribute(ATTR_SUBAPP_VIEW));
		subapp.setBundle(bundleNode);
		computePreSuffixe(subapp);
		parent.add(subapp);

		new NodeIterator(elm, ELEM_MODULE_GROUP) {
			@Override
			public void handle(Element childElement) {
				parseModuleGroup(subapp, childElement);
			}
		}.iterate();
		return subapp;
	}

	private ModuleGroupNode parseModuleGroup(AbstractAssemblyNode parent, Element elm) {
		final ModuleGroupNode moduleGroup = new ModuleGroupNode(parent);
		moduleGroup.setName(elm.getAttribute(ATTR_MODGROUP_NAME));
		moduleGroup.setTypeId(elm.getAttribute(ATTR_MODGROUP_TYPE_ID));
		moduleGroup.setInstanceId(elm.getAttribute(ATTR_MODGROUP_INSTANCE_ID));
		moduleGroup.setBundle(bundleNode);
		computePreSuffixe(moduleGroup);
		parent.add(moduleGroup);

		new NodeIterator(elm, ELEM_MODULE) {
			@Override
			public void handle(Element childElement) {
				parseModule(moduleGroup, childElement);
			}
		}.iterate();
		return moduleGroup;
	}

	private ModuleNode parseModule(AbstractAssemblyNode parent, Element elm) {
		final ModuleNode module = new ModuleNode(parent);
		module.setName(elm.getAttribute(ATTR_MODULE_NAME));
		module.setLabel(elm.getAttribute(ATTR_MODULE_LABEL));
		module.setTypeId(elm.getAttribute(ATTR_MODULE_TYPE_ID));
		module.setInstanceId(elm.getAttribute(ATTR_MODULE_INSTANCE_ID));
		module.setIcon(elm.getAttribute(ATTR_MODULE_ICON));
		module.setUncloseable(parseBoolean(elm, ATTR_MODULE_UNCLOSABLE));
		module.setBundle(bundleNode);
		computePreSuffixe(module);
		parent.add(module);

		new NodeIterator(elm, ELEM_SUBMODULE) {
			@Override
			public void handle(Element childElement) {
				parseSubModule(module, childElement);
			}
		}.iterate();

		return module;
	}

	private SubModuleNode parseSubModule(AbstractAssemblyNode parent, Element elm) {
		final SubModuleNode sub = new SubModuleNode(parent);
		sub.setName(elm.getAttribute(ATTR_SUBMOD_NAME));
		sub.setLabel(elm.getAttribute(ATTR_SUBMOD_LABEL));
		sub.setTypeId(elm.getAttribute(ATTR_SUBMOD_TYPE_ID));
		sub.setInstanceId(elm.getAttribute(ATTR_SUBMOD_INSTANCE_ID));
		sub.setController(elm.getAttribute(ATTR_SUBMOD_CONTROLLER));
		sub.setShared(parseBoolean(elm, ATTR_SUBMOD_SHARED));
		sub.setIcon(elm.getAttribute(ATTR_SUBMOD_ICON));
		sub.setSelectable(parseBoolean(elm, ATTR_SUBMOD_SELECTABLE));
		sub.setBundle(bundleNode);
		computePreSuffixe(sub);
		
		String rcpViewId = elm.getAttribute(ATTR_SUBMOD_VIEW);
		if (null != rcpViewId){
			sub.setRcpView(new RCPView(elm.getAttribute(ATTR_SUBMOD_VIEW)));
			RCPView rcpView = parent.getBundle().findRcpView(rcpViewId);
			
			if (null != rcpView){
				sub.setRcpView(rcpView);
			}
			else{
				System.err.println("rcpView not found for subModule " + sub);
			}
		}
		
		parent.add(sub);

		new NodeIterator(elm, ELEM_SUBMODULE) {
			@Override
			public void handle(Element childElement) {
				parseSubModule(sub, childElement);
			}
		}.iterate();
		return sub;
	}

	public boolean unregisterView(SubModuleNode subModule) {
		Document doc = getDocument(subModule.getBundle());
		
		if (null == doc){
			return false;
		}

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nlViewExtensions = (NodeList) xpath.evaluate(String.format("//%s[@%s='%s']", ELEM_EXTENSION, ELEM_POINT, VALUE_EXT_POINT_VIEWS), doc, XPathConstants.NODESET); //$NON-NLS-1$
			
			for (int i=0; i < nlViewExtensions.getLength(); i++){
				Element elmViewExtension = (Element) nlViewExtensions.item(i);
				NodeList nlViews = elmViewExtension.getElementsByTagName(ELEM_VIEW);
				
				for (int j=0; j < nlViews.getLength(); j++){
					Element elmView = (Element) nlViews.item(j);
					String viewId = elmView.getAttribute(ATTR_VIEW_ID);
					
					
					if (null != viewId &&
						viewId.equals(subModule.getRcpView().getId())){
						elmViewExtension.removeChild(elmView);
						saveDocument(doc, bundleNode);
						return true;
					}
				}
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		
		return false;
	}
}