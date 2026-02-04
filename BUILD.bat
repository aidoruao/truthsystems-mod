@echo off
echo ========================================
echo Building TruthSystems Mod v1.0.0
echo ========================================
echo.
echo Checking Gradle...
where gradlew >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: gradlew not found. Run this from the mod directory.
    pause
    exit /b 1
)

echo.
echo Building mod JAR...
call gradlew.bat build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL
    echo ========================================
    echo.
    echo JAR location: build\libs\TruthSystems-1.0.0.jar
    echo.
    echo To install:
    echo 1. Copy build\libs\TruthSystems-1.0.0.jar
    echo 2. Paste into ..\mods\
    echo 3. Launch Minecraft
    echo.
) else (
    echo.
    echo ========================================
    echo BUILD FAILED
    echo ========================================
    echo Check error messages above.
)

pause
