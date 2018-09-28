# ATP-REST-Java 
REST APIs for Oracle Autonomous Transaciton Processing Service written in Java 


## Objectives 
- [x] A base image based on frolvlad/alpine-glibc:alpine-3.8  that includes Oracle instant client 18.3, sqlplus and sqlcl for basic sql connectivity to an oracle ATP database
https://hub.docker.com/r/frolvlad/alpine-glibc/
- [x]  A python, **Java** and node.js image each built on the base image and layered with drivers to connect respective language programs to an ATP database seamlessly Oracle Cloud Infrastructure CLI
    - [x] instant client
    - [x] SQLcl
    - [x] Java SDK
    - [x] Oracle JDBC driver
- [x] API language scripts for listing, creating, scaling, backing up an ATP instance
- [ ] A sample program that can take a mapped ATP credentials .zip file and connect to the database


### folder stuff 
- [x] Instant Client -  /opt/oracle/instantclient
- [x] (user must scp later) Credentials wallet - /opt/oracle/database/\<ServiceName\>/wallet  — include both the zip file and the unzipped files in this folder
- [x] /opt/oracle/tools/java/restapi
- [x] /opt/oracle/tools/java/sdk
- [ ] Sample apps -  /opt/oracle/apps/<app_name>
- [ ] Set PATH variable to include all of the above

## About
Try out the Oracle Cloud Infrastructure Java SDK! I've tried to make this as simple as possible, but to *reallly* use the SDK you must read the docs! For developing with the `java` SDK read the java sdk docs, but if all you want to do is run this app you can get by with the SDK/CLI configuration docs. They will walk you through getting the required values to authenticate. 

- java sdk docs https://docs.cloud.oracle.com/iaas/Content/API/SDKDocs/javasdk.htm 
- download the java sdk https://github.com/oracle/oci-java-sdk/releases 
- sdk/cli config docs https://docs.cloud.oracle.com/iaas/Content/API/Concepts/sdkconfig.htm 
- Download SQLcl https://www.oracle.com/technetwork/developer-tools/sqlcl/overview/index.html 


## Running 

### W/O Docker
To run you'll need to get those authentication values, make sure that you've got java 1.8+, 

1. make    `~/.oci/config`
2. fill with values*
3. generate key `openssl genrsa -out ~/.oci/oci_api_key.pem 2048`
4. make public key
5. add public key to tenancy 
6. download the [Java SDK from GitHub](https://github.com/oracle/oci-java-sdk/releases) and place the jars into the `./lib` folder
7. run the below `java` command from this directory and with your tenancy id!

```
java -cp target/atp-rest-scripts.jar:lib/oci-java-sdk-full-1.2.46.jar\ 
    com.github.sblack4.atp.ATPSharedExample \
    <TENANCY_ID>
```


### W/ Docker
To run you'll need to get those values and make sure that you've got Docker installed

1. make    `~/.oci/config`
2. fill with values*
3. generate key `openssl genrsa -out ~/.oci/oci_api_key.pem 2048`
4. make public key
5. add public key to tenancy 
6. download the [Java SDK from GitHub](https://github.com/oracle/oci-java-sdk/releases) and place the jars into the `./lib` folder
7. add your tenancy ID to the `./run.sh` script
8. build the docker image with `docker build -t javaOci .`
9. run the docker container with `docker run -it javaOci`

```
java -cp target/atp-rest-scripts.jar:lib/oci-java-sdk-full-1.2.46.jar\ 
    com.github.sblack4.atp.ATPSharedExample \
    <TENANCY_ID>
```

\* see this but fill it in with your own values :)  
example from https://docs.cloud.oracle.com/iaas/Content/API/Concepts/sdkconfig.htm
```
[DEFAULT]
user=ocid1.user.oc1..aaaaaaaafakeuser
fingerprint=20:3B:97:13:55:1c:5b:0d:d3:37:d8:50:4e:c5:3a:34
key_file=~/.oci/oci_api_key.pem
tenancy=ocid1.tenancy.oc1..aaaaaaaaba3pv6wkcr4jqae5f15p2b2m2yt2j6rx32uzr4h25vqstifsfdsq
region=us-ashburn-1
```

