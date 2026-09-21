@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-27
set PATH=C:\Program Files\Java\jdk-27\bin;%PATH%
call .\mvnw.cmd clean compile
