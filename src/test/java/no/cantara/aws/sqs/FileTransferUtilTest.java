package no.cantara.aws.sqs;

import org.junit.Test;

import static no.cantara.aws.sqs.FileTransferUtil.marshalFileEvent;
import static no.cantara.aws.sqs.FileTransferUtil.unmarshalFileEvent;
import static org.junit.Assert.assertEquals;

public class FileTransferUtilTest {
    @Test
    public void testMarshalFileEvent() throws Exception {
        FileTransferEvent event = new FileTransferEvent();
        event.setFilename("file.txt");
        event.setSize(666);
        event.setS3Object("myObject");
        event.getAttributes().put("foo", "bar");

        String json = marshalFileEvent(event);

        String expected = "{\"size\":666,\"filename\":\"file.txt\",\"mimeType\":null,\"s3Object\":\"myObject\",\"attributes\":{\"foo\":\"bar\"}}";
        assertEquals(json, expected);

        FileTransferEvent unmarshalled = unmarshalFileEvent(json);
        assertEquals(unmarshalled, event);
    }
}