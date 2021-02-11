@echo off

%~d0
cd %~p0

if NOT "%JAVA_HOME_8%" == "" (
    set JAVA_HOME="%JAVA_HOME_8%"
)

call mvn clean
mkdir target
call mvn site -Dgpg.skip=true > target/site.log 2>&1

REM github-pages‚Ì‘Î‰
echo "" > .\target\site\.nojekyll

start target/site.log

