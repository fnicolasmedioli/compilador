@echo off
cd ../src/compilador/
"../../tools/yacc.exe" -J -Jpackage=compilador compilador.y
echo Listo
@pause