#!/bin/bash

echo "Starting compilation..."
# cd ./src/
if javac src/com/mieze/hexbattle/**/*.java --source-path ./src/ -d ./bin/ -Werror -Xlint; then
    echo "Compilation finished (.class files in ./bin/)"
    echo "run ./run.sh to run project"
    exit 0
else
    echo "Compilation FAILED. (See errors above...)"
    exit -1
fi
