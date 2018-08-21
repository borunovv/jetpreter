@echo off
if exist "C:\Program Files\Java\jdk1.8.0_101\" (
SET JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101\
)

"%JAVA_HOME%bin\java" -version
"%JAVA_HOME%\bin\java.exe" -jar target/interpreter_web.jar stop 8899