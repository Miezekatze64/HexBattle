# HexBattle

## Requirements
Java Runtime Environment (with javac) 1.8.0 or newer.

## Compile and run
1. Install jre (version 1.8.0 or newer)

### Windows
2. Go to source directory:
    ```cmd
    $ cd PROJECT_ROOT\src
    ```

3. Compile using java:
    ```cmd
    $ java com\mieze\hexbattle\**\*.java -d ..\bin\
    ```

4. Go to project root and run the game
    ```cmd
    cd ..
    java -classpath bin com.mieze.hexbattle.Main
    ```

### Linux / Unix / MacOS
2. Compile using bash script:
    ```sh
    ./compile.sh
    ```

3. Run using bash script:
    ```sh
    ./run.sh
    ```
OR  
2./3. Run and compile in one:
    ```sh
    ./run.sh --recompile
    ```
