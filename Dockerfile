FROM openjdk:11.0.16

RUN apt-get update
RUN apt-get install -y vim net-tools telnet iputils-ping

WORKDIR /run

#COPY build/libs/snipe.jar snipe.jar

EXPOSE ["36080", "37080", "36666"]

CMD java -jar $JVM_OPTS snipe.jar; bash;