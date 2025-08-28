#ifndef REQUEST_HANDLER_H
#define REQUEST_HANDLER_H

#define MAX_PATH_LEN 1024
#define MAX_THREADS 2

typedef struct {
    char filePath[MAX_PATH_LEN];
    char clientFifo[MAX_PATH_LEN];
    int type;
} Request;


void *handle_Request(void *arg);
void init_ThreadPool();

#endif
