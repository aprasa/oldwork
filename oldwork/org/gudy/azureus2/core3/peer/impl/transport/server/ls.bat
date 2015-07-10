@echo off
cls
echo Launching the Server...
java -Djavax.net.ssl.trustStore=server.cer serverChat
