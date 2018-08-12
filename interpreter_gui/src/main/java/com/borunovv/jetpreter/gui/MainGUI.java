package com.borunovv.jetpreter.gui;

import com.borunovv.jetpreter.core.log.Log;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class MainGUI implements Model {
    private static final String INITIAL_SAMPLE_PROGRAM = "print \"Hello, world!\\n\"\n";

    private final Controller controller;

    private Display display;
    private Shell shell;
    private Text sourceCodeTextArea;
    private Text outputTextArea;
    private ProgressBar progressBar;

    public MainGUI() {
        this.controller = new Controller(this);
    }

    public void start() {
        Log.info("Application started");
        try {
            initGui();
            initController();
            startGUILoop();
        } finally {
            tearDownController();
            tearDownGui();
        }
        Log.info("Application stopped");
    }

    private void tearDownGui() {
        display.dispose();
    }

    private void tearDownController() {
        controller.tearDown();
    }

    private void initController() {
        controller.setUp();
        controller.onSourceCodeChanged(sourceCodeTextArea.getText());
    }

    private void initGui() {
        display = new Display();
        shell = new Shell(display);
        shell.setText("Jetpreter 1.0");

        GridLayout gridLayout = new GridLayout(2, false);
        shell.setLayout(gridLayout);

        sourceCodeTextArea = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        sourceCodeTextArea.setLayoutData(gridData);
        sourceCodeTextArea.setBackground(new Color(display, 255, 255, 240));
        sourceCodeTextArea.setText(INITIAL_SAMPLE_PROGRAM);
        sourceCodeTextArea.setSelection(sourceCodeTextArea.getText().length());

        sourceCodeTextArea.addModifyListener(e -> controller.onSourceCodeChanged(((Text) e.widget).getText()));

        outputTextArea = new Text(shell, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        outputTextArea.setLayoutData(gridData);
        outputTextArea.setBackground(new Color(display, 200, 200, 200));

        progressBar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        progressBar.setLayoutData(gridData);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        shell.setSize(800, 600);
        shell.open();
    }

    private void startGUILoop() {
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
            onIdle();
        }
    }

    private void onIdle() {
        controller.onIdle();
    }

    private void executeInGUIThread(Runnable runnable) {
        if (!display.isDisposed()) {
            display.asyncExec(runnable);
        }
    }

    @Override
    public void setProgress(final double progress) {
        executeInGUIThread(() -> {
            if (!progressBar.isDisposed()) {
                progressBar.setSelection((int) (progress * 100.0));
            }
        });
    }

    @Override
    public void appendOutput(final String text) {
        executeInGUIThread(() -> {
            if (!outputTextArea.isDisposed()) {
                outputTextArea.setText(outputTextArea.getText() + text);
                outputTextArea.setSelection(outputTextArea.getText().length());
            }
        });
    }

    @Override
    public void appendOutputFromNewLine(final String text) {
        executeInGUIThread(() -> {
            if (!outputTextArea.isDisposed()) {
                boolean needNL = !outputTextArea.getText().isEmpty() && !outputTextArea.getText().endsWith("\n");
                outputTextArea.setText(outputTextArea.getText() + (needNL ? "\n" : "") + text);
                outputTextArea.setSelection(outputTextArea.getText().length());
            }
        });
    }

    @Override
    public void clearOutput() {
        executeInGUIThread(() -> {
            if (!outputTextArea.isDisposed()) {
                outputTextArea.setText("");
            }
        });
    }
}
