#include <stdlib.h>
#include <pthread.h>
#include <sys/stat.h>
#include "queue.h"

typedef struct RequestNode {
    Request *req;
    off_t fileSize;
    struct RequestNode *next;
} RequestNode;

static RequestNode *head = NULL;
static pthread_mutex_t queueMutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t queueNotEmpty = PTHREAD_COND_INITIALIZER;

void enqueue_by_size(Request *req) {
    struct stat st;
    off_t size = 0;
    if (stat(req->filePath, &st) == 0) 
        size = st.st_size;

    RequestNode *newNode = malloc(sizeof(RequestNode));
    newNode->req = req;
    newNode->fileSize = size;
    newNode->next = NULL;

    pthread_mutex_lock(&queueMutex);
    if (!head || size < head->fileSize) {
        newNode->next = head;
        head = newNode;
    } else {
        RequestNode *curr = head;
        while (curr->next && curr->next->fileSize <= size)
            curr = curr->next;
        newNode->next = curr->next;
        curr->next = newNode;
    }
    pthread_cond_signal(&queueNotEmpty);
    pthread_mutex_unlock(&queueMutex);
}

Request *dequeue() {
    pthread_mutex_lock(&queueMutex);
    while (!head)
        pthread_cond_wait(&queueNotEmpty, &queueMutex);

    RequestNode *node = head;
    head = head->next;
    Request *req = node->req;
    free(node);

    pthread_mutex_unlock(&queueMutex);
    return req;
}
