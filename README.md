# ATP-SDK_and_JDBC-Java
Named ATP-REST-Java by historic accident, actually
[oci-java-sdk](https://github.com/oracle/oci-java-sdk) examples for Oracle Autonomous Transaciton Processing Service written in Java 

## tl;dr
1. download everything
```bash
git clone https://github.com/cloudsolutionhubs/ATPjavaapp.git
```
2. unzip the libraries 
```bash
tar xvzf lib.tar.gz
```
3. configure your `~/.oci` folder, if you haven't already *
3. run some scripts
```bash
bin/listAutonomousDatabase
```


## Objectives 
This we wanted for this app
- [x] A base image based on Oracle linux
- [x]  A **python**, **Java** and **node.js** image each built on the base image and layered with drivers to connect respective language programs to an ATP database seamlessly Oracle Cloud Infrastructure CLI
    - [x] instant client
    - [x] SQLcl
    - [x] Java SDK
    - [x] Oracle JDBC driver
    - [x] SQL Plus 
- [x] API language scripts for listing, creating, scaling, backing up an ATP instance
- [x] A sample program that can use a wallet .zip file and connect to the database
- [x] Most of this list of scripts 

### scripts 
these are the scripts, 
because the [Class Path](https://docs.oracle.com/javase/tutorial/essential/environment/paths.html) can be long I've written shell scripts 
to make demoing the functionality easier :) 

listed as `COMMAND [-h for help] [arg_1 arg_2 ...]` 
- [x] `createAutonomousDatabase` DBNAME DISPLAYNAME PASSWORD CPUCOUNT STORAGEINTBS
- [x] `deleteAutonomousDatabase` DBOCID
- [x] `getAutonomousDatabase` DBOCID
- [x] `listAutonomousDatabases` - no arguments
- [x] `startAutonomousDatabase` DBOCID
- [x] `stopAutonomousDatabase` DBOCID
- [ ] `backupAutonomousDatabase` DBOCID
- [ ] `restoreAutonomousDatabase` DBOCID time
- [x] `updateAutonomousDatabase` CPUCount StorageInTBs DBOCID
- [x] `ATPConnectionTest` DB_USER DB_PASSWD WALLET_DIR

`ATPConnectionTest` can also be called directly 
if you include a file, `db.config`, like
```text
 DB_USER=admin
 DB_PASSWD=Welcome123456!
 WALLET_DIR=/tmp/wallet_ilovedata
```
to do this either place the db.config file in the `target` folder
or specify the file location like this 
```bash
ATPConnectionText -c /tmp/db.config
```

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

See https://docs.cloud.oracle.com/iaas/Content/API/Concepts/sdkconfig.htm for information 
on setting up the Oracle Cloud config files 
and https://docs.cloud.oracle.com/iaas/Content/API/Concepts/apisigningkey.htm for creating the keys 


1. make    `~/.oci/config`
2. fill with values*
3. generate key `openssl genrsa -out ~/.oci/oci_api_key.pem 2048`
4. make public key
5. add public key to tenancy 
6. pull the image with `docker pull sblack4/open-world-base`
9. run the container with `docker run -it sblack4/open-world-base bash`

### *Now just run commands* 
whatever you want, try

1. one of the above listed commands
2. anything that came with the Oracle Linux 7 image
2. the CLI 
    ```bash
    runCLI
    ```
3. By calling the CLI directly
```
java -cp "target/atp-rest-scripts.jar:lib/*" com.github.sblack4.CLI create -h
```
... and probably be greeted by the help message ...
```bash
Incorrect Usage, please give command
  _  _     _ _        __                    _               
 | || |___| | |___   / _|_ _ ___ _ __    _ | |__ ___ ____ _ 
 | __ / -_) | / _ \ |  _| '_/ _ \ '  \  | || / _` \ V / _` |
 |_||_\___|_|_\___/ |_| |_| \___/_|_|_|  \__/\__,_|\_/\__,_|

 Create, Scale, List, and Delete your ATP - examples in... JAVA!
Usage: CLI [-h] [COMMAND]
  -h, --help   Displays this help message and quits.
Commands:
  create   Create an ATP instance with the JAVA OCI SDK 
  list     Delete an ATP instance with the JAVA OCI SDK 
  Scale    Scale an ATP instance with the JAVA OCI SDK 
  delete    Delete an ATP instance with the JAVA OCI SDK 
  connect  Connect to an ATP instance with the JDBC in JAVA 
  start     Start an ATP instance with the JAVA OCI SDK 
  get       Get an ATP instance with the JAVA OCI SDK 
  stop      Stop an ATP instance with the JAVA OCI SDK 
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
compartmentId=....
```


## Building 
Uses 
- JDK 1.8+
- Maven 3.5
- [Java SDK from GitHub](https://github.com/oracle/oci-java-sdk/releases)
- [JDBC for Oracle Database](https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/index.html)
- [Picocli](https://picocli.info/)

```bash
# you will have to download most deps 
# and place them in the ${BASEDIR}/lib folder
# install the rest
mvn install 

# make that jar
mvn package
```

now you are good to run :) 


