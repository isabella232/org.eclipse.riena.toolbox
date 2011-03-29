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

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

@SuppressWarnings("restriction")
public class RidgetGenerator {
	protected static final String NATURE_JAVA = "org.eclipse.jdt.core.javanature"; //$NON-NLS-1$
	protected static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$
	protected static final String METHOD_BASIC_CREATE_PART_CONTROL = "basicCreatePartControl"; //$NON-NLS-1$
	protected static final String METHOD_GET_RIDGET = "getRidget"; //$NON-NLS-1$
	protected static final String METHOD_CONFIGURE_RIDGETS = "configureRidgets"; //$NON-NLS-1$
	protected final IProject project;

	public RidgetGenerator(final IProject project) {
		this.project = project;
	}

	/**
	 * Retrieves a {@link ICompilationUnit} with the given className
	 * 
	 * @param fullyQualifiedClassName
	 * @return if found the CompilationUnit, otherwise false
	 */
	public ICompilationUnit findICompilationUnit(final String fullyQualifiedClassName) {
		try {
			if (project.isNatureEnabled(NATURE_JAVA)) {
				final IJavaProject javaProject = JavaCore.create(project);

				final Pattern p = Pattern.compile("(.*)\\.(.*?)"); //$NON-NLS-1$
				final Matcher m = p.matcher(fullyQualifiedClassName);
				if (m.matches()) {
					final String packageName = m.group(1);
					final String className = m.group(2);

					final IPackageFragment viewPackage = findPackage(javaProject, packageName);
					for (final ICompilationUnit unit : viewPackage.getCompilationUnits()) {
						if (unit.getElementName().equals(className + EXTENSION_JAVA)) {
							return unit;
						}
					}
				}
			}
		} catch (final JavaModelException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (final CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return null;
	}

	/**
	 * Parses the basisCreatePartControl-Method for calls to
	 * addUIControl(control, ridgetId) and UIControlFactory.*. This method finds
	 * all nested calls and calls in inner classes, but does not find any calls
	 * in other Toplevel-Classes.
	 * 
	 * @param fullyQualifiedClassName
	 * @return all found SwtControls, or an empty list
	 */
	@SuppressWarnings("unchecked")
	public List<SwtControl> findSwtControls(final String fullyQualifiedClassName) {
		final CompilationUnit astNode = findCompilationUnit(fullyQualifiedClassName);
		if (null == astNode) {
			return Collections.EMPTY_LIST;
		}

		final MethodDeclaration methodBasicCreatePartControl = findMethod(astNode, METHOD_BASIC_CREATE_PART_CONTROL);
		if (null == methodBasicCreatePartControl) {
			return Collections.EMPTY_LIST;
		}

		final CollectMethodDeclerationsVisitor collector = new CollectMethodDeclerationsVisitor();
		astNode.accept(collector);

		final UIControlVisitor visitor = new UIControlVisitor(collector.getMethods());
		methodBasicCreatePartControl.accept(visitor);
		final List<SwtControl> controls = visitor.getControls();
		return controls;
	}

	/**
	 * Generates a configureRidget-Method for the given List of
	 * {@link SwtControl}. If the configureRidgets does not yes exist, it will
	 * be created.
	 * <p>
	 * It then generates the calls to getRidget("ridgetId") for all
	 * {@link SwtControl} that don't already exist.
	 * 
	 * @param fullyQualifiedControllerClassName
	 * @param controls
	 * @return true if the given class was found, otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean generateConfigureRidgets(final String fullyQualifiedControllerClassName,
			final List<SwtControl> controls) {
		final ICompilationUnit unit = findICompilationUnit(fullyQualifiedControllerClassName);
		if (null == unit) {
			System.err.println("controller not found " + fullyQualifiedControllerClassName);
			return false;
		}

		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		final IJavaProject javaProject = JavaCore.create(project);
		parser.setProject(javaProject);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		final CompilationUnit astNode = (CompilationUnit) parser.createAST(null);
		astNode.recordModifications();

		final AST ast = astNode.getAST();

		MethodDeclaration methodDeclaration = findMethod(astNode, METHOD_CONFIGURE_RIDGETS);
		if (null == methodDeclaration) {
			methodDeclaration = ast.newMethodDeclaration();
			final Block methodBlock = ast.newBlock();
			methodDeclaration.setConstructor(false);
			methodDeclaration.modifiers().addAll(ast.newModifiers(Modifier.PUBLIC));
			methodDeclaration.setName(ast.newSimpleName(METHOD_CONFIGURE_RIDGETS));
			methodDeclaration.setBody(methodBlock);
			final TypeDeclaration typeDecl = (TypeDeclaration) astNode.types().get(0);
			typeDecl.bodyDeclarations().add(methodDeclaration);
		}

		addImportStatements(ast, astNode, controls);

		generateGetRidgetCalls(ast, methodDeclaration, controls);

		return saveDocument(astNode);
	}

	private String cleanVariableName(final String ridgetId) {
		String cleanVariableName = ridgetId.replaceAll("[^a-zA-Z0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		// cut off any digits at beginning of string
		cleanVariableName = cleanVariableName.replaceFirst("^\\d+(.*?)$", "$1"); //$NON-NLS-1$ //$NON-NLS-2$
		cleanVariableName = Character.toLowerCase(cleanVariableName.charAt(0)) + cleanVariableName.substring(1);

		// FIXME Generator can break, if ridgetId is Java keyword like static or privat
		return cleanVariableName;
	}

	/**
	 * Generates for every {@link SwtControl} in the given a List a call like:
	 * <code>
	 * ILabelRidget myLabelRidget = (ILabelRidget) getRidget("myLabelRidget");
	 * </code>
	 * 
	 * @param ast
	 * @param parentMethod
	 * @param controls
	 */
	@SuppressWarnings("unchecked")
	private void generateGetRidgetCalls(final AST ast, final MethodDeclaration parentMethod,
			final List<SwtControl> controls) {

		for (final SwtControl control : controls) {
			// if a call to getRidget with this ridgetId already exist, skip it
			if (getRidgetCallExists(parentMethod, control.getRidgetId())) {
				continue;
			}

			final VariableDeclarationFragment frag = ast.newVariableDeclarationFragment();
			final String cleanVariableName = cleanVariableName(control.getRidgetId());
			frag.setName(ast.newSimpleName(cleanVariableName));
			final VariableDeclarationStatement vds = ast.newVariableDeclarationStatement(frag);
			vds.setType(ast.newSimpleType(ast.newSimpleName(control.getRidgetClassName())));
			parentMethod.getBody().statements().add(vds);

			final MethodInvocation mi = ast.newMethodInvocation();
			mi.setName(ast.newSimpleName(METHOD_GET_RIDGET));

			final TypeLiteral argClass = ast.newTypeLiteral();
			argClass.setType(ast.newSimpleType(ast.newSimpleName(control.getRidgetClassName())));
			mi.arguments().add(argClass);

			final StringLiteral argRidgetId = ast.newStringLiteral();
			argRidgetId.setLiteralValue(control.getRidgetId());
			mi.arguments().add(argRidgetId);

			frag.setInitializer(mi);
		}
	}

	/**
	 * Adds an Importstatement to the given {@link CompilationUnit} if
	 * necessary, for every Ridget-Class in the controls-List.
	 * 
	 * @param ast
	 * @param unit
	 * @param controls
	 */
	@SuppressWarnings("unchecked")
	private void addImportStatements(final AST ast, final CompilationUnit unit, final List<SwtControl> controls) {
		for (final SwtControl swtControl : controls) {
			if (!hasImportStatement(unit, swtControl.getFullyQualifiedRidgetClassName())) {
				final ImportDeclaration newImport = ast.newImportDeclaration();
				newImport.setName(ast.newName(swtControl.getFullyQualifiedRidgetClassName()));
				unit.imports().add(newImport);
			}
		}
	}

	private boolean hasImportStatement(final CompilationUnit unit, final String ridgetClassName) {
		Assert.isNotNull(ridgetClassName);
		for (final Object obj : unit.imports()) {
			if (obj instanceof ImportDeclaration) {
				final ImportDeclaration imp = (ImportDeclaration) obj;
				if (ridgetClassName.equals(imp.getName().getFullyQualifiedName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean getRidgetCallExists(final MethodDeclaration method, final String ridgetId) {
		final RidgetCallVisitor visitor = new RidgetCallVisitor(ridgetId);
		method.accept(visitor);
		return visitor.isCallExists();
	}

	protected MethodDeclaration findMethod(final CompilationUnit astNode, final String name) {
		for (final Object typeDeclObj : astNode.types()) {
			final TypeDeclaration typeDecl = (TypeDeclaration) typeDeclObj;
			for (final Object obj : typeDecl.bodyDeclarations()) {
				if (obj instanceof MethodDeclaration) {
					final MethodDeclaration method = (MethodDeclaration) obj;

					if (name.equals(method.getName().getFullyQualifiedName())) {
						return method;
					}
				}
			}
		}
		return null;
	}

	private IPackageFragment findPackage(final IJavaProject javaProject, final String packageName) {
		try {
			for (final IPackageFragment mypackage : javaProject.getPackageFragments()) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					if (packageName.equals(mypackage.getElementName())) {
						return mypackage;
					}
				}
			}
		} catch (final JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected CompilationUnit findCompilationUnit(final String fullyQualifiedClassName) {
		try {
			if (project.isNatureEnabled(NATURE_JAVA)) {
				final IJavaProject javaProject = JavaCore.create(project);

				final Pattern p = Pattern.compile("(.*)\\.(.*?)"); //$NON-NLS-1$
				final Matcher m = p.matcher(fullyQualifiedClassName);
				if (m.matches()) {
					final String packageName = m.group(1);
					final String className = m.group(2);

					final IPackageFragment viewPackage = findPackage(javaProject, packageName);
					for (final ICompilationUnit unit : viewPackage.getCompilationUnits()) {
						if (unit.getElementName().equals(className + EXTENSION_JAVA)) {
							final ASTParser parser = ASTParser.newParser(AST.JLS3);
							parser.setProject(javaProject);
							parser.setSource(unit);
							parser.setResolveBindings(true);
							final CompilationUnit astNode = (CompilationUnit) parser.createAST(null);
							return astNode;
						}
					}
				}
			}
		} catch (final JavaModelException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (final CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return null;
	}

	protected boolean saveDocument(final CompilationUnit astNode) {
		try {
			final ICompilationUnit unit = (ICompilationUnit) astNode.getJavaElement();

			if (unit == null) {
				System.err.println("iCompilationUnit is null " + astNode);
				return false;
			}

			if (!unit.isOpen()) {
				unit.open(null);
			}
			final IEditorPart part = EditorUtility.openInEditor(unit, false);
			JavaUI.revealInEditor(part, (IJavaElement) unit);

			final Document doc = new Document(unit.getSource());
			final TextEdit edits = astNode.rewrite(doc, unit.getJavaProject().getOptions(true));
			unit.applyTextEdit(edits, null);
			part.doSave(null);
			return true;
		} catch (final MalformedTreeException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (final JavaModelException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (final PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
