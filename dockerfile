FROM java:8
MAINTAINER kongxr<toeat777@gmail.com>
ADD kxr-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8034
ENTRYPOINT ["java","-jar","demo.jar"]