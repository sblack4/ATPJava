FROM frolvlad/alpine-oraclejdk8:cleaned 

# === UPDATE BASE IMAGE ====
RUN echo "=== UPDATING BASE IMAGE ==="

RUN apk update && apk add libaio libnsl && \
    ln -s /usr/lib/libnsl.so.2 /usr/lib/libnsl.so.1

RUN apk add --update \
    git \
    curl \
    bash \
    wget \
    gcc \ 
    make \ 
    openssl-dev \
    libffi-dev \
    musl-dev \
    clang \ 
    python3-dev \
    ncurses \ 
   && rm -rf /var/cache/apk/*


# === INSTALL INSTANT CLIENT ===
RUN echo "=== INSTALLING INSTANT CLIENT ==="
WORKDIR /opt/oracle

# get oracle instant client from bumpx git repo 
ENV CLIENT_FILENAME instantclient-basic-linux.x64-12.1.0.1.0.zip
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


# === INSTALL SQLPLUS ===
RUN echo "=== INSTALLING SQLPLUS ==="
WORKDIR /opt/oracle

# get oracle instant client from bumpx git repo 
ENV CLIENT_FILENAME instantclient-sqlplus-linux.x64-12.1.0.1.0.zip
ADD https://github.com/bumpx/oracle-instantclient/raw/master/${CLIENT_FILENAME} .

RUN unzip ${CLIENT_FILENAME} && \
    mkdir instantclient_12_1/bin && \
    mv instantclient_12_1/sqlplus instantclient_12_1/bin && \
    mkdir instantclient_12_1/sqlplus && \
    mkdir instantclient_12_1/sqlplus/admin && \
    mv instantclient_12_1/glogin.sql instantclient_12_1/sqlplus/admin && \
    mkdir instantclient_12_1/lib && \
    mv instantclient_12_1/*.so instantclient_12_1/lib

ENV PATH /opt/oracle/instantclient_12_1/bin:$PATH


# === INSTALL SQLCL ===
RUN echo '=== INSTALLING SQLCL ==='
ENV SQLCL_DIR /opt/oracle/sqlcl
RUN mkdir ${SQLCL_DIR}
WORKDIR ${SQLCL_DIR}
RUN wget --content-disposition https://github.com/kbhanush/sqlcl4.2/blob/master/sqlcl-4.2.0.16.260.1205-no-jre.zip?raw=true 
RUN unzip sqlcl*
ENV PATH=${SQLCL_DIR}/sqlcl/bin:$PATH


# === INSTALL OCI CLI === 
WORKDIR /root
RUN pip3 install oci-cli
# RUN curl -O https://raw.githubusercontent.com/oracle/oci-cli/master/scripts/install/install.sh 


