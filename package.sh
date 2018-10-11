#!/bin/bash

dest_dir="java/sdk"

mkdir java
mkdir java/sdk

cp bin/* ${dest_dir}
cp java-runner.sh ${dest_dir}
cp target/atp-rest-scripts.jar ${dest_dir}
