#!/bin/bash

echo "Starting compilation..."
cd ./src/
if javac com/mieze/hexbattle/**/*.java -d ../bin/ -Werror -Xlint -verbose; then
    echo "Compilation finished (.class files in ./bin/)"
    echo "run ./run.sh to run project"
    exit 0
else
    echo "Compilation FAILED. (See errors above...)"
    exit -1
fi
