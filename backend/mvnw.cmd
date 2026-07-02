@REM Maven Wrapper for Windows — delegates to system mvn when wrapper jar is absent.
@ECHO OFF
SET MAVEN_WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties
WHERE mvn >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
    mvn %*
) ELSE (
    ECHO Maven not found. Please install Maven 3.9+ or ensure mvn is on PATH.
    EXIT /B 1
)
