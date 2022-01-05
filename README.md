# HexBattle

## Requirements
Java Runtime Environment (with javac) 1.8.0 or newer.

## Compile and run
1. Install jre (version 1.8.0 or newer)

2. if you are using bash, just run `./compile.sh` and `./run.sh`

↓ otherwise do this ↓

2. Go to source directory
    ```sh
    cd PROJECT_ROOT/src/
    ```

3. Compile using javac
    ```sh
    javac com/mieze/hexbattle/**/*.java -d ../bin/
    ```

4. Go to project root and run the game
    ```sh
    cd ..
    java -classpath bin/ com.mieze.hexbattle.Main
    ```