
#include <stdio.h>
#include <stdlib.h>
#include "errExit.h"

// funzione che stampa un messaggio di errore e termina il programma
void errExit(const char *msg) {
    perror(msg);
    exit(EXIT_FAILURE);
}