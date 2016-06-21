@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  tuml-interpreter startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and TUML_INTERPRETER_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\tuml-interpreter-0.0.1.jar;%APP_HOME%\lib\org.eclipse.emf.common-2.11.0-v20150123-0347.jar;%APP_HOME%\lib\org.eclipse.emf.ecore-2.11.0-v20150123-0347.jar;%APP_HOME%\lib\org.eclipse.emf.ecore.xmi-2.11.0-v20150123-0347.jar;%APP_HOME%\lib\org.eclipse.emf.mapping.ecore2xml-2.8.0-v20150123-0452.jar;%APP_HOME%\lib\org.eclipse.uml2.common-2.0.1.v20150202-0947.jar;%APP_HOME%\lib\org.eclipse.uml2.types-2.0.0.v20150202-0947.jar;%APP_HOME%\lib\org.eclipse.uml2.uml-5.0.2.v20150202-0947.jar;%APP_HOME%\lib\org.eclipse.uml2.uml.profile.standard-1.0.0.v20150202-0947.jar;%APP_HOME%\lib\org.eclipse.uml2.uml.resources-5.0.2.v20150202-0947.jar;%APP_HOME%\lib\org.eclipse.xtend.lib-2.9.0.jar;%APP_HOME%\lib\java-petitparser-2.0.0.jar;%APP_HOME%\lib\org.eclipse.xtext.xbase.lib-2.9.0.jar;%APP_HOME%\lib\org.eclipse.xtend.lib.macro-2.9.0.jar;%APP_HOME%\lib\petitparser-core-2.0.0.jar;%APP_HOME%\lib\petitparser-json-2.0.0.jar;%APP_HOME%\lib\petitparser-smalltalk-2.0.0.jar;%APP_HOME%\lib\petitparser-xml-2.0.0.jar;%APP_HOME%\lib\guava-14.0.1.jar

@rem Execute tuml-interpreter
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TUML_INTERPRETER_OPTS%  -classpath "%CLASSPATH%" tuml.interpreter.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable TUML_INTERPRETER_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%TUML_INTERPRETER_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
