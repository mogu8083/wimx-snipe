FROM cimg/openjdk:11.0.13

RUN sudo apt-get update
RUN sudo apt-get install vim

WORKDIR /home/jem/run

COPY build/libs/snipe.jar snipe.jar

RUN mkdir -p /home/jem/run/logs/37082
RUN mkdir -p /home/jem/run/logs/37080
RUN mkdir -p /home/jem/run/logs/37081

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev-client", "-Dtcp.ip=10.10.0.251", "-Dtcp.port=37082", "-Dtcp.port=37080", "-Ddevice.suffix=D", "-Dthread.count=300", "snipe.jar"]