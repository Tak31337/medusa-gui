/*******************************************************************************
 * Copyright (c) 2010 Hippos Development Team
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * Contributors:
 *
 *    Hippos Development Team - Java Subnetting Calculator
 *
 ******************************************************************************/
package org.taksmind.medusagui.util;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * This class allows users to browse for a directory
 */
public class FileBrowser extends SelectionAdapter {
  // The Text this browser is tied to
  private Text text;

  /**
   * DirectoryBrowser constructor
   * 
   * @param text
   */
  public FileBrowser(Text text) {
    this.text = text;
  }

  /**
   * Called when the browse button is pushed
   * 
   * @param event the generated event
   */
  public void widgetSelected(SelectionEvent event) {
    FileDialog dlg = new FileDialog(Display.getCurrent()
        .getActiveShell());
    dlg.setFilterPath(text.getText());
    String file = dlg.open();
    if (file != null) {
      text.setText(file);
    }
  }
}