#!/usr/bin/env bash

# the cheat install
# ./install.sh [installDir]

echo "Installer for use on Oracle Linux"
echo "Usage: ./install.sh [installDir]"

# figure our where to install
ARG1="$1"
homedir=~
eval homedir=${homedir}
APP_DIR=${ARG1:-"$homedir"}

# get jdk
yum update && yum install -y java-1.8.0-openjdk.x86_64
yum install -y java-1.8.0-openjdk.x86_64

# make install folder
cd ${APP_DIR}
mkdir ATPJava
cd ATPJava

# include --content-disposition
# because otherwise it names file with params (eg ATPJava.tar.gz?raw=true)
wget --content-disposition https://github.com/sblack4/ATP-REST-Java/blob/master/ATPJava.tar.gz?raw=true
wget --content-disposition https://github.com/sblack4/ATP-REST-Java/blob/master/lib.tar.gz?raw=true

tar xvzf ATPJava.tar.gz
tar xvzf lib.tar.gz

# run it
./run.sh --help
