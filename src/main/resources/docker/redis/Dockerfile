FROM redis:7.0.5

#Default
RUN apt-get update
RUN apt-get install -y vim net-tools curl procps

RUN mkdir -p /data
RUN chmod 775 -R /data