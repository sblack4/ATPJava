#!/bin/bash

ls /root

java -cp "target/atp-rest-scripts.jar:lib/*" com.github.sblack4.CreateATP \
  -cId ocid1.tenancy.oc1..aaaaaaaawrgt5au6hbledhhyas2secm3q2atqiuvihck45rbi3jyc5tfyfga
