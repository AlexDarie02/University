# Comandi
Comandi principali per fare il build, pulire le FIFO e il progetto:

```bash
# Build del progetto
cmake -S . -B build
cmake --build build --parallel

# Pulizia delle FIFO
cmake --build build --target clean_ipc

# Pulizia del progetto
rm -rf build/
rm -f server client
