@echo off
setlocal
if not exist out mkdir out
for /r src\main\java %%f in (*.java) do echo %%f>>sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
  echo Error de compilacion.
  del sources.txt
  exit /b 1
)
del sources.txt
java -cp out co.edu.unilibre.paqueteria.app.Main
endlocal
