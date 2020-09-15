
::MariaDB [mysql]> GRANT ALL ON bris_poblacion.* TO root@'192.168.43.155' IDENTIFIED BY 'october181986*';
::remote machine mysql -uroot -poctober181986* -h 192.168.43.155 bris_poblacion

set dt=%date:~7,2%-%date:~4,2%-%date:~10,4%_%time:~0,2%_%time:~3,2%_%time:~6,2%
set det=%date:~7,2%-%date:~4,2%-%date:~10,4%
echo Backup database.....
echo %dt%
C:
cd C:\Program Files\MariaDB 10.4\bin 

echo Creating dir if not exist
if not exist "C:\bris\databasebackup" mkdir C:\bris\databasebackup

setlocal
set LogPath=C:\bris\log\
set LogFileExt=.log
set LogFileName=Daily Backup%LogFileExt%
::use set MyLogFile=%date:~4% instead to remove the day of the week
::[COLOR="DarkRed"]set MyLogFile=%date%
::set MyLogFile=%MyLogFile:/=-%[/COLOR]
set MyLogFile=%MyLogFile%
set MyLogFile=%LogPath%%MyLogFile%%LogFileName%
::Note that the quotes are REQUIRED around %MyLogFIle% in case it contains a space
IF NOT Exist "%LogPath%" mkdir %LogPath%
If NOT Exist "%MyLogFile%" goto:noseparator
Echo.>>"%MyLogFile%"
Echo.========================================================================ITALIAWorks========================================>>"%MyLogFile%"
:noseparator
::echo.%Date% >>"%MyLogFile%"
::echo.%Time% >>"%MyLogFile%"
echo.%Date% %Time% Preparing for backup... >>"%MyLogFile%"
echo.%Date% %Time% starting backup... >>"%MyLogFile%"


mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion > C:\bris\databasebackup\bris_poblacion_%det%.sql
mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_talisay > C:\bris\databasebackup\bris_talisay_%det%.sql
mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_lamdalag > C:\bris\databasebackup\bris_lamdalag_%det%.sql

::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion activationcode > C:\bris\databasebackup\bris_activationcode_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion department > C:\bris\databasebackup\bris_department_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion empposition > C:\bris\databasebackup\bris_empposition_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion license > C:\bris\databasebackup\bris_license_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion province > C:\bris\databasebackup\bris_province_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion purok > C:\bris\databasebackup\bris_purok_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion barangay > C:\bris\databasebackup\bris_barangay_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion municipality > C:\bris\databasebackup\bris_municipality_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion regional > C:\bris\databasebackup\bris_regional_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion useraccesslevel > C:\bris\databasebackup\bris_useraccesslevel_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion userdtls > C:\bris\databasebackup\bris_userdtls_%det%.sql
::mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion login > C:\bris\databasebackup\bris_login_%det%.sql

echo.%Date% %Time% Preparing to save the backup >>"%MyLogFile%"
echo.%Date% %Time% Location: C:\bris\databasebackup >>"%MyLogFile%"
echo.%Date% %Time% Backup has been successfully proccessed with the file name of 'bris_%det%.sql' >>"%MyLogFile%"
echo.%Date% %Time% Location: C:\bris\databasebackup\bris_%det%.sql >>"%MyLogFile%"


echo.%Date% %Time% Ended... >>"%MyLogFile%"