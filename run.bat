@echo off
echo ========================================================
echo Launching Cinematica Desktop App...
echo ========================================================

java -cp "bin;lib/*" com.movierating.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Make sure you have compiled the code first using compile.bat
    pause
)
