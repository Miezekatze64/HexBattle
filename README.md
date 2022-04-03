# HexBattle

## Requirements
[Java](https://www.oracle.com/java/technologies/) 1.8.0 or newer.

## Compile and run
- Install a [Java Runtime Environment](https://www.oracle.com/java/technologies/downloads/) (version 1.8 or newer [Java 17 recommended])
- Clone the repository
```
git clone https://github.com/Miezekatze64/HexBattle.git
```


### Linux / Unix / MacOS
- Compile using bash script:
```
./compile.sh
```

- Run using bash script:
```
./run.sh
```

### OR
- Run and compile at same time:
```
./run.sh --recompile
```
### Windows
- Go to source directory:
```
cd PROJECT_ROOT\src
```

- Compile using javac:
```
java com\mieze\hexbattle\**\*.java -d ..\bin\ -verbose
```

- Go to project root and run the game
```
cd ..
java -classpath bin com.mieze.hexbattle.Main
```
