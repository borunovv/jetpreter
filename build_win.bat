@echo off
if exist "C:\Program Files\Java\jdk1.8.0_101\" (
SET JAVA_HOME=C:\Program Files\Java\jdk1.8.0_101\
)

"%JAVA_HOME%bin\java" -version

SET CLEAN_MAVEN_SETTINGS=C:\Users\admin\.m2\settings_without_archiva.xml

if exist %CLEAN_MAVEN_SETTINGS% (
  echo Using clean maven settings
  mvn --settings=%CLEAN_MAVEN_SETTINGS% clean package
) else (
  echo Using default maven settings
  mvn clean package
)
