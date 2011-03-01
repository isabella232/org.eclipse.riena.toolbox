package org.eclipse.riena.toolbox.previewer.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public final class ReflectionUtil {

	
	/**
	 * @param viewPart
	 * @param parent
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeMethod(String methodName, Object viewPart,
			Composite parent) {
		for (Method method : viewPart.getClass().getMethods()) {
			if (methodName.equals(method.getName())) {
				method.setAccessible(true);
				try {
					method.invoke(viewPart, parent);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}

	public static Object newInstance(Class<?> type, Composite parent) {
		for (Constructor<?> constructor : type.getConstructors()) {
			if (Arrays.equals(new Class[] { Composite.class, Integer.TYPE },
					constructor.getParameterTypes())) {
				constructor.setAccessible(true);
				return callConstructor(constructor, parent, SWT.None);
			} else if (Arrays.equals(new Class[] { Composite.class },
					constructor.getParameterTypes())) {
				constructor.setAccessible(true);
				return callConstructor(constructor, parent);
			}
		}
		return null;
	}

	/**
	 * @param parent
	 * @param constructor
	 * @return 
	 */
	public static Object callConstructor(Constructor<?> constructor, Object... params) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(params);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param viewPart
	 */
	public static Object loadClass(ViewPartInfo viewPart) {
		try {
			return viewPart.getType().newInstance();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private ReflectionUtil() {
		// TODO Auto-generated constructor stub
	}

}
