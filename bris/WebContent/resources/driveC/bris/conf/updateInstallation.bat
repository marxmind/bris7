
setlocal
set LogPath=C:\bris\log\
set LogFileExt=.log
set LogFileName=InstallationLog%LogFileExt%
::set MyLogFile=%MyLogFile%
set MyLogFile=%LogPath%%MyLogFile%%LogFileName%
::Note that the quotes are REQUIRED around %MyLogFIle% in case it contains a space
IF NOT Exist "%LogPath%" mkdir %LogPath%
If NOT Exist "%MyLogFile%" goto:noseparator
Echo.>>"%MyLogFile%"
Echo.==============MARXMIND==============>>"%MyLogFile%"
:noseparator
set dateNow=%date:~4,2%-%date:~7,2%-%date:~10,4%
set timeNow=%time:~0,2%_%time:~3,2%_%time:~6,2%
echo Installation Date : %datenow% Time: %timeNow% >>"%MyLogFile%"
cd C:\Program Files\MySQL\MySQL Server 5.7\bin
mysql -uroot -poctober181986* -e "use bris;select id,currentversion,oldversion from copyright;" >>"%MyLogFile%"

echo exit from MYSQL >>"%MyLogFile%"

::cd C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\bin
:: Tomcat9.exe stop