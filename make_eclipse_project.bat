@echo off

%~d0
cd %~p0

if NOT "%JAVA_HOME_7%" == "" (
    set JAVA_HOME="%JAVA_HOME_7%"
)


REM mvn clean
mvn eclipse:eclipse -DdownloadSources=true -Declipse.useProjectReferences=false

PAUSE
