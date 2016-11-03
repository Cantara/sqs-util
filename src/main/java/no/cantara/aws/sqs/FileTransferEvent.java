package no.cantara.aws.sqs;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Meta data for a file stored in S3.
 *
 * <ul>
 * <li>size - The file size in bytes.</li>
 * <li>filename - The original file name.</li>
 * <li>mimeType - The MIME type, may be null.</li>
 * <li>s3Object - The S3 object key.</li>
 * <li>attributes - User-defined attributes.</li>
 * </ul>
 */
public class FileTransferEvent {
    private long size;
    private String filename;
    private String mimeType;
    private String s3Object;
    private final Map<String, String> attributes = new LinkedHashMap<String, String>();

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getS3Object() {
        return s3Object;
    }

    public void setS3Object(String s3Object) {
        this.s3Object = s3Object;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileTransferEvent that = (FileTransferEvent) o;

        if (size != that.size) {
            return false;
        }
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) {
            return false;
        }
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) {
            return false;
        }
        if (s3Object != null ? !s3Object.equals(that.s3Object) : that.s3Object != null) {
            return false;
        }
        return attributes != null ? attributes.equals(that.attributes) : that.attributes == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (size ^ (size >>> 32));
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (s3Object != null ? s3Object.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}
