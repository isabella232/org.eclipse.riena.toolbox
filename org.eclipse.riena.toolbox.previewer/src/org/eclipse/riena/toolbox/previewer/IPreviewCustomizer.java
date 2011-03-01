package org.eclipse.riena.toolbox.previewer;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface to customize the Classload-lifecycle. 
 *
 */
public interface IPreviewCustomizer {
	void beforeClassLoad(ClassLoader classLoader);
	void afterCreation(Composite parent);
	Class<?> getParentClass();
}
