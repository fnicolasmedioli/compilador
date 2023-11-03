@echo off
cd ../src/compiler/
del Parser.java
del ParserVal.java
"../../tools/yacc.exe" -J -Jpackage=compiler -Jnoconstruct compiler.y
echo Listo
@pause