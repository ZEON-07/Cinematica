@echo off
echo ========================================================
echo Compiling Cinematica (Movie Rating and Review System)...
echo ========================================================

if not exist bin mkdir bin

javac -cp "lib/*" -d bin src/com/movierating/exceptions/*.java src/com/movierating/models/*.java src/com/movierating/database/*.java src/com/movierating/ui/*.java src/com/movierating/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation SUCCESSFUL! Compiled .class files are in the 'bin' folder.
) else (
    echo Compilation FAILED! Please check JDK installation and classpath.
)

pause
