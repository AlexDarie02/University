.section .data

priority:
.int 0

next_priority:
.int 0

flag:
.int 0

dimensione:
.int 0

.section .text
.global HPF

.type HPF, @function

HPF:
    movb $4, %bl
    divb %bl    # dividiamo al (che contiene il num di valori da pianificatore) per 4
    movb %al, dimensione    # il risultato è il numero di prodotti che abbiamo
    # flag per il controllo del bubblesort inizializzata a zero
    movl $0, flag 

_bubblesort:
    cmpb $0, flag # controllo di aver terminato il riordino
    jne _fine
    movl $1, flag  # usiamo questa flag per fare un'ulteriore controllo per
    # verificare che non siano necessari ulteriori scambi        
    movl $0, %ecx   # in ecx perchè è il contatore
    movl $4, priority # devo spostarmi di 4 bit da ESP (cima stack)

_loop:
    movl dimensione, %eax
    subl $1, %eax
    cmpl %eax, %ecx # confrontiamo il contatore ecx con il num prodotti per 
                    # vedere quando è finito il riordino
    je _bubblesort 

    movl priority, %ebx
    addl $16, %ebx
    movl %ebx, next_priority
    movl priority, %edx # non uso ecx perchè lo sto usando come contatore

    movl (%esp, %edx), %eax # salvo la prima priority in eax
    movl next_priority, %edx 

    cmpl (%esp, %edx), %eax  # confrontiamo la priority successiva con quella prima salvata in eax
    jl _inverti  # se la prima è minore della successiva scambiamo

    cmpl (%esp, %edx), %eax
    je _same

    # altrimenti sono in ordine corretto e vado avanti
    incl %ecx
    addl $16, priority

    jmp _loop

_inverti: 
# scambio tutti e 4 i parametri dei due prodotti da invertire

    movl priority, %edx         # siamo già sulla priorità
    movl next_priority, %eax    

    # scambio le priorità
    movl (%esp, %edx), %ebx
    movl (%esp, %eax), %edi
    movl %edi, (%esp, %edx)
    movl %ebx, (%esp, %eax) 

    # aggiungiamo 4 per spostarci in giù al campo scadenza
    addl $4, %edx
    addl $4, %eax
    # e fare lo scambio
    movl (%esp, %edx), %ebx
    movl (%esp, %eax), %edi
    movl %edi, (%esp, %edx)
    movl %ebx, (%esp, %eax)

    # aggiungiamo 4 per spostarci in giù al campo durata
    addl $4, %edx
    addl $4, %eax
    # e fare lo scambio
    movl (%esp, %edx), %ebx
    movl (%esp, %eax), %edi
    movl %edi, (%esp, %edx)
    movl %ebx, (%esp, %eax)

    # aggiungiamo 4 per spostarci in giù al campo identificativo
    addl $4, %edx
    addl $4, %eax
    # e fare lo scambio
    movl (%esp, %edx), %ebx
    movl (%esp, %eax), %edi
    movl %edi, (%esp, %edx)
    movl %ebx, (%esp, %eax)

    # abbiamo finito di riordinare i due prodotti quindi settiamo la flag a 0
    movl $0, flag

    jmp _loop

_same: 
# siccome la priorità è uguale tra i due prodotti questi vengono riordinati
# in base alla scadenza 

    # salviamo in eax e in ebx la prima e la seconda scadenza

    movl priority, %eax
    addl $4, %eax
    movl (%esp, %eax), %eax
    movl next_priority, %ebx
    addl $4, %ebx
    movl (%esp, %ebx), %ebx

    # confrontiamo se la prima è maggiore della seconda, e nel caso saltiamo a invertirle

    cmpl %ebx, %eax
    jg _inverti 

    incl %ecx # incrementiamo il numero di prodotti riordinati
    addl $16, priority # ci spostiamo alla priorità successiva
    jmp _loop

_fine: 
    ret # finito il riordino dei prodotti nello stack torniamo al programma principale
