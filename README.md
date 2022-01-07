# HexBattle

## Requirements
[Java](https://www.oracle.com/java/technologies/) 1.8.0 or newer.

## Compile and run
- Install a [Java Runtime Environment](https://www.oracle.com/java/technologies/downloads/) (version 1.8 or newer [Java 17 recommended])
- Clone the repository
    ```sh
    $ git clone https://github.com/Miezekatze64/HexBattle.git
    ```

### Windows
- Go to source directory:
    ```cmd
    $ cd PROJECT_ROOT\src
    ```

- Compile using javac:
    ```cmd
    $ java com\mieze\hexbattle\**\*.java -d ..\bin\ -verbose
    ```

- Go to project root and run the game
    ```cmd
    cd ..
    java -classpath bin com.mieze.hexbattle.Main
    ```

### Linux / Unix / MacOS
- Compile using bash script:
    ```sh
    $ ./compile.sh
    ```

- Run using bash script:
    ```sh
    $ ./run.sh
    ```

- Or run and compile at same time:
    ```sh
    $ ./run.sh --recompile
    ```
