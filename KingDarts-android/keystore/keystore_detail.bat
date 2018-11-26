@echo off
echo Please input your signature file's complete name (including suffix)
set /p keystore_file_name=
echo Please enter the password of your signature file
set /p keystore_file_pass=
keytool -list -v -keystore %cd%\%keystore_file_name% -storepass %keystore_file_pass%
pause