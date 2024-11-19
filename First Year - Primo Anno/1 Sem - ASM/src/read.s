.section .data

file_desc:
.int 0

buffer:
.string ""

new_val:
.int 0

old_val:
.int 0

num_valori:
.int 0

controllo:
.int 1

a__capo:
.byte 10

carriage:
.byte 13

virgola:
.byte 44

str_error: 
.ascii "Struttura del file selezionato non conforme. Ricontrollare.\n"

str_error_len:
.long .-str_error

.section .text
    .global read

.type read, @function

read:
    popl %esi # salvo l'indirizzo di dove ritornare dopo la funzione in un registro che non sovrascrivo
    # per poi potern ritornare al main del programma
    movl %ebx, file_desc 
    cmpl $0, %ebx 
    je _error_file

_loop:
     # syscall read
    movl $3, %eax
    movl file_desc, %ebx      # file descriptor
    movl $buffer, %ecx        # buffer
    movl $1, %edx             # un carattere alla volta
    int $0x80

    # controllo se la sys-call ha letto qualcosa dal file
    cmpl $0, %eax
    # non ha letto nulla quindi
    jle _fine

    # essendo che leggo un carattere alla volta salvo nella parte bassa dei registri
    # invece di usare registri a 32 bit per evitare spreco di spazio
    movb buffer, %al
    cmp a__capo, %al
    jne _insertonstk
    jmp _controllo0


_insertonstk:
    # confronto tra carattere letto e virgola e carriage return
    cmpb carriage, %al
    je _loop
    cmpb virgola, %al
    je _controllo0


    # conversione del carattere letto da ASCII a intero 
    movb %al, new_val
    subl $48, new_val
    # uniamo le varie cifre per formare il numero finale
    movb old_val, %al
    movl $10, %ebx 
    mulb %bl 
    movb %al, old_val
    movb new_val, %al
    addb old_val, %al
    movb %al, new_val
    movb %al, old_val
    jmp _loop


_controllo0:
    cmpb $1, controllo
    je _controllo1
    cmpb $2, controllo
    je _controllo2
    cmpb $3, controllo
    je _controllo3
    cmpb $4, controllo
    je _controllo4

_controllo1: 
    # controlliamo che l'id rispetti i limiti 1-127
    cmpb $1, new_val
    jl _error_file
    cmpb $127, new_val
    jg _error_file
    incl controllo
    jmp _caso_fine


_controllo2: 
    # controlliamo la durata
    cmpb $1, new_val
    jl _error_file
    cmpb $10, new_val
    jg _error_file
    incl controllo
    jmp _caso_fine


_controllo3: 
    # la scadenza
    cmpb $1, new_val
    jl _error_file
    cmpb $100, new_val
    jg _error_file
    incl controllo
    jmp _caso_fine


_controllo4: 
    # la priorità
    cmpb $1, new_val
    jl _error_file
    cmpb $5, new_val
    jg _error_file
    movl $1, controllo # abbiamo letto tutti e 4 i campi del prodotto
    # quindi torna a 1 per ricominciare con la lettura del eventuale successivo
    jmp _caso_fine


_caso_fine: 
    # terminato di leggere un valore, c'è virgola o \n o \r
    push new_val
    cmpb virgola, %al
    jne _contaval
    cmpb a__capo, %al
    jne _contaval
    cmpb carriage, %al
    jne _contaval
    jmp _loop


_contaval: 
    # contiamo i valori che mettiamo sullo stack per poi svuotarlo
    # una volta usciti dalla funzione
    incl num_valori
    movl $0, new_val # azzeriamo per leggere il prossimo valore
    movl $0, old_val
    jmp _loop


_error_file:
    movl $4, %eax
    movl $2, %ebx
    leal str_error, %ecx
    movl str_error_len, %edx
    int $0x80

    movl $90,%ebx # flag di errore 
    jmp _fine


_fine:
    movl num_valori, %ecx # lo salviamo in ecx perchè poi nel pianificatore
    # lo carichiamo in num_val
    pushl %esi # ritornarniamo alla funzione
    ret

