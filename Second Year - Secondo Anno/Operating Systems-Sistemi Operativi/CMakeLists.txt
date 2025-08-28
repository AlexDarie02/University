cmake_minimum_required(VERSION 3.20)

project(ProgettoSO VERSION 1.0 LANGUAGES C)

set(CMAKE_MACOSX_RPATH 1)
set(BUILD_SHARED_LIBS ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_compile_options(-Wall -Wno-deprecated-declarations)

find_package(OpenSSL REQUIRED)

include_directories(SYSTEM ${PROJECT_SOURCE_DIR}/inc ${OPENSSL_INCLUDE_DIR})

add_executable(server
               src/server/main.c
               src/server/request_handler.c
               src/server/cache.c
               src/server/queue.c
               src/errExit.c
               )

target_link_libraries(server
                      ${OPENSSL_LIBRARIES}
                      pthread
                      )

add_executable(client
               src/client.c
               src/errExit.c
               )

target_link_libraries(client
                      ${OPENSSL_LIBRARIES}
                      pthread
                      )

# per rimuovere le FIFO temporanee
add_custom_target(clean_ipc
                  COMMAND ${CMAKE_COMMAND} -E remove -f /tmp/server_fifo /tmp/client_*_fifo
                  )
