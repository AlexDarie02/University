#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include "cache.h"

#define MAX_PATH_LEN 1024

typedef struct CacheEntry {
    char filePath[MAX_PATH_LEN];
    char hash[65];
    struct CacheEntry *next;
} CacheEntry;

static CacheEntry *cacheHead = NULL;
static pthread_mutex_t cacheMutex = PTHREAD_MUTEX_INITIALIZER;

typedef struct PendingEntry {
    char filePath[MAX_PATH_LEN];
    char hash[65];
    int ready;
    pthread_cond_t cond;
    struct PendingEntry *next;
} PendingEntry;

static PendingEntry *pendingHead = NULL;


int cache_lookup(const char *filePath, char *hashOut) {
    pthread_mutex_lock(&cacheMutex);

    // prima cerco in cache già pronta
    CacheEntry *curr = cacheHead;
    while (curr) {
        if (strcmp(curr->filePath, filePath) == 0) {
            strcpy(hashOut, curr->hash);
            pthread_mutex_unlock(&cacheMutex);
            return 1;
        }
        curr = curr->next;
    }

    // cerco se c'è già in elaborazione
    PendingEntry *p = pendingHead;
    while (p) {
        if (strcmp(p->filePath, filePath) == 0) {
            while (!p->ready)
                pthread_cond_wait(&p->cond, &cacheMutex); // aspetto che il risultato sia pronto
            strcpy(hashOut, p->hash);
            pthread_mutex_unlock(&cacheMutex);
            return 1;
        }
        p = p->next;
    }

    // se non esiste, aggiungo in pending
    PendingEntry *newPending = malloc(sizeof(PendingEntry));
    strncpy(newPending->filePath, filePath, MAX_PATH_LEN);
    newPending->ready = 0;
    pthread_cond_init(&newPending->cond, NULL);
    newPending->next = pendingHead;
    pendingHead = newPending;

    pthread_mutex_unlock(&cacheMutex);
    return 0; // hash non pronto, thread deve calcolarlo
}

void cache_insert(const char *filePath, const char *hash) {
    pthread_mutex_lock(&cacheMutex);

    // inserisco in cache
    CacheEntry *newEntry = malloc(sizeof(CacheEntry));
    strncpy(newEntry->filePath, filePath, MAX_PATH_LEN);
    strncpy(newEntry->hash, hash, 65);
    newEntry->next = cacheHead;
    cacheHead = newEntry;


    PendingEntry *p = pendingHead;
    PendingEntry *prev = NULL;
    while (p) {
        if (strcmp(p->filePath, filePath) == 0) {
            strncpy(p->hash, hash, 65);
            p->ready = 1;
            pthread_cond_broadcast(&p->cond); // sveglio tutti i thread in attesa

            // rimuovo da pending
            if (prev) prev->next = p->next;
            else pendingHead = p->next;
            pthread_cond_destroy(&p->cond);
            free(p);
            break;
        }
        prev = p;
        p = p->next;
    }

    pthread_mutex_unlock(&cacheMutex);
}

int cache_query(const char *filePath, char *hashOut) {
    pthread_mutex_lock(&cacheMutex);
    
    CacheEntry *curr = cacheHead;
    while (curr) {
        if (strcmp(curr->filePath, filePath) == 0) {
            strcpy(hashOut, curr->hash);
            pthread_mutex_unlock(&cacheMutex);
            return 1;
        }
        curr = curr->next;
    }

    pthread_mutex_unlock(&cacheMutex);
    return 0;
}
