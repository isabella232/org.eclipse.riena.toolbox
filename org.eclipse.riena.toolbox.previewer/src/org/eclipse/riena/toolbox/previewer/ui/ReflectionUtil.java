package org.eclipse.riena.toolbox.previewer.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.toolbox.previewer.model.ViewPartInfo;

public final class ReflectionUtil {

	/**
	 * @param viewPart
	 * @param parent
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static boolean invokeMethod(final String methodName, final Object viewPart, final Composite parent) {
		for (final Method method : viewPart.getClass().getMethods()) {
			if (methodName.equals(method.getName())) {
				method.setAccessible(true);
				try {
					method.invoke(viewPart, parent);
				} catch (final IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (final IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (final InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				return true;
			}
		}
		return false;
	}

	public static Object newInstance(final Class<?> type, final Composite parent) {
		for (final Constructor<?> constructor : type.getConstructors()) {
			if (Arrays.equals(new Class[] { Composite.class, Integer.TYPE }, constructor.getParameterTypes())) {
				constructor.setAccessible(true);
				return callConstructor(constructor, parent, SWT.None);
			} else if (Arrays.equals(new Class[] { Composite.class }, constructor.getParameterTypes())) {
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
	public static Object callConstructor(final Constructor<?> constructor, final Object... params) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(params);
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param viewPart
	 */
	public static Object loadClass(final ViewPartInfo viewPart) {
		try {
			return viewPart.getType().newInstance();
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private ReflectionUtil() {
		// private constructor
	}

}
