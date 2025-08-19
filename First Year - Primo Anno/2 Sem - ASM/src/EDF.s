.section .data

scad:
.int 0

next_scad:
.int 0

flag:
.int 0

dimensione:
.int 0

.section .text
.global EDF

.type EDF, @function

EDF:
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
    movl $8, scad # devo spostarmi di 8 bit da ESP (cima stack)

_loop:
    movl dimensione, %eax
    subl $1, %eax
    cmpl %eax, %ecx # confrontiamo il contatore ecx con il num prodotti per 
    # vedere quando è finito il riordino
    je _bubblesort 

    movl scad, %ebx
    addl $16, %ebx
    movl %ebx, next_scad
    movl scad, %edx # non uso ecx perchè lo sto usando come contatore

    movl (%esp, %edx), %eax # salvo la prima scadenza in eax
    movl next_scad, %edx

    cmpl (%esp, %edx), %eax  # confrontiamo la next_scad con quella prima salvata in eax
    jg _inverti  # se la prima è maggiore di next_scad scambiamo

    cmpl (%esp, %edx), %eax
    je _same

    # altrimenti sono in ordine corretto e vado avanti
    incl %ecx
    addl $16, scad

    jmp _loop

_inverti: 
    # scambio tutti e 4 i parametri dei due prodotti da invertire

    movl scad, %ebx
    subl $4, %ebx
    movl %ebx, %edx # cosi da potermi posizionare alla prima priorità

    movl next_scad, %ebx
    subl $4, %ebx
    movl %ebx, %eax # per potermi posizionare alla seconda priorità

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
# siccome la scadenza è uguale tra i due prodotti questi vengono riordinati
# in base alla priorità 

    # salviamo in eax e in ebx la prima e la seconda priorità

    movl scad, %eax
    subl $4, %eax
    movl (%esp, %eax), %eax
    movl next_scad, %ebx
    subl $4, %ebx
    movl (%esp, %ebx), %ebx

    # confrontiamo se la prima è minore della seconda, e nel caso saltiamo a invertirle

    cmpl %ebx, %eax
    jle _inverti 

    incl %ecx # incrementiamo il numero di prodotti riordinati
    addl $16, scad # ci spostiamo alla scadenza successiva
    jmp _loop

_fine: 
    ret # finito il riordino dei prodotti nello stack torniamo al programma principale

