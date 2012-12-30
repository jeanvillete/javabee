@echo off
rem ---------------------------------------------------------------------------
rem Script to invoke the JavaBee Org App on Win Platform
rem 2012 12 29
rem ---------------------------------------------------------------------------
if "%JAVA_HOME%" == "" goto jvmNotDefined
:gotExecute
call "%JAVA_HOME%\bin\java" -jar javabee_engine.jar %1
goto end
:jvmNotDefined
echo JAVA_HOME not defined
echo No valid value was defined to environment variable JAVA_HOME, please, do it before!
:end