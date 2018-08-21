@echo off
call build_win.bat

start "" "%JAVA_HOME%\bin\java.exe" -jar target/interpreter_web.jar 8899