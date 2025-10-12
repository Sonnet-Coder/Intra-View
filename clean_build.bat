@echo off
echo Cleaning build directories...

REM Clean Gradle caches
gradlew.bat clean

REM Remove build directories
if exist "app\build" rmdir /s /q "app\build"
if exist "build" rmdir /s /q "build"
if exist ".gradle" rmdir /s /q ".gradle"

REM Remove KAPT generated sources
if exist "app\build\generated\ap_generated_sources" rmdir /s /q "app\build\generated\ap_generated_sources"

echo Build cleaned successfully!
echo Now run: gradlew.bat assembleDebug
pause

