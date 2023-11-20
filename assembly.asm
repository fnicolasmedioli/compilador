.386
.model flat, stdcall

option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\masm32.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\masm32.lib

.data
6_ui dw 6
2_ui dw 2
4_ui dw 4

.code
mov eax, 4_ui
mov var1_global, eax
mov eax, 6_ui
mov var2_global, eax
mov eax, var1_global
add eax, var2_global
mov @aux0, eax
mov eax, @aux0
mov var1_global, eax
mov eax, 2_ui
add eax, var1_global
mov @aux1, eax
mov eax, @aux1
mov var3_global, eax

