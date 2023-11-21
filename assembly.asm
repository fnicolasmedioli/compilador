.586
.model flat, stdcall

option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\masm32.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\masm32.lib

.data
0_ui dw 0
1_ui dw 1
10_ui dw 10

.code
start:

jmp @@imprimir_mensaje_end
@@imprimir_mensaje:
pop eax
invoke MessageBox, NULL, eax, eax, MB_OK
ret
@@imprimir_mensaje_end:

mov eax, 0_ui
mov a_global, eax
do_until_0:
mov eax, a_global
add eax, 1_ui
mov @aux0, eax
mov eax, @aux0
mov a_global, eax
mov eax, a_global
cmp eax, 10_ui
pushfd
pop eax
not eax
push eax
popfd
je do_until_0

fin:
invoke ExitProcess, 0
end start