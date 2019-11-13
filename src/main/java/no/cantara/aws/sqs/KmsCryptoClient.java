package no.cantara.aws.sqs;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DataKeySpec;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import com.amazonaws.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Encryption/decryption utilities based on Amazon KMS (Key Management Service).
 */
public final class KmsCryptoClient {

    private final AWSKMS AWS_KMS_CLIENT;

    public static final String DEFAULT_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String DEFAULT_CMK = "alias/cantara/testcmk";
    public static final String DEFAULT_CONTENT_ENCODING = "gzip";
    public static final Regions DEFAULT_REGION = Regions.EU_WEST_1;
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public KmsCryptoClient(AWSKMS awsKmsClient) {
        this.AWS_KMS_CLIENT = awsKmsClient;
    }

    public KmsCryptoClient() {
        this.AWS_KMS_CLIENT =
                AWSKMSClientBuilder.standard()
                        .withRegion(DEFAULT_REGION)
                        .withCredentials(new DefaultAWSCredentialsProviderChain())
                        .build();
    }

    public byte[] decrypt(final byte[] key, final byte[] iv, final byte[] payload) {
        DecryptResult decryptResult = AWS_KMS_CLIENT.decrypt(new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(key)));

        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptResult.getPlaintext().array(), "AES"), new IvParameterSpec(iv));
            byte[] decryptedBytes = Gzip.decompress(cipher.doFinal(payload));
            return decryptedBytes != null ? decryptedBytes : new byte[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (decryptResult != null) {
                decryptResult.setPlaintext(ByteBuffer.wrap(new byte[0]));
            }
        }
    }

    public String decrypt(final String key, final String iv, final String payload) {
        return new String(decrypt(Base64.decode(key), Base64.decode(iv), Base64.decode(payload)), DEFAULT_CHARSET);
    }

    public String decrypt(final Map<Object, Object> map) {
        return decrypt(map.get("key").toString(), map.get("iv").toString(), map.get("cipher").toString());
    }

    public String decrypt(String payload) {
        return decrypt(JsonUtil.toMap(payload));
    }

    public static String encrypt(final String key, final byte[] plaintext, final byte[] ciphertext, final byte[] payload) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("algorithm", DEFAULT_ALGORITHM);
        map.put("encoding", DEFAULT_CONTENT_ENCODING);
        map.put("length", payload.length);
        map.put("cmk", key);

        try {
            byte[] buffer = Gzip.compress(payload);
            Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(plaintext, "AES"));
            map.put("cipher", Base64.encodeAsString(cipher.doFinal(buffer)));
            map.put("iv", Base64.encodeAsString(cipher.getIV()));
            map.put("key", Base64.encodeAsString(ciphertext));

            return JsonUtil.from(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(final GenerateDataKeyResult dataKeyResult, final String payload) {
        try {
            return encrypt(dataKeyResult.getKeyId(), dataKeyResult.getPlaintext().array(), dataKeyResult.getCiphertextBlob().array(), payload.getBytes(
                    DEFAULT_CHARSET));
        } finally {
            if (dataKeyResult != null) {
                dataKeyResult.setPlaintext(ByteBuffer.wrap(new byte[0]));
            }
        }
    }

    public String encrypt(final String kmsCmkId, final String payload) {
        return encrypt(generateDataKey(kmsCmkId), payload);
    }

    public String encrypt(final String payload) {
        return encrypt(DEFAULT_CMK, payload);
    }

    public GenerateDataKeyResult generateDataKey(final String kmsCmkId) {
        GenerateDataKeyRequest generateDataKeyRequest = new GenerateDataKeyRequest()
                .withKeyId(kmsCmkId)
                .withKeySpec(DataKeySpec.AES_256);
        return AWS_KMS_CLIENT.generateDataKey(generateDataKeyRequest);
    }

}
