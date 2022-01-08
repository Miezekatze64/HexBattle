#!/bin/bash

foreground=''

print_usage() {
  printf "Usage: \n-c|--recompile\t\tcompile before running\n"
}

while [[ $# -gt 0 ]]; do
  key="$1"
  case $key in
 #   -f|--foreground)
#	foreground='true'
#	shift
#	;;
    -c|--recompile)
    if ! ./compile.sh; then
        exit 1;
    fi
    shift
    ;;
    *) print_usage
       exit 1
       ;;
  esac
done

#if [[ $foreground == 'true' ]]
#then
	echo "Starting program in foreground..."
	java -classpath bin/ com.mieze.hexbattle.Main
#else
#	echo "Starting program in background..."
#	java -classpath bin/ com.mieze.hexbattle.Main > /dev/null 2>&1 &
#	exit 0
#fi

