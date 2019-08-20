# sqs-util
Utility lib for secure SQS

![Build Status](https://jenkins.capra.tv/buildStatus/icon?job=Cantara-sqs-util)
[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active)
[![Known Vulnerabilities](https://snyk.io/test/github/Cantara/sqs-util/badge.svg)](https://snyk.io/test/github/Cantara/sqs-util)

## Getting started
See JavaDoc under
[package-info](https://github.com/Cantara/sqs-util/blob/master/src/main/java/no/cantara/aws/sqs/package-info.java)
for code examples and more info for using this library.


## Binaries
Binaries and dependency information for Maven, Ivy, Gradle and others can be
found at https://mvnrepo.cantara.no.

### Example using Maven
```
<repository>
    <id>cantara-releases</id>
    <name>Cantara Release Repository</name>
    <url>https://mvnrepo.cantara.no/content/repositories/releases/</url>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
</repository>
```


```
<dependency>
    <groupId>no.cantara.aws</groupId>
    <artifactId>sqs-util</artifactId>
    <version>x.y.z</version>
</dependency>
```


## Release notes
### 0.6
- update dependencies
### 0.5
- update aws-sdk dependencies 
### 0.4.1
- update aws-sdk dependencies and update AmazonSQSSecureClient to stop using the deprecated constructors in the AWS SDK. 
### 0.3
- Update dependency versions
### 0.2
- Update version of aws-sdk dependencies to 1.11.78
