#!/bin/bash


JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"

java $JAVA_OPTS -cp "target/atp-rest-scripts.jar:lib/*" com.github.sblack4.CLI "$@"

