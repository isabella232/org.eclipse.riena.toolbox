package org.eclipse.riena.toolbox.previewer.handler;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.toolbox.previewer.ClassFinder;
import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;

/**
 * Checks if a given {@link ICompilationUnit} is a instance of {@link Composite} or {@link ViewPart}. 
 *
 */
public class CompilationUnitPropertyTester extends PropertyTester {
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		ICompilationUnit comp = (ICompilationUnit) receiver;
		ClassFinder classFinder = new ClassFinder();
		ViewPartInfo partInfo = classFinder.loadClass(comp);
		if (null == partInfo){
			return false;
		}
		return classFinder.isValidType(partInfo.getType());
	}

}
