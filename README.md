# sqs-util
Utility lib for secure SQS

![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Cantara/sqs-util) ![Build Status](https://jenkins.quadim.ai/buildStatus/icon?job=sqs-util)
[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active)
[![Known Vulnerabilities](https://snyk.io/test/github/Cantara/sqs-util/badge.svg)](https://snyk.io/test/github/Cantara/sqs-util)


## Getting started

This library provides an extension of the standard AmazonSQSClient, adding the
following features in a transparent way:

*   Strong client-side encryption.
*   Compression of messages.
*   Supports arbitrary large messages. (Messages larger than 240 KB after compression and encryption will use S3).
*   Supports automatic publishing of SNS events when SQS messages are sent.

For more details, please refer to the Amazon SQS documention:

*   [Amazon Simple Queue Service (SQS)](https://aws.amazon.com/sqs/)
*   [Amazon SQS JavaDoc](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/services/sqs/)

## Requirements
Java 6 or later is required. Further, since strong encryption is used, _Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy_ must be installed (available from Oracle).

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

## Code examples
### Sending a message

``` 
AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...); 
String queueName = ...; 
String payload = ...;

String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();

// Add a message attribute "foo".
MessageAttributeValue someAttribute = new MessageAttributeValue().withDataType("String").withStringValue("bar");

SendMessageRequest messageRequest = new SendMessageRequest()
.withQueueUrl(queueUrl)
.withMessageBody(payload)
.addMessageAttributesEntry("foo", someAttribute);

String messageId = sqsClient.sendMessage(messageRequest).getMessageId();
```

### Receiving messages
```
AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...);
String queueName = ...;

String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest(queueUrl))
.withMaxNumberOfMessages(10)
.withWaitTimeSeconds(20)            // Enables long-polling
.withMessageAttributeNames("All");  // Enables the reception of all message attributes

List<Message> messages = sqsClient.receiveMessage(receiveRequest).getMessages();
for (Message message : messages) {
process(message);

// Remember to delete the message, or it will be redelivered.
sqsClient.deleteMessage(queueUrl, message.getReceiptHandle()); }
```

### File Transfers

This library also offers an API for transferring files with meta data using a combination of SQS and S3.

A file event is sent (as a JSON message) over SQS. It contains meta data for the file as well as a reference to an S3 object. Upon reception of the file event message, the API is used to download the file from S3 to a local file.

![](https://raw.githubusercontent.com/Cantara/sqs-util/master/src/main/javadoc/no/cantara/aws/sqs/doc-files/file-transfer.png)

### Sending a file
``` 
AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...); 
File sourceFile = ...; 
String queueName = ...;

String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
SendMessageRequest request = new SendMessageRequest()
.withQueueUrl(queueUrl);
.addMessageAttributesEntry("SomeMessageType",
new MessageAttributeValue().withDataType("String").withStringValue("orderAttachment"));

FileTransferEvent sentEvent = new FileTransferEvent();
sentEvent.setFilename("photo.jpeg");
sentEvent.setSize(sourceFile.length());
sentEvent.setS3Object(UUID.randomUUID().toString());

// Example user-defined properties.
sentEvent.getAttributes().put("pointId", "123456");
sentEvent.getAttributes().put("orderId", "789");

FileTransferUtil.sendFile(sqsClient, request, sentEvent, sourceFile);
```


### Receiving a file
``` 
AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...); 
String queueName = ...; 
String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();

// Receive (zero or more) SQS messages.
Message message = sqsClient.receiveMessage(queueUrl).getMessages().get(0);

// Check message type and download file if appropriate.
String messageType = message.getMessageAttributes().get("SomeMessageType").getStringValue();
if ("orderAttachment".equals(messageType)) {
File targetFile = File.createTempFile("target", ".tmp");
FileTransferEvent receivedEvent = FileTransferUtil.receiveFile(sqsClient, message.getBody(), targetFile);
}

// Remember to delete the message, or it will be redelivered.
sqsClient.deleteMessage(queueUrl, message.getReceiptHandle());
```

## Recommended configuration of S3 and SQS
It's recommended (although not required) to specify an S3 bucket policy that requires all S3 objects to use server-side encryption.

Further, it's recommended to specify a lifecycle rule for the S3 bucket that permanently deletes all objects after 14 days.

SQS queues are recommended to be configured with a default "Receive Message Wait Time" of 20 seconds, and must have a "Maximum Message Size" of 256 KB. The "Message Retention Period" must not be longer than the S3 lifecycle period (normally 14 days).

## Triggering AWS Lambda when a message is sent
Often you would like an AWS Lambda to be triggered when an SQS message is available on a queue. Unfortunately, Amazon doesn't support this directly, but this library offers a work-around by (optionally) publishing an SNS event whenever a message is sent. The SNS event can then trigger the execution of a Lambda.

Use {@link no.cantara.aws.sqs.AmazonSQSSecureClient#withSnsNotificationsEnabled(String, boolean)} to turn on SNS notifications.

Remember to create a subscription in the SNS Console. Select protocol "AWS Lambda" and select the Lambda you want to be triggered.


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
