package no.cantara.aws.sqs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP compression utilities.
 */
final class Gzip {
    static byte[] compress(final byte[] payload) throws IOException {
        if ((payload == null) || (payload.length == 0)) {
            return new byte[0];
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(buffer);

        gzip.write(payload);
        gzip.close();
        buffer.close();

        return buffer.toByteArray();
    }

    static byte[] decompress(final byte[] payload) throws IOException {
        if ((payload == null) || (payload.length == 0)) {
            return null;
        }

        if (!isCompressed(payload)) {
            return payload;
        }

        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(payload));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] bytes = new byte[1024];
        int length;

        while ((length = gzip.read(bytes)) > 0) {
            buffer.write(bytes, 0, length);
        }

        gzip.close();
        buffer.close();

        return buffer.toByteArray();
    }

    private static boolean isCompressed(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
}
