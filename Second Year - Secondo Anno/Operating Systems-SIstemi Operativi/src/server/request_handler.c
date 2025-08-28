#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <fcntl.h>
#include <unistd.h>
#include <openssl/sha.h>
#include "request_handler.h"
#include "cache.h"
#include "queue.h"

#define THREAD_POOL_SIZE 2 

static void digest_file(const char *filename, char *char_hash) {
    SHA256_CTX ctx;
    SHA256_Init(&ctx);

    char buffer[4096];
    int file = open(filename, O_RDONLY);
    if (file == -1) { 
        strcpy(char_hash, "FILE_ERROR");
        return; 
    }

    ssize_t bR;
    while ((bR = read(file, buffer, sizeof(buffer))) > 0)
        SHA256_Update(&ctx, buffer, bR);

    if (bR < 0) { strcpy(char_hash, "READ_ERROR"); close(file); return; }

    unsigned char hash[32];
    SHA256_Final(hash, &ctx);
    close(file);

    for (int i = 0; i < 32; i++)
        sprintf(char_hash + (i * 2), "%02x", hash[i]);
    char_hash[64] = '\0';
}

void *handle_Request(void *arg) {
    Request *req = (Request *)arg;
    char hash[65];

    if (req->type == 1) { // QUERY_CACHE
        if (cache_query(req->filePath, hash))
            printf("<Server> Query cache: %s già calcolato\n", req->filePath);
        else
            strcpy(hash, "NOT_FOUND");
    } else { // CALC_HASH
        if (cache_lookup(req->filePath, hash)) {
            printf("<Server> Cache hit per %s\n", req->filePath);
        } else {
            printf("<Server> Cache miss per %s\n", req->filePath);
            digest_file(req->filePath, hash);
            if (strcmp(hash, "FILE_ERROR") && strcmp(hash, "READ_ERROR"))
                cache_insert(req->filePath, hash);
        }
    }

    int clientFIFO = -1;
    for (int i = 0; i < 5; i++) {
        clientFIFO = open(req->clientFifo, O_WRONLY);
        if (clientFIFO != -1) 
            break;
        usleep(100000);
    }

    if (clientFIFO != -1) {
        write(clientFIFO, hash, strlen(hash) + 1);
        close(clientFIFO);
    } else {
        perror("<Server> open client FIFO fallito");
    }

    free(req);
    return NULL;
}

void *worker_Thread(void *arg) {
    while (1) {
        Request *req = dequeue();
        handle_Request(req);
    }
}

void init_ThreadPool() {
    pthread_t tid;
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
        pthread_create(&tid, NULL, worker_Thread, NULL);
        pthread_detach(tid);
    }
}

