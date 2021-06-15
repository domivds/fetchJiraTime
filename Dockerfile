#
# builder
#

FROM gradle:7-jdk11 AS builder

RUN mkdir -p /app
COPY . /app
WORKDIR /app
RUN gradle build

#
# actual image
#

FROM openjdk:11-jdk-buster

COPY --from=builder /app/build/libs/fetchJiraTime-0.0.1-SNAPSHOT.jar fetchJiraTime.jar
WORKDIR /

ENTRYPOINT ["java", "-jar", "fetchJiraTime.jar"]
