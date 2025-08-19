.section .data

tempo:
.int 0

num_parametri:
.int 0

index:
.int 0

penality:
.int 0

display_penality:
.ascii "Penalty: "

display_penality_len:
.long .-display_penality

conclusione:
.ascii "Conclusione: "

conclusione_len:
.long .-conclusione

due_punti:
.ascii ":"

due_punti_len:
.long .-due_punti

a_capo:
.ascii "\n"

a_capo_len:
.long .-a_capo

.section .text
    .global print

.type print, @function


print:
    movl $0, tempo
    movl $0, penality
    movl $4, %ebx # in %al abbiamo il numero di parametri nello stack (id, durata ecc)
    mull %ebx  # moltiplichiamo il numero per 4 per tenere poi traccia nel compare
    # nel ciclo di quando abbiamo finito tutti i parametri 
    movl %eax, num_parametri
    movl $4, %ecx # inizializziamo ecx per spostarci nei vari parametri


ciclo:
    movl %ecx, index
    cmp num_parametri, %ecx
    jg fine

    movl %ecx, %ebx 
    addl $12, %ebx
    movl (%esp,%ebx),%eax

    call itoa # stampiamo l'id

    # stampa dei : 
    movl $4, %eax
    movl $1, %ebx
    leal due_punti, %ecx
    movl due_punti_len, %edx
    int $0x80

    movl tempo, %eax
    call itoa

    # stampa del andata a capo 
    movl $4, %eax
    movl $1, %ebx
    leal a_capo, %ecx
    movl a_capo_len, %edx
    int $0x80

    movl index, %ecx # recupero il valore salvato inizialmente in index
    # perchè ho sovrascritto ecx nella syscall
    
    # per raggiungere il campo della durata
    movl %ecx, %eax
    addl $8, %eax
    movl $0, %ebx # azzero ebx perchè l'ho usato prima nella syscall 
    # e lo riuso per salvare la durata che trovo
    movl (%esp, %eax), %ebx 
    addl %ebx, tempo # aggiungo la durata del prodotto al tempo per avere 
    # il minuto finale di conclusione della realizzazione del prodotto 

    # per raggiungere il campo della scadenza
    movl %ecx,%eax # lo sposto in eax per poter aggiungere 4 senza variare il valore
    # effettivo di ecx che andrà poi incrementato per passare al prodotto dopo
    addl $4, %eax
    movl (%esp,%eax), %ebx  # salvo la scadenza in ebx
    cmpl %ebx, tempo  # confronto il tempo impiegato con la scadenza per 
    # vedere se è necessario calcolare la penality
    jg _penality # se il minuto di conclusione è maggiore di quello di
    # scadenza dobbiamo calcolare la penality
    
    addl $16, %ecx # aumentiamo ecx per passare al prodotto successivo
    jmp ciclo

_penality:
    movl %ecx, %eax
    addl $4, %eax
    movl (%esp, %eax), %ebx # prendiamo il campo scadenza
    movl tempo, %eax
    subl %ebx, %eax # facciamo tempo - scadenza
    # salviamo il risultato della sottrazione in eax
    # per poi moltiplicarlo con la priorità
    # abbiamo cosi trovato le unità di tempo di ritardo rispetto alla
    # scadenza richiesta

    # moltiplichiamo per la priorità
    movl (%esp, %ecx), %edx # prendo il campo priorità
    mull %edx # moltiplico eax in cui ho salvato il risultato della
    # sottrazione tempo - scadenza per la priorità

    addl penality, %eax # incremento la penality con la nuova 
    # penality appena calcolata

    movl %eax, penality # aggiorno la variabile con il nuovo valore di penality

    addl $16, %ecx # aumentiamo ecx per passare al prodotto successivo nel ciclo
    jmp ciclo


fine:
    # stampa della conclusione e della penality
    movl $4, %eax
    movl $1, %ebx
    leal conclusione, %ecx
    movl conclusione_len, %edx
    int $0x80

    # stampa del minuto di conclusione
    movl tempo, %eax
    call itoa

    movl $4, %eax
    movl $1, %ebx
    leal a_capo, %ecx
    movl a_capo_len, %edx
    int $0x80

    # stampa della penality calcolata
    movl $4, %eax
    movl $1, %ebx
    leal display_penality, %ecx
    movl display_penality_len, %edx
    int $0x80

    movl penality, %eax
    call itoa

    # \n
    movl $4, %eax
    movl $1, %ebx
    leal a_capo, %ecx
    movl a_capo_len, %edx
    int $0x80
     # \n
    movl $4, %eax
    movl $1, %ebx
    leal a_capo, %ecx
    movl a_capo_len, %edx
    int $0x80
     # \n
    movl $4, %eax
    movl $1, %ebx
    leal a_capo, %ecx
    movl a_capo_len, %edx
    int $0x80

    ret

