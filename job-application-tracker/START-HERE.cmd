@echo off
setlocal
cd /d "%~dp0"
echo Launchpad - Job Application Tracker
echo When startup completes, open http://localhost:8080 in your browser.
echo Keep this window open. Press Ctrl+C to stop the app.
echo.
set "APP_JAVA=java"
if exist "%ProgramFiles%\Java\jdk-21.0.11\bin\java.exe" set "APP_JAVA=%ProgramFiles%\Java\jdk-21.0.11\bin\java.exe"
if exist "job-application-tracker-2.0.0.jar" (
  "%APP_JAVA%" -jar "job-application-tracker-2.0.0.jar"
) else (
  echo The packaged JAR is missing. Run mvn clean package, then use:
  echo java -jar target/job-application-tracker-2.0.0.jar
)
echo.
pause
