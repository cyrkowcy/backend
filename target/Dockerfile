FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
COPY *.jar /opt/app
CMD ["java", "-jar", "/opt/app/backend.jar"]
