#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <sys/stat.h>
#include "errExit.h"
#include "request_handler.h"

#define SERVER_FIFO "/tmp/server_fifo"
#define MAX_PATH_LEN 1024

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Per calcolare hash: 0, per visualizzare la cache: 1\n");
        return 1;
    }

    Request req;
    int choice = atoi(argv[1]);

    if (choice != 0 && choice != 1) {
        fprintf(stderr, "<Client> Operazione non valida\n");
        return 1;
    }

    req.type = choice;

    if (choice == 0) {
        printf("<Client> Inserisci il percorso del file: ");
        if (fgets(req.filePath, MAX_PATH_LEN, stdin) == NULL) {
            fprintf(stderr, "<Client> Nessun input letto\n");
            return 1;
        }
        req.filePath[strcspn(req.filePath, "\n")] = '\0';
    } else {
        req.filePath[0] = '\0'; // per QUERY_CACHE non serve inserire un file
    }

    snprintf(req.clientFifo, MAX_PATH_LEN, "/tmp/client_%d_fifo", getpid());

    if (mkfifo(req.clientFifo, 0660) == -1)
        perror("<Client> FIFO privata già esistente (ok)");

    int serverFIFO = open(SERVER_FIFO, O_WRONLY);
    if (serverFIFO == -1)
        errExit("open server_fifo failed");

    if (write(serverFIFO, &req, sizeof(Request)) != sizeof(Request))
        errExit("write request failed");
    close(serverFIFO);

    int clientFIFO = open(req.clientFifo, O_RDONLY);
    if (clientFIFO == -1)
        errExit("open client_fifo failed");

    char buffer[4096];
    ssize_t n = read(clientFIFO, buffer, sizeof(buffer) - 1);
    if (n <= 0)
        errExit("read failed");
    buffer[n] = '\0';

    if (choice == 0) {
        if (strcmp(buffer, "FILE_ERROR") == 0)
            printf("<Client> Errore: il file non esiste o non è accessibile.\n");
        else if (strcmp(buffer, "READ_ERROR") == 0)
            printf("<Client> Errore: lettura del file fallita.\n");
        else
            printf("<Client> Hash di %s: %s\n", req.filePath, buffer);
    } else {
        printf("<Client> Contenuto della cache:\n%s\n", buffer);
    }

    close(clientFIFO);
    unlink(req.clientFifo);

    return 0;
}
