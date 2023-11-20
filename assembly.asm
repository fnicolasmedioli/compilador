.386
.model flat, stdcall

option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\masm32.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\masm32.lib

.data
4_ui dw 4
3_ui dw 3

.code
mov eax, 4_ui
mul 3_ui
mov @aux0, eax
mov eax, @aux0
mov var1_global, eax

