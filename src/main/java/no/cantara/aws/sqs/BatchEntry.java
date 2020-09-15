package no.cantara.aws.sqs;

import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;

public class BatchEntry {
    private final String encryptedPayload;
    private final SendMessageBatchRequestEntry batchRequestEntry;

    public BatchEntry(String encryptedPayload, SendMessageBatchRequestEntry batchRequestEntry) {
        this.encryptedPayload = encryptedPayload;
        this.batchRequestEntry = batchRequestEntry;
    }

    public String getEncryptedPayload() {
        return encryptedPayload;
    }

    public SendMessageBatchRequestEntry getBatchRequestEntry() {
        return batchRequestEntry;
    }
}
