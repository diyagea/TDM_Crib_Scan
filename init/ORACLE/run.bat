echo off

setlocal enabledelayedexpansion
for /f "delims=" %%a in ('type "DB.ini"^| find /i "="') do (
set %%a
)

sqlplus %username%/%password%@%db% @0_exec.sql

set /p delExit=Press the ENTER key to exit...: