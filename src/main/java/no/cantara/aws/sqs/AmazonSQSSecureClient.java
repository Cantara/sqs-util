package no.cantara.aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Wraps {@link com.amazonaws.services.sqs.AmazonSQSClient} with support for encryption, compression and
 * arbitrary message size.
 * <p>
 * Please refer to the {@link no.cantara.aws.sqs package documentation} for more details and code examples.
 */
public class AmazonSQSSecureClient extends AmazonSQSClientBase {
    private static final String LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY = "AWS_SQS_LARGE_PAYLOAD";
    private static final String REGION_KEY = "region";
    private static final String S3_BUCKET_KEY = "bucket";
    private static final String IDENTIFIER_KEY = "identifier";

    private static final int MAX_MESSAGE_SIZE = (1024 * 64 * 4) - (1024 * 16);

    private final String AWS_KMS_CMK_ID;
    private final Regions AWS_REGION;
    final String AWS_S3_BUCKET;
    final AmazonS3 AWS_S3_CLIENT;
    private final AmazonSNS AWS_SNS_CLIENT;
    private final KmsCryptoClient AWS_KMS_CRYPTO_CLIENT;
    private final Set<String> queueNamesWithSnsNotification = new LinkedHashSet<String>();

    private static final class AmazonS3WithServerSideEncryption extends AmazonS3ClientBase {
        AmazonS3WithServerSideEncryption(final AmazonS3 delegate) {
            super(delegate);
        }

        static AmazonS3WithServerSideEncryption create(final AWSCredentialsProvider awsCredentialsProvider,
                                                       final Regions awsRegion) {
            final AmazonS3 amazonS3 = AmazonS3Client.builder()
                    .withCredentials(awsCredentialsProvider)
                    .withRegion(awsRegion)
                    .build();

            return new AmazonS3WithServerSideEncryption(amazonS3);
        }

        @Override
        public final PutObjectResult putObject(PutObjectRequest putObjectRequest) {
            return super.putObject(putObjectRequest.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams()));
        }

        @Override
        public final PutObjectResult putObject(String bucketName, String key, File file) {
            return putObject(new PutObjectRequest(bucketName, key, file));
        }

        @Override
        public final PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
            return putObject(new PutObjectRequest(bucketName, key, input, metadata));
        }
    }

    private AmazonSQSSecureClient(final AmazonSQS delegate,
                                  final Regions awsRegion,
                                  final String kmsCmkId,
                                  final String s3Bucket,
                                  final AmazonS3 awsS3Client,
                                  final AmazonSNS awsSnsClient,
                                  final KmsCryptoClient awsKmsCryptoClient) {
        super(delegate);

        this.AWS_KMS_CMK_ID = kmsCmkId;
        this.AWS_REGION = awsRegion;
        this.AWS_S3_BUCKET = s3Bucket;
        this.AWS_S3_CLIENT = awsS3Client;
        this.AWS_SNS_CLIENT = awsSnsClient;
        this.AWS_KMS_CRYPTO_CLIENT = awsKmsCryptoClient;
    }


    public static AmazonSQSSecureClient create(final AWSCredentialsProvider awsCredentialsProvider,
                                               final Regions awsRegion,
                                               final String kmsCmkId,
                                               final String s3Bucket) {
        return amazonSqsSecureClient(awsRegion, kmsCmkId, s3Bucket, awsCredentialsProvider);
    }

    public static AmazonSQSSecureClient create(final Regions awsRegion,
                                               final String kmsCmkId,
                                               final String s3Bucket) {
        final AWSCredentialsProvider awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();
        return amazonSqsSecureClient(awsRegion, kmsCmkId, s3Bucket, awsCredentialsProvider);
    }

    public static AmazonSQSSecureClient create(final Regions awsRegion,
                                               final String kmsCmkId,
                                               final String s3Bucket,
                                               final AWSKMS awsKmsClient,
                                               final AmazonS3 awsS3Client,
                                               final AmazonSQS amazonSQS) {
        final AmazonS3WithServerSideEncryption awsSqsClient = new AmazonS3WithServerSideEncryption(awsS3Client);
        final AmazonSNS awsSnsClient = AmazonSNSClient.builder().withRegion(awsRegion).build();
        final KmsCryptoClient awsKmsCryptoClient = new KmsCryptoClient(awsKmsClient);

        return new AmazonSQSSecureClient(
                amazonSQS,
                awsRegion,
                kmsCmkId,
                s3Bucket,
                awsSqsClient,
                awsSnsClient,
                awsKmsCryptoClient
        );
    }

    private static AmazonSQSSecureClient amazonSqsSecureClient(
            final Regions awsRegion,
            final String kmsCmkId,
            final String s3Bucket,
            final AWSCredentialsProvider awsCredentialsProvider
    ) {
        final AmazonSQS awsSqsClient = awsSqsClient(awsCredentialsProvider, awsRegion);
        final AmazonS3WithServerSideEncryption awsS3Client = AmazonS3WithServerSideEncryption
                .create(awsCredentialsProvider, awsRegion);
        final AmazonSNS awsSnsClient = snsClient(awsCredentialsProvider, awsRegion);
        final KmsCryptoClient awsKmsCryptoClient = kmsCryptoClient(awsCredentialsProvider, awsRegion);

        return new AmazonSQSSecureClient(
                awsSqsClient,
                awsRegion,
                kmsCmkId,
                s3Bucket,
                awsS3Client,
                awsSnsClient,
                awsKmsCryptoClient
        );
    }

    private static AmazonSQS awsSqsClient(
            final AWSCredentialsProvider awsCredentialsProvider,
            final Regions awsRegion
    ) {
        return AmazonSQSClient.builder()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion)
                .build();
    }

    private static AmazonSNS snsClient(
            final AWSCredentialsProvider awsCredentialsProvider,
            final Regions awsRegion
    ) {
        return AmazonSNSClient.builder()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion)
                .build();
    }

    private static KmsCryptoClient kmsCryptoClient(
            final AWSCredentialsProvider awsCredentialsProvider,
            final Regions awsRegion
    ) {
        final AWSKMS awsKmsClient = AWSKMSClientBuilder.standard()
                .withRegion(awsRegion.toString())
                .withCredentials(awsCredentialsProvider)
                .build();
        return new KmsCryptoClient(awsKmsClient);
    }

    @Override
    public final ReceiveMessageResult receiveMessage(ReceiveMessageRequest receiveMessageRequest) {
        receiveMessageRequest.getMessageAttributeNames().add(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY);

        ReceiveMessageResult receiveMessageResult = super.receiveMessage(receiveMessageRequest);
        for (Message message : receiveMessageResult.getMessages()) {
            inflate(message);
        }

        receiveMessageRequest.getMessageAttributeNames().remove(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY);

        return receiveMessageResult;
    }

    @Override
    public final ReceiveMessageResult receiveMessage(String queueUrl) {
        return receiveMessage(new ReceiveMessageRequest(queueUrl));
    }

    @Override
    public final SendMessageResult sendMessage(SendMessageRequest sendMessageRequest) {
        SendMessageResult result = super.sendMessage(deflate(sendMessageRequest));

        String queueUrl = sendMessageRequest.getQueueUrl();
        String queueName = queueUrl.substring(queueUrl.lastIndexOf('/') + 1);

        if (queueNamesWithSnsNotification.contains(queueName)) {
            CreateTopicResult topic = AWS_SNS_CLIENT.createTopic(queueName);
            String topicArn = topic.getTopicArn();
            AWS_SNS_CLIENT.publish(topicArn, result.getMessageId());
        }
        return result;
    }

    @Override
    public final SendMessageResult sendMessage(String queueUrl, String messageBody) {
        return sendMessage(new SendMessageRequest(queueUrl, messageBody));
    }

    @Override
    public final SendMessageBatchResult sendMessageBatch(SendMessageBatchRequest sendMessageBatchRequest) {
        SendMessageBatchResult result = super.sendMessageBatch(sendMessageBatchRequest);

        String queueUrl = sendMessageBatchRequest.getQueueUrl();
        String queueName = queueUrl.substring(queueUrl.lastIndexOf('/') + 1);
        if(queueNamesWithSnsNotification.contains(queueName)) {
            CreateTopicResult topic = AWS_SNS_CLIENT.createTopic(queueName);
            String topicArn = topic.getTopicArn();
            for (SendMessageBatchResultEntry entry : result.getSuccessful()) {
                AWS_SNS_CLIENT.publish(topicArn, entry.getMessageId());
            }
        }

        return result;
    }

    @Override
    public final SendMessageBatchResult sendMessageBatch(String queueUrl, List<SendMessageBatchRequestEntry> entries) {
        return sendMessageBatch(new SendMessageBatchRequest(queueUrl, entries));
    }

    /**
     * If SNS notifications are enabled, an SNS event is automatically published when an SQS message
     * is sent to the given queue.
     * <p>
     * The SNS topic name used will be the same as the SQS queue name.
     *
     * @param queueName               The SQS queue name for which to enable/disable SNS events.
     * @param snsNotificationsEnabled Whether to publish SNS events.
     * @return Returns a reference to this object so that method calls can be chained together.
     */
    public AmazonSQSSecureClient withSnsNotificationsEnabled(String queueName, boolean snsNotificationsEnabled) {
        if (snsNotificationsEnabled) {
            queueNamesWithSnsNotification.add(queueName);
        } else {
            queueNamesWithSnsNotification.remove(queueName);
        }
        return this;
    }

    private void cleanMessageAttributes(Message message) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();

        for (Map.Entry<String, MessageAttributeValue> entry : message.getMessageAttributes().entrySet()) {
            if (!entry.getKey().equals(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY)) {
                messageAttributes.put(entry.getKey(), entry.getValue());
            }
        }
        message.clearMessageAttributesEntries();
        message.setMessageAttributes(messageAttributes);
    }

    private SendMessageRequest deflate(SendMessageRequest sendMessageRequest) {
        String payload = AWS_KMS_CRYPTO_CLIENT.encrypt(AWS_KMS_CMK_ID, sendMessageRequest.getMessageBody());

        if (payload.length() >= MAX_MESSAGE_SIZE) {
            String uuid = UUID.randomUUID().toString();

            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put(REGION_KEY, AWS_REGION.getName());
            map.put(S3_BUCKET_KEY, AWS_S3_BUCKET);
            map.put(IDENTIFIER_KEY, uuid);

            MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(JsonUtil.from(map));
            sendMessageRequest.addMessageAttributesEntry(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY, messageAttributeValue);
            sendMessageRequest.setMessageBody("{}");

            write(AWS_S3_BUCKET, uuid, payload, sendMessageRequest.getQueueUrl());
        } else {
            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put(REGION_KEY, AWS_REGION.getName());

            MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(JsonUtil.from(map));
            sendMessageRequest.addMessageAttributesEntry(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY, messageAttributeValue);
            sendMessageRequest.setMessageBody(payload);
        }

        return sendMessageRequest;
    }

    private void inflate(Message message) {
        if (message.getMessageAttributes().get(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY) != null) {
            String messageAttributeValue = message
                    .getMessageAttributes()
                    .get(LARGE_PAYLOAD_MESSAGE_ATTRIBUTE_KEY)
                    .getStringValue();
            Map<Object, Object> map = JsonUtil.toMap(messageAttributeValue);

            if (map.containsKey(IDENTIFIER_KEY)) {
                String payload = read(map.get(S3_BUCKET_KEY).toString(), map.get(IDENTIFIER_KEY).toString());
                String decryptedPayload = AWS_KMS_CRYPTO_CLIENT.decrypt(payload);
                message.setBody(decryptedPayload);
            } else {
                message.setBody(AWS_KMS_CRYPTO_CLIENT.decrypt(message.getBody()));
            }

            cleanMessageAttributes(message);
        } else {
            message.setBody(AWS_KMS_CRYPTO_CLIENT.decrypt(message.getBody()));
        }
    }

    private String read(final String bucket, final String key) {
        try {
            byte[] bytes = IOUtils.toByteArray(AWS_S3_CLIENT.getObject(bucket, key).getObjectContent());
            return new String(bytes, KmsCryptoClient.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void write(final String bucket, final String key, final String payload, final String queue) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] buffer = payload.getBytes(KmsCryptoClient.DEFAULT_CHARSET);
        Map<String, String> map = new HashMap<String, String>();

        map.put("sqs-queue", queue);
        objectMetadata.setUserMetadata(map);
        objectMetadata.setContentLength(buffer.length);

        AWS_S3_CLIENT.putObject(bucket, key, new ByteArrayInputStream(buffer), objectMetadata);
    }
}
