# It must be in the same location as the Dockerfile.
#   > influxdb.conf
#   > influxdb_1.8.9_amd64.deb
# influxdb_1.8.9_amd64.deb Download URL
# https://dl.influxdata.com/influxdb/releases/influxdb_1.8.9_amd64.deb

FROM ubuntu:20.04

LABEL maintainer = "KMH"
LABEL "purpose" = "influxdb-1.8"

# baseFile download
RUN apt-get update
RUN apt-get install vim -y
RUN apt-get install wget -y
RUN apt-get install curl -y
RUN apt install net-tools -y

# install influxdb
COPY influxdb_1.8.9_amd64.deb .
RUN dpkg -i influxdb_1.8.9_amd64.deb

# InfluxDB Data Storage Change
RUN mkdir -p /data2/influxdb/meta
RUN mkdir -p /data2/influxdb/data
RUN mkdir -p /data2/influxdb/wal
RUN chown -R influxdb:influxdb /data2/influxdb
RUN rm /etc/influxdb/influxdb.conf
COPY ./conf/influxdb.conf /etc/influxdb/influxdb.conf

# InfluxDB service enable and create user, database
RUN service influxdb start && influx -port 48086 -execute \
    "create user ulalalab with password 'dak102938$&' with all privileges; create database wimx;"

# InfluxDB service start
CMD /usr/bin/influxd -config /etc/influxdb/influxdb.conf

EXPOSE 48086
EXPOSE 48088