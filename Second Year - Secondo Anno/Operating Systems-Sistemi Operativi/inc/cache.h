#ifndef CACHE_H
#define CACHE_H

#include <stddef.h>
#include "request_handler.h"

int cache_lookup(const char *filePath, char *hashOut);
void cache_insert(const char *filePath, const char *hash);
void get_cache_content(char *out, size_t maxLen);

#endif
