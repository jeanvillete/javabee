# ---------------------------------------------------------------------------
# Script to invoke the JavaBee Org App on Linux Platform
# 2012 12 29
# ---------------------------------------------------------------------------
if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
  echo "JAVA_HOME not defined"
  echo "No valid value was defined to JAVA_HOME environment variable, please, do it before!"
  exit 1
fi

APP_BIN_DIR=$(readlink -f $(dirname $0))
CURRENT_DIR="$PWD/"
PARAMETERS="$* -current_directory $CURRENT_DIR"

exec "$JAVA_HOME"/bin/java -jar "$APP_BIN_DIR"/javabee_engine.jar $PARAMETERS
