/*******************************************************************************
 * Copyright (c) 2010 Hippos Development Team
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * Contributors:
 *
 *    Hippos Development Team - Medusa Graphical User Interface
 *
 ******************************************************************************/
package org.taksmind.medusagui.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedusaUtil {
	public static Object[] getModules() throws IOException {
		Runtime r = Runtime.getRuntime();
		Process proc = r.exec("medusa -d");
		
		Scanner s = new Scanner(proc.getInputStream());
		Pattern p = Pattern.compile("[+] (.*?).mod");
		Matcher m;
		ArrayList<String> mods = new ArrayList<String>();
		
		while(s.hasNextLine()) {
			m = p.matcher(s.nextLine());
			if(m.find()) {
				mods.add(m.group(1));
			}
		}
		
		return mods.toArray();
	}
}
