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

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class CommandFormatter {
	public static String getCommand(Button[] usernameSelection,
			Button[] passwordSelection, Text[] listOption, Text[] singleOption,
			ComboViewer mods, Text target) {
		String command = "";
		if (usernameSelection[0].getSelection()
				&& passwordSelection[0].getSelection()) {
			command = "medusa -h " + target.getText() + " -U "
					+ listOption[0].getText() + " -P "
					+ listOption[1].getText() + " -M "
					+ mods.getElementAt(mods.getCombo().getSelectionIndex());
		} else if (usernameSelection[0].getSelection()
				&& passwordSelection[1].getSelection()) {
			command = "medusa -h " + target.getText() + " -U "
					+ listOption[0].getText() + " -p "
					+ singleOption[1].getText() + " -M "
					+ mods.getElementAt(mods.getCombo().getSelectionIndex());
		} else if (usernameSelection[1].getSelection()
				&& passwordSelection[1].getSelection()) {
			command = "medusa -h " + target.getText() + " -u "
					+ singleOption[0].getText() + " -p "
					+ singleOption[1].getText() + " -M "
					+ mods.getElementAt(mods.getCombo().getSelectionIndex());
		} else if (usernameSelection[1].getSelection()
				&& passwordSelection[0].getSelection()) {
			command = "medusa -h " + target.getText() + " -u "
					+ singleOption[0].getText() + " -P "
					+ listOption[1].getText() + " -M "
					+ mods.getElementAt(mods.getCombo().getSelectionIndex());
		}

		return command;
	}
}
