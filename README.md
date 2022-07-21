# HexBattle

## Requirements
[Java](https://www.oracle.com/java/technologies/) 1.8.0 or newer.

## Compile and run
- Install a [Java Runtime Environment](https://www.oracle.com/java/technologies/downloads/) (version 1.8 or newer [Java 18 recommended])
- Clone the repository
```bash
$ git clone https://github.com/Miezekatze64/HexBattle.git
```


### Linux / Unix / MacOS
- Compile using bash script:
```bash
./compile.sh
```

- Run using bash script:
```bash
./run.sh
```

### OR
- Run and compile at same time:
```bash
./run.sh --recompile
```
### Windows
- Go to source directory:
```ps
cd PROJECT_ROOT\src
```

- Compile using javac:
```bash
javac com\mieze\hexbattle\**\*.java -d ..\bin\ -verbose
```

- Go to project root and run the game
```bash
cd ..
java -classpath bin com.mieze.hexbattle.Main
```
