FROM haproxy:2.6.6

USER root

RUN apt-get update
RUN apt-get install -y vim net-tools procps telnet

#COPY ./conf/haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg
#COPY conf/haproxy.cfg /etc/haproxy/haproxy.cfg
#RUN chmod 775 /etc/haproxy/haproxy.cfg

#CMD /bin/sh