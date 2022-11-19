FROM openjdk:11.0.16

RUN apt-get update
RUN apt-get install -y vim net-tools telnet iputils-ping

WORKDIR /run

EXPOSE 36080 37080

CMD java -jar $JVM_OPTS snipe-server.jar; bash;