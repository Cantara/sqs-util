package no.cantara.aws.sqs;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
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
public final class KMSCryptoUtil {
    public static final String DEFAULT_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static final String DEFAULT_CMK = "alias/cantara/testcmk";

    public static final String DEFAULT_CONTENT_ENCODING = "gzip";

    public static final Region DEFAULT_REGION = Region.getRegion(Regions.EU_WEST_1);

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final Map<Region, AWSKMSClient> REGION = new HashMap<Region, AWSKMSClient>();

    public static byte[] decrypt(final Region region, final byte[] key, final byte[] iv, final byte[] payload) {
        DecryptResult decryptResult = getAWSKMS(region).decrypt(new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(key)));

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

    public static String decrypt (final Region region, final String key, final String iv, final String payload) {
        return new String (decrypt(region, Base64.decode(key), Base64.decode(iv), Base64.decode(payload)), DEFAULT_CHARSET);
    }

    public static String decrypt(final Region region, final Map<Object, Object> map) {
        return decrypt(region, map.get("key").toString(), map.get("iv").toString(), map.get("cipher").toString());
    }

    public static String decrypt(final Region region, String payload) {
        return decrypt(region, JsonUtil.toMap(payload));
    }

    public static String decrypt(String payload) {
        return decrypt(DEFAULT_REGION, payload);
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

    public static String encrypt(final Region region, final String kmsCmkId, final String payload) {
        return encrypt(generateDataKey(region, kmsCmkId), payload);
    }

    public static String encrypt(final Region region, final String payload) {
        return encrypt(region, DEFAULT_CMK, payload);
    }

    public static String encrypt(final String payload) {
        return encrypt(DEFAULT_REGION, payload);
    }

    public static GenerateDataKeyResult generateDataKey(final Region region, final String kmsCmkId) {
        return getAWSKMS(region).generateDataKey(new GenerateDataKeyRequest().withKeyId(kmsCmkId).withKeySpec(DataKeySpec.AES_256));
    }

    private static AWSKMS getAWSKMS(final Region region) {
        if (!REGION.containsKey(region)) {
            AWSKMSClient client = new AWSKMSClient(new DefaultAWSCredentialsProviderChain());
            client.setRegion(region);
            REGION.put(region, client);
        }
        return REGION.get(region);
    }
}
