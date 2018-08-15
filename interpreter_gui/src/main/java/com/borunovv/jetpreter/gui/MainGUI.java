package com.borunovv.jetpreter.gui;

import com.borunovv.jetpreter.core.log.Log;
import com.borunovv.jetpreter.core.util.SystemConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * GUI view.
 */
public class MainGUI implements View {
    private static final String INITIAL_SAMPLE_PROGRAM = "" +
            "print \"Hello, world!\\n\"\n" +
            "var n = 1000\n" +
            "var sequence = map({0, n}, i -> (-1)^i / (2 * i + 1))\n" +
            "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
            "print \"pi = \"\n" +
            "out pi\n" +
            "var e = reduce(map({1, 20}, i -> 1 / reduce({1,i}, 1, a x -> a * x)), 1, a x -> a + x)\n" +
            "print \"e = \"\n" +
            "out e\n";

    private final Controller controller;
    private Display display;
    private Shell shell;
    private Text sourceCodeTextArea;
    private Text outputTextArea;
    private ProgressBar progressBar;

    public MainGUI() {
        this.controller = new Controller(this);
    }

    /**
     * Start the GUI.
     */
    public void start() {
        Log.trace("Application started");
        try {
            initGui();
            initController();
            startGUILoop();
        } finally {
            tearDownController();
            tearDownGui();
        }
        Log.trace("Application stopped");
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
            if (!display.readAndDispatch()) {
                onIdle();
                display.sleep();
            }
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
                String currentText = outputTextArea.getText();
                boolean needNL = !currentText.isEmpty() && !currentText.endsWith("\n");
                outputTextArea.setText(currentText + (needNL ? SystemConstants.LINE_SEPARATOR : "") + text);
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
