#!/usr/bin/env bash

ARG1="$1"
homedir=~
eval homedir=${homedir}
APP_DIR=${ARG1:-"$homedir"}
cd ${APP_DIR}
yum update && yum install -y java-1.8.0-openjdk.x86_64
yum install -y java-1.8.0-openjdk.x86_64
mkdir ATPJava
cd ATPJava
wget https://github.com/sblack4/ATP-REST-Java/releases/download/V0.2/atp-rest-scripts.jar
mkdir lib
cd lib
# install the JDBC drivers
wget https://github.com/sblack4/ojdbc8-full/raw/master/ojdbc8-full.tar.gz
# install teh OCI JDK
wget https://github.com/oracle/oci-java-sdk/releases/download/v1.2.48/oci-java-sdk.zip
tar xzfv ojdbc8-full.tar.gz
unzip oci-java-sdk
mv ojdbc8-full/*.jar .
mv lib/*.jar .
mv third-party/lib/*.jar .
# install picocli
wget http://central.maven.org/maven2/info/picocli/picocli/3.6.1/picocli-3.6.1.jar
cd ..
curl -O https://raw.githubusercontent.com/sblack4/ATP-REST-Java/master/bin/run.sh
chmod +x run.sh
mkdir target
mv atp-rest-scripts.jar target
./run.sh --help
