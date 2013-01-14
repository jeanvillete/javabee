@echo off
rem ---------------------------------------------------------------------------
rem Script to invoke the JavaBee Org App on Win Platform
rem 2012 12 29
rem ---------------------------------------------------------------------------
if "%JAVA_HOME%" == "" goto jvmNotDefined
:gotExecute
set CURRENT_DIR=%~dp0
set PARAMETERS=%* -current_directory " %CURRENT_DIR% "
call "%JAVA_HOME%\bin\java" -jar "%CURRENT_DIR%javabee_engine.jar" %PARAMETERS%
goto end
:jvmNotDefined
echo JAVA_HOME not defined
echo No valid value was defined to JAVA_HOME environment variable, please, do it before!
:end