#!/bin/bash
# - PARAMETERS & CONSTANTS
COMMAND=$1
DOCKER_COMMAND='docker-compose --file src/test/resources/docker/docker-compose.yml '
APISIMULATOR_COMMAND='src/test/apisimulator/apisimulator-http-1.4/bin/apisimulator'
APISUMULATOR_ADMIN_PORT=' -admin_port 6192 '
APISIMULATOR_OPTIONS=' -p 6092 '
APISIMULATOR_SIMULATION='src/test/apisimulator/esi-simulation'
WORKING_DIRECTORY='/home/adam/Development/NeoCom/neocom-datamanagement-infinity/NeoCom.DataManagement'

export APISIMULATOR_JAVA='/usr/lib/jvm/java-1.11.0-openjdk-amd64'

# - S T A R T
start() {
  cd ${WORKING_DIRECTORY}
#  $DOCKER_COMMAND up &
  echo "Starting api simulator with: $APISIMULATOR_SIMULATION"
  $APISIMULATOR_COMMAND start $APISIMULATOR_SIMULATION $APISIMULATOR_OPTIONS $APISUMULATOR_ADMIN_PORT &
}
stop() {
  cd ${WORKING_DIRECTORY}
#  $DOCKER_COMMAND down &
  echo "Stopping api simulator..."
  $APISIMULATOR_COMMAND stop $APISIMULATOR_SIMULATION $APISUMULATOR_ADMIN_PORT &
}

case $COMMAND in
'start')
  start
  ;;
'stop')
  stop
  ;;
'restart')
  stop
  start
  ;;
*)
  echo "Usage: $0 { start | stop | restart }"
  echo
  exit 1
  ;;
esac
exit 0