@ECHO OFF
SET APP_PATH=.
SET PARAMER=-Dfile.encoding=utf-8
SET JAVA_MEM=-Xmx2048m -Xms1024m -Xmn712m
SET JAVA_HOME=%APP_PATH%\jre1.8.0_20
SET JAVA_FILE=%APP_PATH%\xrk.uiac.service-1.0-SNAPSHOT.jar
%JAVA_HOME%\bin\java %PARAMER% %JAVA_MEM% -jar %JAVA_FILE%