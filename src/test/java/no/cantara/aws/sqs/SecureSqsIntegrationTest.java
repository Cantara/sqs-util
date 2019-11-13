package no.cantara.aws.sqs;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static no.cantara.aws.sqs.KmsCryptoClient.DEFAULT_CMK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(value = IntegrationTest.class)
public class SecureSqsIntegrationTest {

    private static final String QUEUE_NAME = "cantara-aws-util-sqs-unittest";
    private static final String S3_BUCKET = "jms.lab.cantara.no";
    private static final Regions REGION = Regions.US_EAST_1;
    private static AmazonSQSSecureClient sqsClient;
    private static final Logger log = LoggerFactory.getLogger(SecureSqsIntegrationTest.class);

    @BeforeClass
    public static void createClientAndQueue() {
        sqsClient = AmazonSQSSecureClient.create(REGION, DEFAULT_CMK, S3_BUCKET);
        sqsClient.createQueue(new CreateQueueRequest(QUEUE_NAME)
                                      .addAttributesEntry("MessageRetentionPeriod", "60")          // Minimal time for retaining messages
                                      .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20")); // Use long-polling by default
    }

    @Before
    public void clearQueue() {
        GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(QUEUE_NAME);
        List<Message> messages = sqsClient.receiveMessage(new ReceiveMessageRequest(queueUrl.getQueueUrl())
                                                                  .withMaxNumberOfMessages(10).withWaitTimeSeconds(5)).getMessages();
        if (!messages.isEmpty()) {
            log.info("Draining {} message(s) from queue {}", messages.size(), QUEUE_NAME);
            for (Message message : messages) {
                sqsClient.deleteMessage(queueUrl.getQueueUrl(), message.getReceiptHandle());
            }
        }
    }

    @Test
    public void testSQSMessage() {
        GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(QUEUE_NAME);
        String smallMessage = "{ title: test, body: some-test-data }";

        log.debug("Sending small message with payload size {}kB to queue={}", smallMessage.length() / (double) 1000, queueUrl);
        sqsClient.sendMessage(queueUrl.getQueueUrl(), smallMessage);
        Message message = sqsClient.receiveMessage(queueUrl.getQueueUrl()).getMessages().get(0);
        String messageContent = message.getBody();

        assertEquals(messageContent, smallMessage);

        log.debug("Deleting test message from queue {}", queueUrl);
        sqsClient.deleteMessage(queueUrl.getQueueUrl(), message.getReceiptHandle());
    }

    @Test
    public void testSQSMessageWithEmptyBody() {
        GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(QUEUE_NAME);
        String emptyBody = "";

        log.debug("Sending message with empty payload to queue={}", queueUrl);
        sqsClient.sendMessage(queueUrl.getQueueUrl(), emptyBody);
        Message message = sqsClient.receiveMessage(queueUrl.getQueueUrl()).getMessages().get(0);
        String messageContent = message.getBody();

        assertEquals(messageContent, emptyBody);

        log.debug("Deleting test message from queue {}", queueUrl);
        sqsClient.deleteMessage(queueUrl.getQueueUrl(), message.getReceiptHandle());
    }

    @Test
    public void testSQSMessageWithSNSNotification() {
        sqsClient.withSnsNotificationsEnabled(QUEUE_NAME, true);
        GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(QUEUE_NAME);
        String smallMessage = "{ title: test, body: some-test-data }";

        log.debug("Sending small message with payload size {}kB to queue={}", smallMessage.length() / (double) 1000, queueUrl);
        sqsClient.sendMessage(queueUrl.getQueueUrl(), smallMessage);
        Message message = sqsClient.receiveMessage(queueUrl.getQueueUrl()).getMessages().get(0);
        String messageContent = message.getBody();

        Assert.assertEquals(messageContent, smallMessage);

        log.debug("Deleting test message from queue {}", queueUrl);
        sqsClient.deleteMessage(queueUrl.getQueueUrl(), message.getReceiptHandle());
    }

    @Test
    public void testSQSAndS3Message() {
        GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(QUEUE_NAME);
        String largeMessage = createDataSize(250000);

        log.debug("Sending large message with payload size {}kB to queue={}", largeMessage.length() / (double) 1000, queueUrl);
        sqsClient.sendMessage(queueUrl.getQueueUrl(), largeMessage);
        Message message = sqsClient.receiveMessage(queueUrl.getQueueUrl()).getMessages().get(0);
        String messageContent = message.getBody();

        Assert.assertEquals(messageContent.length(), largeMessage.length());

        log.debug("Deleting test message from queue {}", queueUrl);
        sqsClient.deleteMessage(QUEUE_NAME, message.getReceiptHandle());
    }

    @Test(expected = QueueDoesNotExistException.class)
    public void testNonExistantQueue() {
        String nonExistantQueueUrl = "https://sqs.eu-west-1.amazonaws.com/944493252375/nonexistantqueue";
        sqsClient.sendMessage(nonExistantQueueUrl, "{}");
    }


    @Test(expected = AmazonS3Exception.class)
    // The specified bucket does not exist
    public void testNonExistantBucket() {
        AmazonSQS client = AmazonSQSSecureClient.create(REGION, DEFAULT_CMK, "non-existant-bucket");
        client.createQueue(QUEUE_NAME);
        String queueUrl = client.getQueueUrl(QUEUE_NAME).getQueueUrl();
        String largeMessage = createDataSize(250000);

        log.info("Sending large message ({}kB) with non-existant S3 bucket specified.", largeMessage.length() / (double) 1000);
        client.sendMessage(queueUrl, largeMessage);
    }

    @Test
    public void testSQSAndS3WithCustomCredentialsProvider() {
        log.info("Using custom credentials provider");

        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        AWSCredentialsProvider credentialsProvider = createBasicCredentialsProvider(credentials.getAWSAccessKeyId(),
                                                                                    credentials.getAWSSecretKey());
        AmazonSQS client = AmazonSQSSecureClient.create(credentialsProvider,
                                                        REGION, DEFAULT_CMK, S3_BUCKET);
        GetQueueUrlResult queueUrl = client.getQueueUrl(QUEUE_NAME);
        String largeMessage = createDataSize(250000);

        log.info("Sending large message with payload size {}kB to queue={}", largeMessage.length() / (double) 1000, queueUrl);
        client.sendMessage(queueUrl.getQueueUrl(), largeMessage);
        Message message = client.receiveMessage(queueUrl.getQueueUrl()).getMessages().get(0);
        String messageContent = message.getBody();

        Assert.assertEquals(messageContent.length(), largeMessage.length());

        log.info("Deleting test message from queue {}", queueUrl);
        sqsClient.deleteMessage(QUEUE_NAME, message.getReceiptHandle());
    }

    @Test(expected = AmazonServiceException.class)
    public void testInvalidCredentials() {
        AWSCredentialsProvider credentialsProvider = createBasicCredentialsProvider("invalid", "invalid");
        log.info("Creating SQS/S3 client and queue with invalid credentials");
        AmazonSQS client = AmazonSQSSecureClient.create(credentialsProvider,
                                                        REGION, DEFAULT_CMK, S3_BUCKET);
        client.createQueue(QUEUE_NAME);
    }

    @Test(expected = NotFoundException.class)
    public void testInvalidCmk() {
        new KmsCryptoClient().encrypt("invalid_id", "{}");
    }

    @Test
    public void testFileTransfer() throws IOException {
        GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(QUEUE_NAME);

        String fileContent = createDataSize(3000);
        File sourceFile = File.createTempFile("source", ".txt");
        FileWriter fileWriter = new FileWriter(sourceFile);
        fileWriter.write(fileContent);
        fileWriter.close();

        SendMessageRequest request = new SendMessageRequest().withQueueUrl(queueUrl.getQueueUrl());
        FileTransferEvent sentEvent = new FileTransferEvent();
        sentEvent.setFilename("file.txt");
        sentEvent.setSize(sourceFile.length());
        sentEvent.setS3Object("myObject");
        sentEvent.getAttributes().put("foo", "bar");
        sentEvent.getAttributes().put("hei", "hoi");

        log.info("Sending file {}", sourceFile);
        FileTransferUtil.sendFile(sqsClient, request, sentEvent, sourceFile);

        File targetFile = File.createTempFile("target", ".txt");


        log.info("Receiving file {}", targetFile);
        Message message = sqsClient.receiveMessage(queueUrl.getQueueUrl()).getMessages().get(0);
        FileTransferEvent receivedEvent = FileTransferUtil.receiveFile(sqsClient, message.getBody(), targetFile);

        assertTrue(Arrays.equals(IOUtils.toByteArray(new FileInputStream(sourceFile)),
                                 IOUtils.toByteArray(new FileInputStream(targetFile))));

        assertEquals(receivedEvent, sentEvent);

        log.debug("Deleting test message from queue {}", queueUrl);
        sqsClient.deleteMessage(QUEUE_NAME, message.getReceiptHandle());
        targetFile.delete();
        sourceFile.delete();
    }

    private static String createDataSize(int msgSize) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(msgSize);
        for (int i = 0; i < msgSize; i++) {
            sb.append(random.nextInt(500));
        }
        return sb.toString();
    }

    private static AWSCredentialsProvider createBasicCredentialsProvider(final String accessToken, final String secretToken) {
        return new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials(accessToken, secretToken);
            }

            @Override
            public void refresh() {

            }
        };
    }

}
