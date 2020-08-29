FROM openjdk:14

COPY target/lib/* /deployments/lib/
COPY target/*-runner.jar /deployments/app.jar

EXPOSE 8082

CMD java -jar /deployments/app.jar -DpreferIPv4Stack