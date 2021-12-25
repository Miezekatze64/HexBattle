#!/bin/bash

echo "Starting compilation..."
cd ./src/
javac com/mieze/hexbattle/**/*.java -d ../bin/
echo "Compilation finished (.class files in ./bin/)"
echo "run ./run.sh to run project"
