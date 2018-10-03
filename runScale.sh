#!/bin/bash

ls /root

java -cp "target/atp-rest-scripts.jar:lib/*" com.github.sblack4.ScaleATP \
  -cId ocid1.tenancy.oc1..aaaaaaaawrgt5au6hbledhhyas2secm3q2atqiuvihck45rbi3jyc5tfyfga \
  -Id ocid1.autonomousdatabase.oc1.phx.abyhqljsi26fy2brenq4qxwsjcwg537p6zz643v5exmtwrtynqtkdyewvrsa
