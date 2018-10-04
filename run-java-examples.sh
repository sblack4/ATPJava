#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
JDBC_LIB="/opt/oracle/tools/java/ojdbc8-full/*"
JAVA_SDK="/opt/oracle/tools/java/sdk"
SDK_LIB="$JAVA_SDK/lib/*:$JAVA_SDK/third-party-lib/*"
CLASSPATH="$JDBC_LIB:$SDK_LIB:$DIR/atp-rest-scripts.jar"

java -cp "$CLASSPATH" com.github.sblack4.CLI "$@"
