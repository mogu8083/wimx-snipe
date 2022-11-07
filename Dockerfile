FROM openjdk:11.0.16

RUN apt-get update
RUN apt-get install -y vim net-tools telnet

WORKDIR /run

COPY build/libs/snipe.jar snipe.jar

CMD java -jar $JVM_OPTS snipe.jar; bash;