FROM oraclelinux:7-slim

MAINTAINER oracle

RUN yum -y install java-1.8.0-openjdk && rm -rf /var/cache/yum

ENV JAVA_HOME /usr/lib/jvm/java-openjdk


# Installing python pip

RUN echo "Installing EPEL, python-pip, unzip, libaio, oci_cli, requests, cx_Oracle"  && \
    yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm && \
    yum -y install python-pip &&\
    yum -y install unzip && \
    yum -y install libaio && \ 
    yum -y install nodejs npm --enablerepo=epel && \
    yum -y install git && \
    yum -y install nano && \
    yum clean all

    
    
RUN echo 'installing oci_cli, requests, cx_Oracle' && \
    pip install oci_cli requests cx_Oracle



# Setup folder structure


#Setup oracle instant client and sqlcl

ENV SQLPLUS oracle-instantclient12.2-sqlplus-12.2.0.1.0-1.x86_64.rpm
ENV SQLCL sqlcl-18.2.0.zip
ENV INSTANT_CLIENT oracle-instantclient12.2-basic-12.2.0.1.0-1.x86_64.rpm
# ENV NODEJS node-v8.12.0-linux-x64.tar.xz


# set working directory
WORKDIR /opt/oracle/lib

# Add instant client, sqlcl and sqlplus rpm's to image
ADD ${CLIENT_FILENAME} ${SQLCL} ${SQLPLUS} ./


RUN echo "Installing instant client........" && \
   rpm -ivh ${INSTANT_CLIENT} && \
   echo "Installing SQL*Plus..........." && \
   rpm -ivh ${SQLPLUS} && \
   unzip ${SQLCL} && \
   rm ${INSTANT_CLIENT} ${SQLPLUS} ${SQLCL} && \
   mkdir -p /opt/oracle/database/wallet

#set env variables
#ENV ORACLE_BASE /opt/oracle/lib/instantclient_12_2
ENV LD_LIBRARY_PATH /usr/lib/oracle/12.2/client64/lib/:$LD_LIBRARY_PATH
ENV TNS_ADMIN /opt/oracle/database/wallet
#ENV ORACLE_HOME /opt/oracle/lib/instantclient_12_2
ENV PATH $PATH:/usr/lib/oracle/12.2/client64/bin:/opt/oracle/lib/sqlcl/bin

WORKDIR /opt/oracle/tools/nodejs
RUN mkdir sdk restapi apps && \
    npm install oracledb http-signature jssha 
# Get the ATPConnectionTest node app
WORKDIR /opt/oracle/tools/nodejs/apps
RUN git clone https://github.com/kbhanush/ATPConnectionTest
EXPOSE 3050



# Uninstall packages
RUN echo "Cleaning up yum packages........................." && \
    yum -y remove unzip && \
    yum -y remove git
   
