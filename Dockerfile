FROM bellsoft/liberica-openjdk-alpine:11
MAINTAINER mistergo.com
COPY build/libs/reminder_first-0.0.1-SNAPSHOT.jar reminder.jar
ENTRYPOINT ["java","-jar","/reminder.jar"]