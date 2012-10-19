@echo off
if "%JAVA_HOME%" == "" goto ERROR

%JAVA_HOME%\bin\java -cp JPortForwarder.jar jportforwarder.JPortForwarder ports.txt

goto END

:ERROR
echo JAVA_HOME variable not set please set

:END
