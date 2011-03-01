package org.eclipse.riena.toolbox.previewer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

public class WorkspaceEquinoxContainer {
	private Object bundleContext;

	public void start() {
		String args[] = { "-consoleLog"};
		try {
			bundleContext = createClassLoader();
			
			for (IProject workspaceProject : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
				String bundleLocation = "file://" + getWorkspacePath() + workspaceProject.getName();
				System.out.println("install bundle " + bundleLocation);
				installBundle(bundleLocation);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private Object installBundle(String bundlePath){
		try {
			Method installBundle = bundleContext.getClass().getMethod("installBundle", String.class); //$NON-NLS-1$
			installBundle.invoke(bundleContext, bundlePath);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getWorkspacePath(){
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		IWorkspaceRoot root = workspace.getRoot();
//		IPath location = root.getLocation();
		return "C:/build/workspaces/toolbox_runtime3/";
	}
	
	@SuppressWarnings("restriction")
	private Object createClassLoader(){
		try {
			//ClassLoader parentClassLoder = BundleContextImpl.class.getClassLoader();
			ClassLoader parentClassLoder = null;
			URLClassLoader equinoxClassLoader = new URLClassLoader(new URL[]{new Path("C:/build/develop/eclipse-SDK-3.7M2a-win32/eclipse/plugins/org.eclipse.osgi_3.7.0.v20100910.jar").toFile().toURI().toURL()}, parentClassLoder); //$NON-NLS-1$
			Class<?> eclipseStarterClass = equinoxClassLoader.loadClass("org.eclipse.core.runtime.adaptor.EclipseStarter"); //$NON-NLS-1$
			Object eclipseStarter = eclipseStarterClass.newInstance();
			Method startupMethod = eclipseStarter.getClass().getMethod("startup", String[].class, Runnable.class); //$NON-NLS-1$
			return startupMethod.invoke(eclipseStarter, new String[]{"-consoleLog -console 12345"}, null);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
