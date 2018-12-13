@ECHO OFF
cls
cd /d %appdata%
cd ..
cd Local\Android\Sdk\platform-tools
echo # Devices...
adb devices -l
echo.
echo # Current forwards...
adb forward --list
echo.
echo # Enabling forward...
adb forward tcp:7381 tcp:7381
echo.
echo # Current forwards...
adb forward --list
pause