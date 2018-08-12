package com.borunovv.jetpreter.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MainGUI {

    private Display display;
    private Shell shell;

    public void start() {
        try {
            display = new Display();
            shell = new Shell(display);
            init();
            startGUILoop(display, shell);
        } finally {
            display.dispose();
        }
    }

    private void init() {
        final Button ok = new Button(shell, SWT.PUSH);
        ok.setText("Hello world!");
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                display.dispose();
            }
        });
        shell.setDefaultButton(ok);

        shell.setLayout(new RowLayout());
        shell.pack();
        shell.open();
    }

    private void startGUILoop(Display display, Shell shell) {
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}
