@echo off
cd ../src/compilador/
del Parser.java
del ParserVal.java
"../../tools/yacc.exe" -J -Jpackage=compilador compilador.y
echo Listo
@pause