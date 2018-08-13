@echo off
call build_win.bat
"%JAVA_HOME%bin\java" -jar interpreter_gui/target/interpreter_gui-1.0.jar