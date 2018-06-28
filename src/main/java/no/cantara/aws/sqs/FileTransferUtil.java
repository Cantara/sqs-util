package no.cantara.aws.sqs;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.json.Jackson;

import java.io.File;

/**
 * Utilities for transferring files with Amazon SQS/S3.
 * <p>
 * Please refer to the {@link no.cantara.aws.sqs package documentation} for code examples.
 */
public class FileTransferUtil {
    /**
     * Sends the file event on SQS and uploads the given file to S3.
     *
     * @param sqsClient The SQS client.
     * @param request   The SQS send request. Only the {@link SendMessageRequest#queueUrl} is required.
     * @param event     File meta data that will be sent as a JSON message on SQS.
     * @param file      The content of this file will be uploaded to S3.
     */
    public static void sendFile(AmazonSQSSecureClient sqsClient, SendMessageRequest request, FileTransferEvent event, File file) {
        sqsClient.AWS_S3_CLIENT.putObject(sqsClient.AWS_S3_BUCKET, event.getS3Object(), file);
        request.setMessageBody(marshalFileEvent(event));
        sqsClient.sendMessage(request);
    }

    /**
     * Sends the file event on SQS and uploads the given file to S3.
     *
     * @param sqsClient The SQS client.
     * @param request   The SQS send request. Only the {@link SendMessageRequest#queueUrl} is required.
     * @param event     File meta data that will be sent as a JSON message on SQS.
     * @param file      The content of this file will be uploaded to S3.
     */
    public static void sendFile(AmazonSQSSecureClientV2 sqsClient, SendMessageRequest request, FileTransferEvent event, File file) {
        sqsClient.AWS_S3_CLIENT.putObject(sqsClient.AWS_S3_BUCKET, event.getS3Object(), file);
        request.setMessageBody(marshalFileEvent(event));
        sqsClient.sendMessage(request);
    }

    /**
     * Downloads a file from S3, based on the file event in the body.
     *
     * @param sqsClient The SQS client.
     * @param body      The SQS message body, as obtained by {@link AmazonSQSSecureClient#receiveMessage(String)}.
     * @param target    Save the downloaded content to this file. The file doesn't have to exist. If it
     *                  exists, it will be overwritten.
     * @return Meta data for the file.
     */
    public static FileTransferEvent receiveFile(AmazonSQSSecureClient sqsClient, String body, File target) {
        FileTransferEvent event = unmarshalFileEvent(body);
        sqsClient.AWS_S3_CLIENT.getObject(new GetObjectRequest(sqsClient.AWS_S3_BUCKET, event.getS3Object()), target);
        return event;
    }

    /**
     * Downloads a file from S3, based on the file event in the body.
     *
     * @param sqsClient The SQS client.
     * @param body      The SQS message body, as obtained by {@link AmazonSQSSecureClient#receiveMessage(String)}.
     * @param target    Save the downloaded content to this file. The file doesn't have to exist. If it
     *                  exists, it will be overwritten.
     * @return Meta data for the file.
     */
    public static FileTransferEvent receiveFile(AmazonSQSSecureClientV2 sqsClient, String body, File target) {
        FileTransferEvent event = unmarshalFileEvent(body);
        sqsClient.AWS_S3_CLIENT.getObject(new GetObjectRequest(sqsClient.AWS_S3_BUCKET, event.getS3Object()), target);
        return event;
    }

    static String marshalFileEvent(FileTransferEvent event) {
        return Jackson.toJsonString(event);
    }

    static FileTransferEvent unmarshalFileEvent(String body) {
        return Jackson.fromJsonString(body, FileTransferEvent.class);
    }
}
