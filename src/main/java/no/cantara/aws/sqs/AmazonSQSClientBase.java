package no.cantara.aws.sqs;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.regions.Region;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;

import java.util.List;
import java.util.Map;

public class AmazonSQSClientBase implements AmazonSQS {
    private final AmazonSQS delegate;

    AmazonSQSClientBase(final AmazonSQS delegate) {
        this.delegate = delegate;
    }

    @Override
    @Deprecated
    public void setEndpoint(String endpoint) {
        delegate.setEndpoint(endpoint);
    }

    @Override
    @Deprecated
    public void setRegion(Region region) {
        delegate.setRegion(region);
    }

    @Override
    public AddPermissionResult addPermission(AddPermissionRequest addPermissionRequest) {
        return delegate.addPermission(addPermissionRequest);
    }

    @Override
    public AddPermissionResult addPermission(String queueUrl, String label, List<String> aWSAccountIds, List<String> actions) {
        return delegate.addPermission(queueUrl, label, aWSAccountIds, actions);
    }

    @Override
    public CancelMessageMoveTaskResult cancelMessageMoveTask(CancelMessageMoveTaskRequest cancelMessageMoveTaskRequest) {
        return delegate.cancelMessageMoveTask(cancelMessageMoveTaskRequest);
    }

    @Override
    public ChangeMessageVisibilityResult changeMessageVisibility(ChangeMessageVisibilityRequest changeMessageVisibilityRequest) {
        return delegate.changeMessageVisibility(changeMessageVisibilityRequest);
    }

    @Override
    public ChangeMessageVisibilityResult changeMessageVisibility(String queueUrl, String receiptHandle, Integer visibilityTimeout) {
        return delegate.changeMessageVisibility(queueUrl, receiptHandle, visibilityTimeout);
    }

    @Override
    public ChangeMessageVisibilityBatchResult changeMessageVisibilityBatch(ChangeMessageVisibilityBatchRequest changeMessageVisibilityBatchRequest) {
        return delegate.changeMessageVisibilityBatch(changeMessageVisibilityBatchRequest);
    }

    @Override
    public ChangeMessageVisibilityBatchResult changeMessageVisibilityBatch(String queueUrl, List<ChangeMessageVisibilityBatchRequestEntry> entries) {
        return delegate.changeMessageVisibilityBatch(queueUrl, entries);
    }

    @Override
    public CreateQueueResult createQueue(CreateQueueRequest createQueueRequest) {
        return delegate.createQueue(createQueueRequest);
    }

    @Override
    public CreateQueueResult createQueue(String queueName) {
        return delegate.createQueue(queueName);
    }

    @Override
    public DeleteMessageResult deleteMessage(DeleteMessageRequest deleteMessageRequest) {
        return delegate.deleteMessage(deleteMessageRequest);
    }

    @Override
    public DeleteMessageResult deleteMessage(String queueUrl, String receiptHandle) {
        return delegate.deleteMessage(queueUrl, receiptHandle);
    }

    @Override
    public DeleteMessageBatchResult deleteMessageBatch(DeleteMessageBatchRequest deleteMessageBatchRequest) {
        return delegate.deleteMessageBatch(deleteMessageBatchRequest);
    }

    @Override
    public DeleteMessageBatchResult deleteMessageBatch(String queueUrl, List<DeleteMessageBatchRequestEntry> entries) {
        return delegate.deleteMessageBatch(queueUrl, entries);
    }

    @Override
    public DeleteQueueResult deleteQueue(DeleteQueueRequest deleteQueueRequest) {
        return delegate.deleteQueue(deleteQueueRequest);
    }

    @Override
    public DeleteQueueResult deleteQueue(String queueUrl) {
        return delegate.deleteQueue(queueUrl);
    }

    @Override
    public GetQueueAttributesResult getQueueAttributes(GetQueueAttributesRequest getQueueAttributesRequest) {
        return delegate.getQueueAttributes(getQueueAttributesRequest);
    }

    @Override
    public GetQueueAttributesResult getQueueAttributes(String queueUrl, List<String> attributeNames) {
        return delegate.getQueueAttributes(queueUrl, attributeNames);
    }

    @Override
    public GetQueueUrlResult getQueueUrl(GetQueueUrlRequest getQueueUrlRequest) {
        return delegate.getQueueUrl(getQueueUrlRequest);
    }

    @Override
    public GetQueueUrlResult getQueueUrl(String queueName) {
        return delegate.getQueueUrl(queueName);
    }

    @Override
    public ListDeadLetterSourceQueuesResult listDeadLetterSourceQueues(ListDeadLetterSourceQueuesRequest listDeadLetterSourceQueuesRequest) {
        return delegate.listDeadLetterSourceQueues(listDeadLetterSourceQueuesRequest);
    }

    @Override
    public ListMessageMoveTasksResult listMessageMoveTasks(ListMessageMoveTasksRequest listMessageMoveTasksRequest) {
        return delegate.listMessageMoveTasks(listMessageMoveTasksRequest);
    }

    @Override
    public ListQueueTagsResult listQueueTags(ListQueueTagsRequest listQueueTagsRequest) {
        return delegate.listQueueTags(listQueueTagsRequest);
    }

    @Override
    public ListQueueTagsResult listQueueTags(String queueUrl) {
        return delegate.listQueueTags(queueUrl);
    }

    @Override
    public ListQueuesResult listQueues(ListQueuesRequest listQueuesRequest) {
        return delegate.listQueues(listQueuesRequest);
    }

    @Override
    public ListQueuesResult listQueues() {
        return delegate.listQueues();
    }

    @Override
    public ListQueuesResult listQueues(String queueNamePrefix) {
        return delegate.listQueues(queueNamePrefix);
    }

    @Override
    public PurgeQueueResult purgeQueue(PurgeQueueRequest purgeQueueRequest) {
        return delegate.purgeQueue(purgeQueueRequest);
    }

    @Override
    public ReceiveMessageResult receiveMessage(ReceiveMessageRequest receiveMessageRequest) {
        return delegate.receiveMessage(receiveMessageRequest);
    }

    @Override
    public ReceiveMessageResult receiveMessage(String queueUrl) {
        return delegate.receiveMessage(queueUrl);
    }

    @Override
    public RemovePermissionResult removePermission(RemovePermissionRequest removePermissionRequest) {
        return delegate.removePermission(removePermissionRequest);
    }

    @Override
    public RemovePermissionResult removePermission(String queueUrl, String label) {
        return delegate.removePermission(queueUrl, label);
    }

    @Override
    public SendMessageResult sendMessage(SendMessageRequest sendMessageRequest) {
        return delegate.sendMessage(sendMessageRequest);
    }

    @Override
    public SendMessageResult sendMessage(String queueUrl, String messageBody) {
        return delegate.sendMessage(queueUrl, messageBody);
    }

    @Override
    public SendMessageBatchResult sendMessageBatch(SendMessageBatchRequest sendMessageBatchRequest) {
        return delegate.sendMessageBatch(sendMessageBatchRequest);
    }

    @Override
    public SendMessageBatchResult sendMessageBatch(String queueUrl, List<SendMessageBatchRequestEntry> entries) {
        return delegate.sendMessageBatch(queueUrl, entries);
    }

    @Override
    public SetQueueAttributesResult setQueueAttributes(SetQueueAttributesRequest setQueueAttributesRequest) {
        return delegate.setQueueAttributes(setQueueAttributesRequest);
    }

    @Override
    public SetQueueAttributesResult setQueueAttributes(String queueUrl, Map<String, String> attributes) {
        return delegate.setQueueAttributes(queueUrl, attributes);
    }

    @Override
    public StartMessageMoveTaskResult startMessageMoveTask(StartMessageMoveTaskRequest startMessageMoveTaskRequest) {
        return delegate.startMessageMoveTask(startMessageMoveTaskRequest);
    }

    @Override
    public TagQueueResult tagQueue(TagQueueRequest tagQueueRequest) {
        return delegate.tagQueue(tagQueueRequest);
    }

    @Override
    public TagQueueResult tagQueue(String queueUrl, Map<String, String> tags) {
        return delegate.tagQueue(queueUrl, tags);
    }

    @Override
    public UntagQueueResult untagQueue(UntagQueueRequest untagQueueRequest) {
        return delegate.untagQueue(untagQueueRequest);
    }

    @Override
    public UntagQueueResult untagQueue(String queueUrl, List<String> tagKeys) {
        return delegate.untagQueue(queueUrl, tagKeys);
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return delegate.getCachedResponseMetadata(request);
    }
}
