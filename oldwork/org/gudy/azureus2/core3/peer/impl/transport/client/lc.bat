@echo off
cls
echo Launching the client...
java -Djavax.net.ssl.trustStore=server.cer "localhost"
