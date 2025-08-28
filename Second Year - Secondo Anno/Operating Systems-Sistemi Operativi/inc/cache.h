#ifndef CACHE_H
#define CACHE_H

#include "request_handler.h"

int cache_lookup(const char *filePath, char *hashOut);
void cache_insert(const char *filePath, const char *hash);
int cache_query(const char *filePath, char *hashOut);

#endif
