#!/bin/bash

GC_OPTS="-XX:+UseG1GC -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:MaxGCPauseMillis=200"
HEAP_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=75 -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof"

java -jar $GC_OPTS $HEAP_OPTS *.jar