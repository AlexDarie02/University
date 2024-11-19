.section .data

file_descr:
    .int 0

num_val:
    .int 0

num_arg:
    .int 0

error_par: 
.ascii "Troppi parametri inseriti: inserirne solo due"

error_par_len: 
.long .-error_par

error_no_par:
.ascii "Non è stato inserito alcun parametro"

error_no_par_len:
.long .-error_no_par

str_errfile: 
.ascii "Errore in apertura del file"

str_errfile_len:
.long .-str_errfile

display_menu: 
.ascii "Scegliere l'algoritmo di pianificazione:\n  1 - Earliest Deadline First(EDF)\n  2 - Highest Priority First(HPF)\n  3 - Exit\n"

display_menu_len:
.long .-display_menu

one:
.ascii "1"

two: 
.ascii "2"

three:
.ascii "3"

display_EDF:
.ascii "\nPianificazione EDF:\n"

display_EDF_len:
.long .-display_EDF

display_HPF:
.ascii "\nPianificazione HPF:\n"

display_HPF_len:
.long .-display_HPF


.section .bss
scelta: .string ""


.section .text
.global _start

_start: 
    popl %esi
    movl %esi, num_arg # prendo il numero di parametri passati

    cmpl $2, %esi # controllo che i parametri passati non siano più di 2 (pianificatore Both.txt)
    jg _errpar  # se sono più di 2 parametri errore

    popl %esi # pop del programma "pianificatore"
    popl %esi # recupero l'indirizzo relativo alla stringa
    testl %esi, %esi # controllo che ecx non sia 0
    jz _errnopar # non è stato passato alcun parametro 

    # syscall open
    movl $5, %eax           # syscall open 
    movl %esi, %ebx         # carico l'indirizzo del nome del file in %ebx
    movl $0, %ecx           # read-only
    int $0x80       

    # controllo se l'apertura è andata a buon fine
    cmpl $0, %eax               # confronto il valore di ritorno della syscall con 0
    jl _errore_apertura         # se eax è minore di 0, salto a dare errore

    # altrimenti, memorizzo il file descriptor se tutto ok
    movl %eax, file_descr

    movl file_descr, %ebx

    call read

    cmpl $90, %ebx # 90 flag di errore "passato" dalla funzione readfile
    je _end   

    movl %ecx, num_val # in readfile spostiamo il numero di valori trovato in ecx
    # e lo salviamo cosi nella variabile di pianificatore


_menu:
    movl $4, %eax           # Stampo richiesta di inserimento algoritmo
    movl $1, %ebx
    leal display_menu, %ecx
    movl display_menu_len, %edx
    int $0x80

    xorl %ecx, %ecx # azzeriamo ecx per assicurarci che non contenga valori residui
    # dall'istruzione precedente

    # syscall read
    movl $3, %eax
    movl $0, %ebx            # input da tastiera
    leal scelta, %ecx       # salvo ciò che leggo in scelta
    movl $45, %edx          # lunghezza stringa
    int $0x80

    # salviamo i caratteri ASCII nella parte bassa dei registri
    # perchè ogni carattere occupa 1B quindi sarebbe poco efficente usare registri da 32bit
    movb one, %al
    movb two, %bl
    movb three, %cl

    # Controllo quale valore ha inserito l'utente
    cmp %al, scelta
    je _EDF

    cmp %bl, scelta
    je _HPF

    cmp %cl, scelta
    je _saveval # salto li perchè prima di chiudere devo svuotare lo stack 

    # se arrivo qui = l'input non è nessuno dei 3 numeri validi quindi richiedo
    jmp _menu


_EDF: 
    movb num_val, %al
    call EDF
    # write 
    movl $4, %eax
    movl $1, %ebx
    leal display_EDF, %ecx
    movl display_EDF_len, %edx
    int $0x80
    movb num_val, %al 
    call print
    jmp _menu

_HPF:
    movb num_val, %al
    call HPF
    # write
    movl $4, %eax
    movl $1, %ebx
    leal display_HPF, %ecx
    movl display_HPF_len, %edx
    int $0x80
    movb num_val, %al
    call print
    jmp _menu


_errpar:
    # stampo a video la stringa d'errore se sono stati inseriti più di 2 parametri
    movl $4, %eax          
    movl $2, %ebx               # standard error
    leal error_par, %ecx
    movl error_par_len, %edx
    int $0x80
    jmp _saveval

_errnopar:
    movl $4, %eax          
    movl $2, %ebx               # standard error
    leal error_no_par, %ecx
    movl error_no_par_len, %edx
    int $0x80
    jmp _saveval


_saveval: 
    movl num_val, %ecx # perchè nella loop in svuotastk decremento ecx 
    # per svuotare lo stack di tutti i valori

# separiamo il salvataggio dei valori in quanto se lo inserissimo nell'etichetta
# _end si sovrascrive continuamente ecx con num_val creando un loop continuo

_end:
    cmp $0, %ecx 
    jne _svuotastk

    # chiudo i file
    movl $6, %eax            # syscall close
    movl file_descr, %ebx    # file descriptor che devo chiudere
    int $0x80

    # esco dal programma
    movl $1, %eax                # syyscall exit
    movl $0, %ebx                # Codice di uscita 0 (uscita normale)
    int $0x80

_svuotastk:
    popl %eax
    loop _svuotastk
    jmp _end
    
_errore_apertura: 
    # stampo a video la stringa d'errore di apertura del file
    movl $4, %eax          
    movl $2, %ebx               # standard error
    leal str_errfile, %ecx
    movl str_errfile_len, %edx
    int $0x80
    jmp _saveval
