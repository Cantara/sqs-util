/**
 * <h1>Amazon SQS utilities.</h1>
 * <p>
 * This library provides an extension of the standard {@link com.amazonaws.services.sqs.AmazonSQSClient},
 * adding the following features in a transparent way:
 * <ul>
 * <li>Strong client-side encryption.</li>
 * <li>Compression of messages.</li>
 * <li>Supports arbitrary large messages. (Messages larger than 240 KB after compression and encryption will use S3).</li>
 * <li>Supports automatic publishing of SNS events when SQS messages are sent.</li>
 * </ul>
 * For more details, please refer to the Amazon SQS documention:
 * <ul>
 * <li><a href="https://aws.amazon.com/sqs/">Amazon Simple Queue Service (SQS)</a></li>
 * <li><a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/services/sqs/">Amazon SQS JavaDoc</a></li>
 * </ul>
 * <h1>Requirements</h1>
 * Java 6 or later is required. Further, since strong encryption is used, <em>Java Cryptography Extension (JCE) Unlimited
 * Strength Jurisdiction Policy</em> must be installed (available from Oracle).
 * <h2>Code example - Sending a message</h2>
 * <pre>
 *    AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...);
 *    String queueName = ...;
 *    String payload = ...;
 *
 *    String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
 *
 *    // Add a message attribute "foo".
 *    MessageAttributeValue someAttribute = new MessageAttributeValue().withDataType("String").withStringValue("bar");
 *
 *    SendMessageRequest messageRequest = new SendMessageRequest()
 *                               .withQueueUrl(queueUrl)
 *                               .withMessageBody(payload)
 *                               .addMessageAttributesEntry("foo", someAttribute);
 *
 *    String messageId = sqsClient.sendMessage(messageRequest).getMessageId();
 * </pre>
 * <h2>Code example - Receiving messages</h2>
 * <pre>
 *    AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...);
 *    String queueName = ...;
 *
 *    String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
 *    ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest(queueUrl))
 *                             .withMaxNumberOfMessages(10)
 *                             .withWaitTimeSeconds(20)            // Enables long-polling
 *                             .withMessageAttributeNames("All");  // Enables the reception of all message attributes
 *
 *    List&lt;Message&gt; messages = sqsClient.receiveMessage(receiveRequest).getMessages();
 *    for (Message message : messages) {
 *        process(message);
 *
 *       // Remember to delete the message, or it will be redelivered.
 *        sqsClient.deleteMessage(queueUrl, message.getReceiptHandle());
 *     }
 * </pre>
 * <h1>File Transfers</h1>
 * This library also offers an API for transferring files with meta data using a combination of SQS and S3.
 * <p>
 * A file event is sent (as a JSON message) over SQS. It contains meta data for the file as well as a reference
 * to an S3 object. Upon reception of the file event message, the API is used to download the file from S3 to a local file.
 * <p>
 * <img src="doc-files/file-transfer.png" alt="">
 * <h2>Code example - Sending a file</h2>
 * <pre>
 *    AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...);
 *    File sourceFile = ...;
 *    String queueName = ...;
 *
 *    String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
 *    SendMessageRequest request = new SendMessageRequest()
 *              .withQueueUrl(queueUrl);
 *              .addMessageAttributesEntry("SoriaMessageType",
 *                                         new MessageAttributeValue().withDataType("String").withStringValue("WorkOrderAttachment"));
 *
 *    FileTransferEvent sentEvent = new FileTransferEvent();
 *    sentEvent.setFilename("photo.jpeg");
 *    sentEvent.setSize(sourceFile.length());
 *    sentEvent.setS3Object(UUID.randomUUID().toString());
 *
 *    // Example user-defined properties.
 *    sentEvent.getAttributes().put("meterPointId", "123456");
 *    sentEvent.getAttributes().put("workOrderId", "789");
 *
 *    FileTransferUtil.sendFile(sqsClient, request, sentEvent, sourceFile);
 * </pre>
 * <h2>Code example - Receiving a file</h2>
 * <pre>
 *    AmazonSQSSecureClient sqsClient = AmazonSQSSecureClient.create(...);
 *    String queueName = ...;
 *    String queueUrl = sqsClient.getQueueUrl(queueName).getQueueUrl();
 *
 *    // Receive (zero or more) SQS messages.
 *    Message message = sqsClient.receiveMessage(queueUrl).getMessages().get(0);
 *
 *    // Check message type and download file if appropriate.
 *    String messageType = message.getMessageAttributes().get("SoriaMessageType").getStringValue();
 *    if ("WorkOrderAttachment".equals(messageType)) {
 *        File targetFile = File.createTempFile("target", ".tmp");
 *        FileTransferEvent receivedEvent = FileTransferUtil.receiveFile(sqsClient, message.getBody(), targetFile);
 *    }
 *
 *    // Remember to delete the message, or it will be redelivered.
 *    sqsClient.deleteMessage(queueUrl, message.getReceiptHandle());
 * </pre>
 * <h1>Recommended configuration of S3 and SQS</h1>
 * It's recommended (although not required) to specify an S3 bucket policy that requires all S3 objects to use
 * server-side encryption.
 * <p>
 * Further, it's recommended to specify a lifecycle rule for the S3 bucket that permanently deletes all objects
 * after 14 days.
 * <p>
 * SQS queues are recommended to be configured with a default "Receive Message Wait Time" of 20 seconds, and must have a
 * "Maximum Message Size" of 256 KB. The "Message Retention Period" must not be longer than the S3 lifecycle period (normally 14 days).
 * <h1>Triggering AWS Lambda when a message is sent</h1>
 * Often you would like an AWS Lambda to be triggered when an SQS message is available on a queue.
 * Unfortunately, Amazon doesn't support this directly, but this library offers a work-around by (optionally) publishing
 * an SNS event whenever a message is sent. The SNS event can then trigger the execution of a Lambda.
 * <p>
 * Use {@link no.cantara.aws.sqs.AmazonSQSSecureClient#withSnsNotificationsEnabled(String, boolean)} to turn on SNS notifications.
 * <p>
 * Remember to create a subscription in the SNS Console. Select protocol "AWS Lambda" and select the Lambda you want
 * to be triggered.
 */
package no.cantara.aws.sqs;
