foreground=''

print_usage() {
  printf "Usage: \n-f run in foreground\n"
}

while [[ $# -gt 0 ]]; do
  key="$1"
  case $key in
    -f|--foreground)
	foreground='true'
	shift
	;;
    *) print_usage
       exit 1
       ;;
  esac
done

if [[ $foreground == 'true' ]]
then
	echo "Starting program in foreground..."
	java -classpath bin/ com.mieze.hexbattle.Main
else
	echo "Starting program in background..."
	java -classpath bin/ com.mieze.hexbattle.Main > /dev/null &
	exit 0
fi

