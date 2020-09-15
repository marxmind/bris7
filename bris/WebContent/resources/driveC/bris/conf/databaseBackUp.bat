cd C:\Program Files\MySQL\MySQL Server 5.7\bin 

echo Creating dir if not exist
if not exist "C:\bris\databasebackup" mkdir C:\bris\databasebackup

mysqldump.exe -e -uroot -poctober181986* -hlocalhost bris_poblacion > C:\bris\databasebackup\bris_poblacion_test.sql