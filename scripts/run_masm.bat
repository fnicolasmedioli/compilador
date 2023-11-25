@echo off

cd ../build
\masm32\bin\ml /c /Zd /coff program.asm

if %ERRORLEVEL% neq 0 (
    echo:
    echo No se pudo ensamblar
    echo:
    exit /b %ERRORLEVEL%
)

\masm32\bin\Link /SUBSYSTEM:CONSOLE program.obj

if %ERRORLEVEL% neq 0 (
    echo:
    echo: No se pudo linkear
    echo:
    exit /b %ERRORLEVEL%
)

echo Salida del programa:
echo:
program.exe