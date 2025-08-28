#ifndef QUEUE_H
#define QUEUE_H

#include "request_handler.h"

void enqueue_by_size(Request *req);
Request *dequeue();

#endif
