#!/bin/bash
# - PARAMETERS & CONSTANTS
COMMAND=$1

SERVICE_PORT=6091
ADMIN_PORT=$(( $SERVICE_PORT + 100 ))
SIMULATION_NAME='esi-simulation'
WORKING_DIRECTORY="${HOME}/Development/NeoCom/neocom-datamanagement-infinity/NeoCom.DataManagement"
DOCKER_COMMAND="docker-compose --file src/test/resources/docker/docker-compose.yml "
APISIMULATOR_COMMAND="${WORKING_DIRECTORY}/src/test/apisimulator/apisimulator-http-1.4/bin/apisimulator"
APISUMULATOR_ADMIN_PORT=" -admin_port ${ADMIN_PORT} "
APISIMULATOR_OPTIONS=" -p ${SERVICE_PORT} "
APISIMULATOR_SIMULATION="${WORKING_DIRECTORY}/src/test/resources/$SIMULATION_NAME"

export APISIMULATOR_JAVA='/usr/lib/jvm/java-1.11.0-openjdk-amd64'

# - S T A R T
start() {
  cd ${WORKING_DIRECTORY}
#  $DOCKER_COMMAND up &
  echo ">> Starting api simulator with: $APISIMULATOR_SIMULATION"
  echo ">>> Service port: $SERVICE_PORT"
  echo ">>> Administration port: $ADMIN_PORT"
  echo ">>> Simulation: $SIMULATION_NAME"
  ${APISIMULATOR_COMMAND} start $APISIMULATOR_SIMULATION $APISIMULATOR_OPTIONS $APISUMULATOR_ADMIN_PORT &
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
