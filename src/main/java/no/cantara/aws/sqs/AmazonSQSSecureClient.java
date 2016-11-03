package no.cantara.aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
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
 * Extends {@link com.amazonaws.services.sqs.AmazonSQSClient} with support for encryption, compression and
 * arbitrary message size.
 * <p>
 * Please refer to the {@link no.cantara.aws.sqs package documentation} for more details and code examples.
 */
public final class AmazonSQSSecureClient extends AmazonSQSClient {
    private static final String AWS_SQS_LARGE_PAYLOAD = "AWS_SQS_LARGE_PAYLOAD";
    private static final String REGION = "region";
    private static final String BUCKET = "bucket";
    private static final String IDENTIFIER = "identifier";

    private static final int MAX_MESSAGE_SIZE = (1024 * 64 * 4) - (1024 * 16);

    private final String AWS_KMS_CMK_ID;
    private final Region AWS_REGION;
    final String AWS_S3_BUCKET;
    final AmazonS3 AWS_S3_CLIENT;
    private final AmazonSNS AWS_SNS_CLIENT;
    private final Set<String> queueNamesWithSnsNotification = new LinkedHashSet<String>();

    private final class AmazonS3WithServerSideEncryption extends AmazonS3Client {
        AmazonS3WithServerSideEncryption(final AWSCredentialsProvider awsCredentialsProvider, final Region awsRegion) {
            super(awsCredentialsProvider);
            super.withRegion(awsRegion);
        }

        AmazonS3WithServerSideEncryption(final Region awsRegion) {
            super(new DefaultAWSCredentialsProviderChain());
            super.withRegion(awsRegion);
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

    public AmazonSQSSecureClient(final AWSCredentialsProvider awsCredentialsProvider, final Region awsRegion, final String kmsCmkId, final String s3Bucket) {
        super(awsCredentialsProvider);
        super.setRegion(awsRegion);

        this.AWS_KMS_CMK_ID = kmsCmkId;
        this.AWS_REGION = awsRegion;
        this.AWS_S3_BUCKET = s3Bucket;
        this.AWS_S3_CLIENT = new AmazonS3WithServerSideEncryption(awsCredentialsProvider, awsRegion);
        this.AWS_SNS_CLIENT = new AmazonSNSClient();
        this.AWS_SNS_CLIENT.setRegion(awsRegion);
    }

    public AmazonSQSSecureClient(final Region awsRegion, final String kmsCmkId, final String s3Bucket) {
        super(new DefaultAWSCredentialsProviderChain());
        super.setRegion(awsRegion);

        this.AWS_KMS_CMK_ID = kmsCmkId;
        this.AWS_REGION = awsRegion;
        this.AWS_S3_BUCKET = s3Bucket;
        this.AWS_S3_CLIENT = new AmazonS3WithServerSideEncryption(awsRegion);
        this.AWS_SNS_CLIENT = new AmazonSNSClient();
        this.AWS_SNS_CLIENT.setRegion(awsRegion);
    }

    @Override
    public final ReceiveMessageResult receiveMessage(ReceiveMessageRequest receiveMessageRequest) {
        receiveMessageRequest.getMessageAttributeNames().add(AWS_SQS_LARGE_PAYLOAD);

        ReceiveMessageResult receiveMessageResult = super.receiveMessage(receiveMessageRequest);
        for (Message message : receiveMessageResult.getMessages()) {
            inflate(message);
        }

        receiveMessageRequest.getMessageAttributeNames().remove(AWS_SQS_LARGE_PAYLOAD);

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
        throw new NoSuchMethodError("TBI");
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
            if (!entry.getKey().equals(AWS_SQS_LARGE_PAYLOAD)) {
                messageAttributes.put(entry.getKey(), entry.getValue());
            }
        }
        message.clearMessageAttributesEntries();
        message.setMessageAttributes(messageAttributes);
    }

    private SendMessageRequest deflate(SendMessageRequest sendMessageRequest) {
        String payload = KMSCryptoUtil.encrypt(AWS_REGION, AWS_KMS_CMK_ID, sendMessageRequest.getMessageBody());
        Map<Object, Object> map = new HashMap<Object, Object>();

        map.put(REGION, AWS_REGION.getName());

        if (payload.length() >= MAX_MESSAGE_SIZE) {
            String uuid = UUID.randomUUID().toString();

            map.put(BUCKET, AWS_S3_BUCKET);
            map.put(IDENTIFIER, uuid);

            sendMessageRequest.addMessageAttributesEntry(AWS_SQS_LARGE_PAYLOAD, new MessageAttributeValue().withDataType("String").withStringValue(
                    JsonUtil.from(map)));
            sendMessageRequest.setMessageBody("{}");

            write(AWS_S3_BUCKET, uuid, payload, sendMessageRequest.getQueueUrl());
        } else {
            sendMessageRequest.addMessageAttributesEntry(AWS_SQS_LARGE_PAYLOAD, new MessageAttributeValue().withDataType("String").withStringValue(JsonUtil.from(map)));
            sendMessageRequest.setMessageBody(payload);
        }

        return sendMessageRequest;
    }

    private void inflate(Message message) {
        if (message.getMessageAttributes().get(AWS_SQS_LARGE_PAYLOAD) != null) {
            Map<Object, Object> map = JsonUtil.toMap(message.getMessageAttributes().get(AWS_SQS_LARGE_PAYLOAD).getStringValue());

            if (map.containsKey(IDENTIFIER)) {
                message.setBody(KMSCryptoUtil
                        .decrypt(Region.getRegion(Regions.fromName(map.get(REGION).toString())), read(map.get(BUCKET).toString(), map.get(IDENTIFIER).toString())));
            } else {
                message.setBody(KMSCryptoUtil.decrypt(Region.getRegion(Regions.fromName(map.get(REGION).toString())), message.getBody()));
            }

            cleanMessageAttributes(message);
        } else {
            message.setBody(KMSCryptoUtil.decrypt(AWS_REGION, message.getBody()));
        }
    }

    private String read(final String bucket, final String key) {
        try {
            return new String(IOUtils.toByteArray(AWS_S3_CLIENT.getObject(bucket, key).getObjectContent()), KMSCryptoUtil.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void write(final String bucket, final String key, final String payload, final String queue) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] buffer = payload.getBytes(KMSCryptoUtil.DEFAULT_CHARSET);
        Map<String, String> map = new HashMap<String, String>();

        map.put("sqs-queue", queue);
        objectMetadata.setUserMetadata(map);
        objectMetadata.setContentLength(buffer.length);

        AWS_S3_CLIENT.putObject(bucket, key, new ByteArrayInputStream(buffer), objectMetadata);
    }
}
