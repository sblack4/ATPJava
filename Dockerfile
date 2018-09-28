FROM frolvlad/alpine-oraclejdk8:cleaned 


# update base image and download required glibc libraries
RUN apk update && apk add libaio libnsl && \
    ln -s /usr/lib/libnsl.so.2 /usr/lib/libnsl.so.1

RUN apk add --update \
    git \
    maven \
   && rm -rf /var/cache/apk/*

# get oracle instant client from bumpx git repo
# ENV CLIENT_FILENAME instantclient-basic-linux.x64-12.1.0.1.0.zip
ENV CLIENT_FILENAME instantclient-sqlplus-linux.x64-12.1.0.1.0.zip

WORKDIR /opt/oracle/lib

# download instant client zip file from git repo
ADD https://github.com/bumpx/oracle-instantclient/raw/master/${CLIENT_FILENAME} .

RUN LIBS="*/libociei.so */libons.so */libnnz12.so */libclntshcore.so.12.1 */libclntsh.so.12.1" && \
    unzip ${CLIENT_FILENAME} ${LIBS} && \
    for lib in ${LIBS}; do mv ${lib} /usr/lib; done && \
    ln -s /usr/lib/libclntsh.so.12.1 /usr/lib/libclntsh.so && \
    rm ${CLIENT_FILENAME}

ENV ORACLE_BASE /opt/oracle/lib/instantclient_12_1
ENV LD_LIBRARY_PATH /opt/oracle/lib/instantclient_12_1
ENV ORACLE_HOME /opt/oracle/lib/instantclient_12_1
ENV TNS_ADMIN /opt/oracle/lib/wallet_NODEAPPDB2
ENV PATH /opt/oracle/lib/instantclient_12_1:$PATH

# add our app and stuff 
RUN mkdir /opt/ATP-REST-Java
ADD . /opt/ATP-REST-Java
RUN mkdir /root/.oci
ADD ./.oci /root/.oci

WORKDIR  /opt/ATP-REST-Java
EXPOSE 3050
CMD [ "sh", "run.sh" ]