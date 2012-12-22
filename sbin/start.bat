set CURRENT_DIR=%cd%

set TWITTER_HOME=d:\haijia

@echo off
 setLocal EnableDelayedExpansion

 set CLASSPATH=
 for /R  ..\lib  %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 
set CLASSPATH=!CLASSPATH!

set CLASSPATH=%TWITTER_HOME%\config;%CLASSPATH%
 
echo classpath=%CLASSPATH%

java -classpath %CLASSPATH% com.sohu.wap.HaijiaNetMain