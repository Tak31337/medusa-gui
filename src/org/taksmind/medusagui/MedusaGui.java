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
package org.taksmind.medusagui;


import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.taksmind.medusagui.util.CommandFormatter;
import org.taksmind.medusagui.util.FileBrowser;
import org.taksmind.medusagui.util.MedusaUtil;

public class MedusaGui extends ApplicationWindow {

	private boolean editing; //Some obscure variable, like you probably haven't heard of it.
	
	public MedusaGui() {
		super(null);
	}

	public void run() {
		setBlockOnOpen(true);
		open();
		addStatusLine();
		Display.getCurrent().dispose();
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Medusa-GUI");
		shell.setSize(800, 600);
		shell.setImage(new Image(Display.getCurrent(), "/usr/local/share/medusa-gui/medusa-gui-icon.xpm"));
	}

	protected Control createContents(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		createLabelGroup(composite, "Target", "Mods");

		Group group1 = new Group(composite, SWT.NONE);
		group1.setLayout(new GridLayout(2, true));
		group1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Text target = new Text(group1, SWT.NONE);
		target.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final ComboViewer mods = new ComboViewer(group1, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		mods.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mods.setContentProvider(new ArrayContentProvider());
		try {
			mods.setInput(MedusaUtil.getModules());
		} catch (IOException e) {
			e.printStackTrace();
		}

		/**
		 * Groups in groups in groups..
		 */
		Group radioGroup = new Group(composite, SWT.NONE);
		radioGroup.setLayout(new GridLayout(2, true));
		radioGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button[] usernameSelection = createRadioGroup(radioGroup,
				"Username Text File", "Username Single Value");
		final Button[] passwordSelection = createRadioGroup(radioGroup,
				"Password Text File", "Password Single Value");

		createLabelGroup(composite, "User List", "Password List");
		final Text[] listOption = createTextGroup(composite, true);
		createLabelGroup(composite, "User Single", "Password Single");
		final Text[] singleOption = createTextGroup(composite, false);

		/**
		 * do you want a username list?
		 */
		usernameSelection[0].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				singleOption[0].setEnabled(false);
				listOption[0].setEnabled(true);
				editing = false;
			}
		});

		/**
		 * do you want a single password?
		 */
		passwordSelection[1].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				listOption[1].setEnabled(false);
				singleOption[1].setEnabled(true);
				editing = false;
			}
		});

		/**
		 * do you want a password list?
		 */
		passwordSelection[0].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				singleOption[1].setEnabled(false);
				listOption[1].setEnabled(true);
				editing = false;
			}
		});

		/**
		 * do you want a single username?
		 */
		usernameSelection[1].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				listOption[0].setEnabled(false);
				singleOption[0].setEnabled(true);
				editing = false;
			}
		});

		Group commandGroup = new Group(composite, SWT.NONE);
		commandGroup.setLayout(new GridLayout(2, false));
		commandGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label commandLabel = new Label(commandGroup, SWT.NONE);
		commandLabel.setText("Command: ");
		commandLabel.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER));

		final Text commandText = new Text(commandGroup, SWT.NONE);
		commandText.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label outputLabel = new Label(composite, SWT.NONE);
		outputLabel.setText("Output:");

		final Text output = new Text(composite, SWT.READ_ONLY | SWT.MULTI
				| SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		output.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button execute = new Button(composite, SWT.PUSH);
		execute.setText("Execute");
		execute.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		execute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				/**
				 * Oh Mr. CommandFormatter, what shall the command be?
				 */
				editing = false;
				
				String command = commandText.getText();

				Runtime run = Runtime.getRuntime();
				try {
					output.append(command + "\n");
					Process proc = run.exec(command);
					final Scanner scan = new Scanner(proc.getInputStream());
					Runnable thr = new Runnable() {

						@Override
						public void run() {
							if (scan.hasNextLine()) {
								output.append(scan.nextLine() + "\n");
							}
							parent.getDisplay().timerExec(50, this);
						}
					};
					parent.getDisplay().timerExec(50, thr);
					// output.append(out);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		editing = false;
		
		commandText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				//do nothing
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				editing = true;
			}
		});

		Runnable runnable = new Runnable() {
			public void run() {
				if (!editing) {
					commandText.setText(CommandFormatter.getCommand(
							usernameSelection, passwordSelection, listOption,
							singleOption, mods, target));
				}
				parent.getDisplay().timerExec(400, this);
			}
		};

		parent.getDisplay().timerExec(400, runnable);

		final Shell shell = composite.getShell();
		final Tray tray = composite.getDisplay().getSystemTray();
		final TrayItem item = new TrayItem(tray, SWT.None);
		Image i = new Image(composite.getDisplay(), "/usr/local/share/medusa-gui/medusa-gui-icon.xpm");
		item.setImage(i);
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// do nothing.
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				shell.setVisible(!shell.getVisible());
			}
		});

		return composite;
	}

	/**
	 * Create the labels
	 */
	protected void createLabelGroup(Composite parent, String text1, String text2) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, true));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label1 = new Label(group, SWT.NONE);
		label1.setText(text1);
		label1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label2 = new Label(group, SWT.NONE);
		label2.setText(text2);
		label2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	protected Button[] createRadioGroup(Composite parent, String text1,
			String text2) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, true));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button button1 = new Button(group, SWT.RADIO);
		button1.setText(text1);
		button1.setSelection(true);

		Button button2 = new Button(group, SWT.RADIO);
		button2.setText(text2);

		return (new Button[] { button1, button2 });
	}

	protected Text[] createTextGroup(Composite parent, boolean browse) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Text text1;
		Text text2;
		if (browse) {
			group.setLayout(new GridLayout(4, false));
			text1 = new Text(group, SWT.NONE);
			text1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Button browse1 = new Button(group, SWT.PUSH);
			browse1.setText("...");
			browse1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			browse1.addSelectionListener(new FileBrowser(text1));

			text2 = new Text(group, SWT.NONE);
			text2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Button browse2 = new Button(group, SWT.PUSH);
			browse2.setText("...");
			browse2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			browse2.addSelectionListener(new FileBrowser(text2));
		} else {
			group.setLayout(new GridLayout(2, true));
			text1 = new Text(group, SWT.NONE);
			text1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			text1.setEnabled(false);
			text2 = new Text(group, SWT.NONE);
			text2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			text2.setEnabled(false);
		}

		return (new Text[] { text1, text2 });
	}

	/**
	 * Creates the status line manager
	 */
	protected StatusLineManager createStatusLineManager() {
		return super.createStatusLineManager();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MedusaGui().run();
	}
}
