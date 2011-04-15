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
package org.eclipse.riena.ui.wizard.cs.internal;

import org.eclipse.osgi.util.NLS;

public class RienaWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.riena.ui.wizard.cs.internal.messages"; //$NON-NLS-1$
	static {
		NLS.initializeMessages(BUNDLE_NAME, RienaWizardMessages.class);
	}

	public static String ClientPage_Title;
	public static String ClientPageLayout_ClientType;
	public static String ClientPageLayout_ConsoleClient;
	public static String ClientPageLayout_GUIClient;
	public static String ClientPageLayout_NoClient;
	public static String NewApplicationWizard_title;
	public static String GeneralPage_title;

	public static String ProjectSuffixCommon;
	public static String ProjectSuffixClient;
	public static String ProjectSuffixService;

	public static String GeneralPage_ProjectName;
	public static String GeneralPage_PackageName;
	//CHECKSTYLE:OFF
	public static String GeneralPage_Validation_GeneralPage_ProjectsToBeCreated;
	public static String GeneralPage_Validation_NoBasePluginId;
	public static String GeneralPage_Validation_NoProjectName;
	public static String GeneralPage_Validation_ProjectAlreadyExists;
	public static String GenerateProjectOperation_Name;
	//CHECKSTYLE:ON
}
