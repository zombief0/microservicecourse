# microservicecourse
This is a project built during the time I took a udemy course on microservices with spring boot

In fact, I took this course to discover what lies beyond the realm of creating basic Rest Apis with Spring boot.

This source contains everything I learned from the course, and I will start enumerating them in the following lines:
  - creating and starting mutliple microservices on random ports;
  - creating a discovery service based on Netflix Eureka to automatically register the microservices;
  - assigning different instance Ids to multiple instances of a same microservices;
  - creating an api gateway to communicate with those microservices and enabling load balancing;
  - setting and configure a config server to regroup common settings used in microservices;
  - setting a git repository that contains those configurations and enable encrytion on sensitive properties;
  - protecting a microservice with spring security and configuring custom authentication filter to enable JWT authentication;
  - adding a filter to the api gateway to verify access token before sending a request to the microservice;
  - enabling messaging through spring cloud bus and rabbitMq to share updated configurations from the config server to the microservices;
  -  enable spring boot actuator
