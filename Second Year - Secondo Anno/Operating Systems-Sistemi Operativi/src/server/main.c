#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <pthread.h>
#include "request_handler.h"
#include "queue.h"
#include "errExit.h"

#define SERVER_FIFO "/tmp/server_fifo"

int main() {
    if (mkfifo(SERVER_FIFO, 0660) == -1)
        perror("<Server> FIFO già esistente (ok)");

    int serverFIFO = open(SERVER_FIFO, O_RDONLY);
    int serverFIFO_extra = open(SERVER_FIFO, O_WRONLY);
    
    if (serverFIFO == -1) 
        errExit("open server_fifo failed");
    if (serverFIFO_extra == -1) 
        errExit("open serverFd_extra failed");

    printf("<Server> In ascolto su %s...\n", SERVER_FIFO);

    // avvio il thread pool
    init_ThreadPool();

    // ciclo principale di accodamento
    while (1) {
        Request *req = malloc(sizeof(Request));
        ssize_t bytes = read(serverFIFO, req, sizeof(Request));
        if (bytes <= 0) {
            free(req);
            continue;
        }
        enqueue_by_size(req);
    }

    close(serverFIFO);
    unlink(SERVER_FIFO);
    return 0;
}
