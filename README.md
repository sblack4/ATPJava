# ATP-REST-Java 
REST APIs for Oracle Autonomous Transaciton Processing Service written in Java 

## about
- java sdk docs https://docs.cloud.oracle.com/iaas/Content/API/SDKDocs/javasdk.htm 
- sdk/cli config docs https://docs.cloud.oracle.com/iaas/Content/API/Concepts/sdkconfig.htm 

## running 

1. make    `~/.oci/config`
2. fill with values*
3. generate key `openssl genrsa -out ~/.oci/oci_api_key.pem 2048`
4. make public key
5. add public key to tenancy 


```
java -cp target/atp-rest-scripts.jar:lib/oci-java-sdk-full-1.2.46.jar com.github.sblack4.atp.ATPSharedExample ocid1.tenancy.oc1..aaaaaaaawrgt5au6hbledhhyas2secm3q2atqiuvihck45rbi3jyc5tfyfga
```



\* see this but fill it in with your own values :) 
```
[DEFAULT]
user=ocid1.user.oc1..aaaaaaaat5nvwcna5j6aqzjcaty5eqbb6qt2jvpkanghtgdaqedqw3rynjq
fingerprint=20:3b:97:13:55:1c:5b:0d:d3:37:d8:50:4e:c5:3a:34
key_file=~/.oci/oci_api_key.pem
tenancy=ocid1.tenancy.oc1..aaaaaaaaba3pv6wkcr4jqae5f15p2b2m2yt2j6rx32uzr4h25vqstifsfdsq
region=us-ashburn-1
```

