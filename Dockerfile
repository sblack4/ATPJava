FROM frolvlad/alpine-oraclejdk8:cleaned 


# update base image and download required glibc libraries
RUN apk update && apk add libaio libnsl && \
    ln -s /usr/lib/libnsl.so.2 /usr/lib/libnsl.so.1

RUN apk add --update \
    git \
    maven \
    wget \
   && rm -rf /var/cache/apk/*

# === INSTANT CLIENT === 
# get oracle instant client from bumpx git repo 
ENV CLIENT_FILENAME instantclient-basic-linux.x64-12.1.0.1.0.zip
# ENV CLIENT_FILENAME instantclient-sqlplus-linux.x64-12.1.0.1.0.zip

WORKDIR /opt/oracle
ADD https://github.com/bumpx/oracle-instantclient/raw/master/${CLIENT_FILENAME} .

RUN LIBS="*/libociei.so */libons.so */libnnz12.so */libclntshcore.so.12.1 */libclntsh.so.12.1" && \
    unzip ${CLIENT_FILENAME} ${LIBS} && \
    for lib in ${LIBS}; do mv ${lib} /usr/lib; done && \
    ln -s /usr/lib/libclntsh.so.12.1 /usr/lib/libclntsh.so && \
    rm ${CLIENT_FILENAME}

ENV ORACLE_BASE /opt/oracle/instantclient_12_1
ENV LD_LIBRARY_PATH /opt/oracle/instantclient_12_1
ENV ORACLE_HOME /opt/oracle/instantclient_12_1
ENV PATH /opt/oracle/instantclient_12_1:$PATH

# === SQLcl === 
ENV SQLCL_DIR /opt/oracle/sqlcl
RUN mkdir ${SQLCL_DIR}
WORKDIR ${SQLCL_DIR}
RUN wget --content-disposition https://github.com/kbhanush/sqlcl4.2/blob/master/sqlcl-4.2.0.16.260.1205-no-jre.zip?raw=true 
RUN unzip sqlcl*
ENV PATH=${SQLCL_DIR}/bin:$PATH

# === Java SDK ===
# sdk in /opt/oracle/tools/java/sdk/oci-java-sdk/lib
# 3rd party libs in /opt/oracle/tools/java/sdk/oci-java-sdk/third-party/lib
RUN mkdir /opt/oracle/database && \
    mkdir /opt/oracle/tools && \
    mkdir /opt/oracle/tools/cli && \
    mkdir /opt/oracle/tools/java && \
    mkdir /opt/oracle/tools/java/sdk

WORKDIR /opt/oracle/tools/java/sdk

ADD https://github.com/oracle/oci-java-sdk/releases/download/v1.2.47/oci-java-sdk.zip .
RUN unzip oci-java-sdk.zip

ENV PATH $PATH:/opt/oracle/database
ENV PATH $PATH:/opt/oracle/tools 
ENV PATH $PATH:/opt/oracle/tools/cli 
ENV PATH $PATH:/opt/oracle/tools/java 
ENV PATH $PATH:/opt/oracle/tools/java/sdk

# === ORACLE JDBC DRIVERS === 
# in /opt/oracle/ojdbc8-full
WORKDIR /opt/oracle/
RUN wget --content-disposition https://github.com/sblack4/ojdbc8-full/blob/master/ojdbc8-full.tar.gz?raw=true && \
    tar xvzf ojdbc8-full.tar.gz

# add our app and stuff 

RUN mkdir /opt/oracle/apps && \
    mkdir /opt/oracle/apps/ATP-REST-Java
ADD . /opt/oracle/apps/ATP-REST-Java
ENV PATH $PATH:/opt/oracle/apps/ATP-REST-Java
ENV TNS_ADMIN /opt/oracle/wallet
RUN mkdir /root/.oci
ADD ./.oci /root/.oci

WORKDIR /opt/oracle/apps/ATP-REST-Java
EXPOSE 3050
CMD [ "sh", "run.sh" ]