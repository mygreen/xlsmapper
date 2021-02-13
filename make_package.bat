@echo off

%~d0
cd %~p0

call env.bat

call mvn -version

rem call mvn clean
rem call mvn compile -Dmaven.test.skip=true
call mvn package
rem call mvn source:jar -Dmaven.test.skip=true
rem call mvn javadoc:jar -Dmaven.test.skip=true -Dencoding=UTF-8 -Dcharset=UTF-8 -Ddecoding=UTF-8
rem mvn javadoc:javadoc -Dencoding=UTF-8 -Dcharset=UTF-8 -Ddecoding=UTF-8 -Dmaven.test.skip=true

pause
