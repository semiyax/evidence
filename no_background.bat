@echo off
setlocal

if not exist evidence.jar (
  echo [ERROR] evidence.jar not found in current folder.
  echo Place this .bat next to evidence.jar from the release archive.
  pause
  exit /b 1
)

java -jar evidence.jar %* --nobackground
if errorlevel 1 (
  echo.
  echo [ERROR] Evidence exited with code %errorlevel%.
)

pause
