#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h> // per le FIFO (open, O_RDONLY, O_WRONLY ecc.)
#include <unistd.h>
#include <string.h>
#include <sys/stat.h>
#include "errExit.h"
#include "request_handler.h"

#define SERVER_FIFO "/tmp/server_fifo"
#define MAX_PATH_LEN 1024

int main() {
    Request req;

    printf("<Client> Inserisci il nome del file: ");
    if (fgets(req.filePath, MAX_PATH_LEN, stdin) == NULL) {
        fprintf(stderr, "<Client> Nessun input letto\n");
        return 1;
    }
    
    // rimuovo il newline alla fine, se presente
    req.filePath[strcspn(req.filePath, "\n")] = '\0';

    printf("Scegli l'operazione: CALC_HASH = 0, QUERY_CACHE = 1: ");
    int choice;
    if (scanf("%d", &choice) != 1 || (choice != 0 && choice != 1)) {
        fprintf(stderr, "<Client> Scelta non valida\n");
        return 1;
    }
    req.type = choice;

    snprintf(req.clientFifo, MAX_PATH_LEN, "/tmp/client_%d_fifo", getpid());

    if (mkfifo(req.clientFifo, 0660) == -1)
        perror("<Client> FIFO privata già esistente (ok)");

    // apro la FIFO del server per inviare la richiesta
    int serverFIFO = open(SERVER_FIFO, O_WRONLY);
    if (serverFIFO == -1)
        errExit("open server_fifo failed");

    if (write(serverFIFO, &req, sizeof(Request)) != sizeof(Request))
        errExit("write request failed");

    close(serverFIFO);

    // apro la FIFO del client per ricevere la risposta
    int clientFIFO = open(req.clientFifo, O_RDONLY);
    if (clientFIFO == -1)
        errExit("open client_fifo failed");

    // leggo il risultato dalla FIFO del client
    char hash[65]; // 64 caratteri + terminatore \0
    if (read(clientFIFO, hash, sizeof(hash)) <= 0)
        errExit("read hash failed");

    if (strcmp(hash, "FILE_ERROR") == 0) {
        printf("<Client> Errore: il file non esiste o non è accessibile.\n");
    } else if (strcmp(hash, "READ_ERROR") == 0) {
        printf("<Client> Errore: lettura del file fallita.\n");
    } else if (strcmp(hash, "NOT_FOUND") == 0) {
        printf("<Client> File non trovato in cache.\n");
    } 

    close(clientFIFO);
    unlink(req.clientFifo);

    return 0;
}
