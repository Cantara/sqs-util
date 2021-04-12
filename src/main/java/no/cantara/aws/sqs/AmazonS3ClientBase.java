package no.cantara.aws.sqs;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.S3ResponseMetadata;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.amazonaws.services.s3.model.ownership.OwnershipControls;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class AmazonS3ClientBase implements AmazonS3 {
    private final AmazonS3 delegate;

    AmazonS3ClientBase(final AmazonS3 delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setEndpoint(String s) {
        delegate.setEndpoint(s);
    }

    @Override
    public void setRegion(Region region) throws IllegalArgumentException {
        delegate.setRegion(region);
    }

    @Override
    public void setRequestPaymentConfiguration(SetRequestPaymentConfigurationRequest setRequestPaymentConfigurationRequest) {
        delegate.setRequestPaymentConfiguration(setRequestPaymentConfigurationRequest);
    }

    @Override
    public void setS3ClientOptions(S3ClientOptions s3ClientOptions) {
        delegate.setS3ClientOptions(s3ClientOptions);
    }

    @Override
    public SetObjectRetentionResult setObjectRetention(SetObjectRetentionRequest var1) {
        return delegate.setObjectRetention(var1);
    }

    @Override
    public GetObjectRetentionResult getObjectRetention(GetObjectRetentionRequest var1) {
        return delegate.getObjectRetention(var1);
    }

    @Override
    public WriteGetObjectResponseResult writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest) {
        return delegate.writeGetObjectResponse(writeGetObjectResponseRequest);
    }

    @Override
    public PresignedUrlDownloadResult download(PresignedUrlDownloadRequest presignedUrlDownloadRequest) {
        return delegate.download(presignedUrlDownloadRequest);
    }

    @Override
    public void download(PresignedUrlDownloadRequest presignedUrlDownloadRequest, File file) {
        delegate.download(presignedUrlDownloadRequest, file);
    }

    @Override
    public PresignedUrlUploadResult upload(PresignedUrlUploadRequest presignedUrlUploadRequest) {
        return delegate.upload(presignedUrlUploadRequest);
    }

    @Override
    public SetObjectLockConfigurationResult setObjectLockConfiguration(SetObjectLockConfigurationRequest var1) {
        return delegate.setObjectLockConfiguration(var1);
    }

    @Override
    public GetObjectLockConfigurationResult getObjectLockConfiguration(GetObjectLockConfigurationRequest var1) {
        return delegate.getObjectLockConfiguration(var1);
    }

    @Override
    public SetObjectLegalHoldResult setObjectLegalHold(SetObjectLegalHoldRequest var1) {
        return delegate.setObjectLegalHold(var1);
    }

    @Override
    public GetObjectLegalHoldResult getObjectLegalHold(GetObjectLegalHoldRequest var1) {
        return delegate.getObjectLegalHold(var1);
    }

    @Override
    public GetBucketPolicyStatusResult getBucketPolicyStatus(GetBucketPolicyStatusRequest var1) {
        return delegate.getBucketPolicyStatus(var1);
    }

    @Override
    public DeletePublicAccessBlockResult deletePublicAccessBlock(DeletePublicAccessBlockRequest var1) {
        return delegate.deletePublicAccessBlock(var1);
    }

    @Override
    public SetPublicAccessBlockResult setPublicAccessBlock(SetPublicAccessBlockRequest var1) {
        return delegate.setPublicAccessBlock(var1);
    }

    @Override
    public GetPublicAccessBlockResult getPublicAccessBlock(GetPublicAccessBlockRequest var1) {
        return delegate.getPublicAccessBlock(var1);
    }

    @Override
    @Deprecated
    public void changeObjectStorageClass(String s, String s1, StorageClass storageClass) throws SdkClientException {
        delegate.changeObjectStorageClass(s, s1, storageClass);
    }

    @Override
    @Deprecated
    public void setObjectRedirectLocation(String s, String s1, String s2) throws SdkClientException {
        delegate.setObjectRedirectLocation(s, s1, s2);
    }

    @Override
    public ObjectListing listObjects(String s) throws SdkClientException {
        return delegate.listObjects(s);
    }

    @Override
    public ObjectListing listObjects(String s, String s1) throws SdkClientException {
        return delegate.listObjects(s, s1);
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws SdkClientException {
        return delegate.listObjects(listObjectsRequest);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String s) throws SdkClientException {
        return delegate.listObjectsV2(s);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(String s, String s1) throws SdkClientException {
        return delegate.listObjectsV2(s, s1);
    }

    @Override
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws SdkClientException {
        return delegate.listObjectsV2(listObjectsV2Request);
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ObjectListing objectListing) throws SdkClientException {
        return delegate.listNextBatchOfObjects(objectListing);
    }

    @Override
    public ObjectListing listNextBatchOfObjects(ListNextBatchOfObjectsRequest listNextBatchOfObjectsRequest) throws SdkClientException {
        return delegate.listNextBatchOfObjects(listNextBatchOfObjectsRequest);
    }

    @Override
    public VersionListing listVersions(String s, String s1) throws SdkClientException {
        return delegate.listVersions(s, s1);
    }

    @Override
    public VersionListing listNextBatchOfVersions(VersionListing versionListing) throws SdkClientException {
        return delegate.listNextBatchOfVersions(versionListing);
    }

    @Override
    public VersionListing listNextBatchOfVersions(ListNextBatchOfVersionsRequest listNextBatchOfVersionsRequest) throws SdkClientException {
        return delegate.listNextBatchOfVersions(listNextBatchOfVersionsRequest);
    }

    @Override
    public VersionListing listVersions(String s, String s1, String s2, String s3, String s4, Integer integer) throws SdkClientException {
        return delegate.listVersions(s, s1, s2, s3, s4, integer);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws SdkClientException {
        return delegate.listVersions(listVersionsRequest);
    }

    @Override
    public Owner getS3AccountOwner() throws SdkClientException {
        return delegate.getS3AccountOwner();
    }

    @Override
    public Owner getS3AccountOwner(GetS3AccountOwnerRequest getS3AccountOwnerRequest) throws SdkClientException {
        return delegate.getS3AccountOwner(getS3AccountOwnerRequest);
    }

    @Override
    @Deprecated
    public boolean doesBucketExist(String s) throws SdkClientException {
        return delegate.doesBucketExist(s);
    }

    @Override
    public boolean doesBucketExistV2(String s) throws SdkClientException {
        return delegate.doesBucketExistV2(s);
    }

    @Override
    public HeadBucketResult headBucket(HeadBucketRequest headBucketRequest) throws SdkClientException {
        return delegate.headBucket(headBucketRequest);
    }

    @Override
    public List<Bucket> listBuckets() throws SdkClientException {
        return delegate.listBuckets();
    }

    @Override
    public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest) throws SdkClientException {
        return delegate.listBuckets(listBucketsRequest);
    }

    @Override
    public String getBucketLocation(String s) throws SdkClientException {
        return delegate.getBucketLocation(s);
    }

    @Override
    public String getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws SdkClientException {
        return delegate.getBucketLocation(getBucketLocationRequest);
    }

    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws SdkClientException {
        return delegate.createBucket(createBucketRequest);
    }

    @Override
    public Bucket createBucket(String s) throws SdkClientException {
        return delegate.createBucket(s);
    }

    @Override
    @Deprecated
    public Bucket createBucket(String s, com.amazonaws.services.s3.model.Region region) throws SdkClientException {
        return delegate.createBucket(s, region);
    }

    @Override
    @Deprecated
    public Bucket createBucket(String s, String s1) throws SdkClientException {
        return delegate.createBucket(s, s1);
    }

    @Override
    public AccessControlList getObjectAcl(String s, String s1) throws SdkClientException {
        return delegate.getObjectAcl(s, s1);
    }

    @Override
    public AccessControlList getObjectAcl(String s, String s1, String s2) throws SdkClientException {
        return delegate.getObjectAcl(s, s1, s2);
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclRequest getObjectAclRequest) throws SdkClientException {
        return delegate.getObjectAcl(getObjectAclRequest);
    }

    @Override
    public void setObjectAcl(String s, String s1, AccessControlList accessControlList) throws SdkClientException {
        delegate.setObjectAcl(s, s1, accessControlList);
    }

    @Override
    public void setObjectAcl(String s, String s1, CannedAccessControlList cannedAccessControlList) throws SdkClientException {
        delegate.setObjectAcl(s, s1, cannedAccessControlList);
    }

    @Override
    public void setObjectAcl(String s, String s1, String s2, AccessControlList accessControlList) throws SdkClientException {
        delegate.setObjectAcl(s, s1, s2, accessControlList);
    }

    @Override
    public void setObjectAcl(String s, String s1, String s2, CannedAccessControlList cannedAccessControlList) throws SdkClientException {
        delegate.setObjectAcl(s, s1, s2, cannedAccessControlList);
    }

    @Override
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest) throws SdkClientException {
        delegate.setObjectAcl(setObjectAclRequest);
    }

    @Override
    public AccessControlList getBucketAcl(String s) throws SdkClientException {
        return delegate.getBucketAcl(s);
    }

    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws SdkClientException {
        delegate.setBucketAcl(setBucketAclRequest);
    }

    @Override
    public AccessControlList getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws SdkClientException {
        return delegate.getBucketAcl(getBucketAclRequest);
    }

    @Override
    public void setBucketAcl(String s, AccessControlList accessControlList) throws SdkClientException {
        delegate.setBucketAcl(s, accessControlList);
    }

    @Override
    public void setBucketAcl(String s, CannedAccessControlList cannedAccessControlList) throws SdkClientException {
        delegate.setBucketAcl(s, cannedAccessControlList);
    }

    @Override
    public ObjectMetadata getObjectMetadata(String s, String s1) throws SdkClientException {
        return delegate.getObjectMetadata(s, s1);
    }

    @Override
    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest getObjectMetadataRequest) throws SdkClientException {
        return delegate.getObjectMetadata(getObjectMetadataRequest);
    }

    @Override
    public S3Object getObject(String s, String s1) throws SdkClientException {
        return delegate.getObject(s, s1);
    }

    @Override
    public S3Object getObject(GetObjectRequest getObjectRequest) throws SdkClientException {
        return delegate.getObject(getObjectRequest);
    }

    @Override
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws SdkClientException {
        return delegate.getObject(getObjectRequest, file);
    }

    @Override
    public String getObjectAsString(String s, String s1) throws SdkClientException {
        return delegate.getObjectAsString(s, s1);
    }

    @Override
    public GetObjectTaggingResult getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        return delegate.getObjectTagging(getObjectTaggingRequest);
    }

    @Override
    public SetObjectTaggingResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) {
        return delegate.setObjectTagging(setObjectTaggingRequest);
    }

    @Override
    public DeleteObjectTaggingResult deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        return delegate.deleteObjectTagging(deleteObjectTaggingRequest);
    }

    @Override
    public void deleteBucket(DeleteBucketRequest deleteBucketRequest) throws SdkClientException {
        delegate.deleteBucket(deleteBucketRequest);
    }

    @Override
    public void deleteBucket(String s) throws SdkClientException {
        delegate.deleteBucket(s);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws SdkClientException {
        return delegate.putObject(putObjectRequest);
    }

    @Override
    public PutObjectResult putObject(String s, String s1, File file) throws SdkClientException {
        return delegate.putObject(s, s1, file);
    }

    @Override
    public PutObjectResult putObject(String s, String s1, InputStream inputStream, ObjectMetadata objectMetadata) throws SdkClientException {
        return delegate.putObject(s, s1, inputStream, objectMetadata);
    }

    @Override
    public PutObjectResult putObject(String s, String s1, String s2) throws SdkClientException {
        return delegate.putObject(s, s1, s2);
    }

    @Override
    public CopyObjectResult copyObject(String s, String s1, String s2, String s3) throws SdkClientException {
        return delegate.copyObject(s, s1, s2, s3);
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws SdkClientException {
        return delegate.copyObject(copyObjectRequest);
    }

    @Override
    public CopyPartResult copyPart(CopyPartRequest copyPartRequest) throws SdkClientException {
        return delegate.copyPart(copyPartRequest);
    }

    @Override
    public void deleteObject(String s, String s1) throws SdkClientException {
        delegate.deleteObject(s, s1);
    }

    @Override
    public void deleteObject(DeleteObjectRequest deleteObjectRequest) throws SdkClientException {
        delegate.deleteObject(deleteObjectRequest);
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws SdkClientException {
        return delegate.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public void deleteVersion(String s, String s1, String s2) throws SdkClientException {
        delegate.deleteVersion(s, s1, s2);
    }

    @Override
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws SdkClientException {
        delegate.deleteVersion(deleteVersionRequest);
    }

    @Override
    public BucketLoggingConfiguration getBucketLoggingConfiguration(String s) throws SdkClientException {
        return delegate.getBucketLoggingConfiguration(s);
    }

    @Override
    public BucketLoggingConfiguration getBucketLoggingConfiguration(GetBucketLoggingConfigurationRequest getBucketLoggingConfigurationRequest) throws SdkClientException {
        return delegate.getBucketLoggingConfiguration(getBucketLoggingConfigurationRequest);
    }

    @Override
    public void setBucketLoggingConfiguration(SetBucketLoggingConfigurationRequest setBucketLoggingConfigurationRequest) throws SdkClientException {
        delegate.setBucketLoggingConfiguration(setBucketLoggingConfigurationRequest);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(String s) throws SdkClientException {
        return delegate.getBucketVersioningConfiguration(s);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioningConfiguration(GetBucketVersioningConfigurationRequest getBucketVersioningConfigurationRequest) throws SdkClientException {
        return delegate.getBucketVersioningConfiguration(getBucketVersioningConfigurationRequest);
    }

    @Override
    public void setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest) throws SdkClientException {
        delegate.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String s) {
        return delegate.getBucketLifecycleConfiguration(s);
    }

    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        return delegate.getBucketLifecycleConfiguration(getBucketLifecycleConfigurationRequest);
    }

    @Override
    public void setBucketLifecycleConfiguration(String s, BucketLifecycleConfiguration bucketLifecycleConfiguration) {
        delegate.setBucketLifecycleConfiguration(s, bucketLifecycleConfiguration);
    }

    @Override
    public void setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest) {
        delegate.setBucketLifecycleConfiguration(setBucketLifecycleConfigurationRequest);
    }

    @Override
    public void deleteBucketLifecycleConfiguration(String s) {
        delegate.deleteBucketLifecycleConfiguration(s);
    }

    @Override
    public void deleteBucketLifecycleConfiguration(DeleteBucketLifecycleConfigurationRequest deleteBucketLifecycleConfigurationRequest) {
        delegate.deleteBucketLifecycleConfiguration(deleteBucketLifecycleConfigurationRequest);
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(String s) {
        return delegate.getBucketCrossOriginConfiguration(s);
    }

    @Override
    public BucketCrossOriginConfiguration getBucketCrossOriginConfiguration(GetBucketCrossOriginConfigurationRequest getBucketCrossOriginConfigurationRequest) {
        return delegate.getBucketCrossOriginConfiguration(getBucketCrossOriginConfigurationRequest);
    }

    @Override
    public void setBucketCrossOriginConfiguration(String s, BucketCrossOriginConfiguration bucketCrossOriginConfiguration) {
        delegate.setBucketCrossOriginConfiguration(s, bucketCrossOriginConfiguration);
    }

    @Override
    public void setBucketCrossOriginConfiguration(SetBucketCrossOriginConfigurationRequest setBucketCrossOriginConfigurationRequest) {
        delegate.setBucketCrossOriginConfiguration(setBucketCrossOriginConfigurationRequest);
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(String s) {
        delegate.deleteBucketCrossOriginConfiguration(s);
    }

    @Override
    public void deleteBucketCrossOriginConfiguration(DeleteBucketCrossOriginConfigurationRequest deleteBucketCrossOriginConfigurationRequest) {
        delegate.deleteBucketCrossOriginConfiguration(deleteBucketCrossOriginConfigurationRequest);
    }

    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(String s) {
        return delegate.getBucketTaggingConfiguration(s);
    }

    @Override
    public BucketTaggingConfiguration getBucketTaggingConfiguration(GetBucketTaggingConfigurationRequest getBucketTaggingConfigurationRequest) {
        return delegate.getBucketTaggingConfiguration(getBucketTaggingConfigurationRequest);
    }

    @Override
    public void setBucketTaggingConfiguration(String s, BucketTaggingConfiguration bucketTaggingConfiguration) {
        delegate.setBucketTaggingConfiguration(s, bucketTaggingConfiguration);
    }

    @Override
    public void setBucketTaggingConfiguration(SetBucketTaggingConfigurationRequest setBucketTaggingConfigurationRequest) {
        delegate.setBucketTaggingConfiguration(setBucketTaggingConfigurationRequest);
    }

    @Override
    public void deleteBucketTaggingConfiguration(String s) {
        delegate.deleteBucketTaggingConfiguration(s);
    }

    @Override
    public void deleteBucketTaggingConfiguration(DeleteBucketTaggingConfigurationRequest deleteBucketTaggingConfigurationRequest) {
        delegate.deleteBucketTaggingConfiguration(deleteBucketTaggingConfigurationRequest);
    }

    @Override
    public BucketNotificationConfiguration getBucketNotificationConfiguration(String s) throws SdkClientException {
        return delegate.getBucketNotificationConfiguration(s);
    }

    @Override
    public BucketNotificationConfiguration getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) throws SdkClientException {
        return delegate.getBucketNotificationConfiguration(getBucketNotificationConfigurationRequest);
    }

    @Override
    public void setBucketNotificationConfiguration(SetBucketNotificationConfigurationRequest setBucketNotificationConfigurationRequest) throws SdkClientException {
        delegate.setBucketNotificationConfiguration(setBucketNotificationConfigurationRequest);
    }

    @Override
    public void setBucketNotificationConfiguration(String s, BucketNotificationConfiguration bucketNotificationConfiguration) throws SdkClientException {
        delegate.setBucketNotificationConfiguration(s, bucketNotificationConfiguration);
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String s) throws SdkClientException {
        return delegate.getBucketWebsiteConfiguration(s);
    }

    @Override
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(GetBucketWebsiteConfigurationRequest getBucketWebsiteConfigurationRequest) throws SdkClientException {
        return delegate.getBucketWebsiteConfiguration(getBucketWebsiteConfigurationRequest);
    }

    @Override
    public void setBucketWebsiteConfiguration(String s, BucketWebsiteConfiguration bucketWebsiteConfiguration) throws SdkClientException {
        delegate.setBucketWebsiteConfiguration(s, bucketWebsiteConfiguration);
    }

    @Override
    public void setBucketWebsiteConfiguration(SetBucketWebsiteConfigurationRequest setBucketWebsiteConfigurationRequest) throws SdkClientException {
        delegate.setBucketWebsiteConfiguration(setBucketWebsiteConfigurationRequest);
    }

    @Override
    public void deleteBucketWebsiteConfiguration(String s) throws SdkClientException {
        delegate.deleteBucketWebsiteConfiguration(s);
    }

    @Override
    public void deleteBucketWebsiteConfiguration(DeleteBucketWebsiteConfigurationRequest deleteBucketWebsiteConfigurationRequest) throws SdkClientException {
        delegate.deleteBucketWebsiteConfiguration(deleteBucketWebsiteConfigurationRequest);
    }

    @Override
    public BucketPolicy getBucketPolicy(String s) throws SdkClientException {
        return delegate.getBucketPolicy(s);
    }

    @Override
    public BucketPolicy getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) throws SdkClientException {
        return delegate.getBucketPolicy(getBucketPolicyRequest);
    }

    @Override
    public void setBucketPolicy(String s, String s1) throws SdkClientException {
        delegate.setBucketPolicy(s, s1);
    }

    @Override
    public void setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws SdkClientException {
        delegate.setBucketPolicy(setBucketPolicyRequest);
    }

    @Override
    public void deleteBucketPolicy(String s) throws SdkClientException {
        delegate.deleteBucketPolicy(s);
    }

    @Override
    public void deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) throws SdkClientException {
        delegate.deleteBucketPolicy(deleteBucketPolicyRequest);
    }

    @Override
    public URL generatePresignedUrl(String s, String s1, Date date) throws SdkClientException {
        return delegate.generatePresignedUrl(s, s1, date);
    }

    @Override
    public URL generatePresignedUrl(String s, String s1, Date date, HttpMethod httpMethod) throws SdkClientException {
        return delegate.generatePresignedUrl(s, s1, date, httpMethod);
    }

    @Override
    public URL generatePresignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) throws SdkClientException {
        return delegate.generatePresignedUrl(generatePresignedUrlRequest);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest initiateMultipartUploadRequest) throws SdkClientException {
        return delegate.initiateMultipartUpload(initiateMultipartUploadRequest);
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest) throws SdkClientException {
        return delegate.uploadPart(uploadPartRequest);
    }

    @Override
    public PartListing listParts(ListPartsRequest listPartsRequest) throws SdkClientException {
        return delegate.listParts(listPartsRequest);
    }

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) throws SdkClientException {
        delegate.abortMultipartUpload(abortMultipartUploadRequest);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) throws SdkClientException {
        return delegate.completeMultipartUpload(completeMultipartUploadRequest);
    }

    @Override
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) throws SdkClientException {
        return delegate.listMultipartUploads(listMultipartUploadsRequest);
    }

    @Override
    public S3ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest amazonWebServiceRequest) {
        return delegate.getCachedResponseMetadata(amazonWebServiceRequest);
    }

    @Override
    @Deprecated
    public void restoreObject(RestoreObjectRequest restoreObjectRequest) throws AmazonServiceException {
        delegate.restoreObject(restoreObjectRequest);
    }

    @Override
    public RestoreObjectResult restoreObjectV2(RestoreObjectRequest restoreObjectRequest) throws AmazonServiceException {
        return delegate.restoreObjectV2(restoreObjectRequest);
    }

    @Override
    @Deprecated
    public void restoreObject(String s, String s1, int i) throws AmazonServiceException {
        delegate.restoreObject(s, s1, i);
    }

    @Override
    public void enableRequesterPays(String s) throws SdkClientException {
        delegate.enableRequesterPays(s);
    }

    @Override
    public void disableRequesterPays(String s) throws SdkClientException {
        delegate.disableRequesterPays(s);
    }

    @Override
    public boolean isRequesterPaysEnabled(String s) throws SdkClientException {
        return delegate.isRequesterPaysEnabled(s);
    }

    @Override
    public void setBucketReplicationConfiguration(String s, BucketReplicationConfiguration bucketReplicationConfiguration) throws SdkClientException {
        delegate.setBucketReplicationConfiguration(s, bucketReplicationConfiguration);
    }

    @Override
    public void setBucketReplicationConfiguration(SetBucketReplicationConfigurationRequest setBucketReplicationConfigurationRequest) throws SdkClientException {
        delegate.setBucketReplicationConfiguration(setBucketReplicationConfigurationRequest);
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(String s) throws SdkClientException {
        return delegate.getBucketReplicationConfiguration(s);
    }

    @Override
    public BucketReplicationConfiguration getBucketReplicationConfiguration(GetBucketReplicationConfigurationRequest getBucketReplicationConfigurationRequest) throws SdkClientException {
        return delegate.getBucketReplicationConfiguration(getBucketReplicationConfigurationRequest);
    }

    @Override
    public void deleteBucketReplicationConfiguration(String s) throws SdkClientException {
        delegate.deleteBucketReplicationConfiguration(s);
    }

    @Override
    public void deleteBucketReplicationConfiguration(DeleteBucketReplicationConfigurationRequest deleteBucketReplicationConfigurationRequest) throws SdkClientException {
        delegate.deleteBucketReplicationConfiguration(deleteBucketReplicationConfigurationRequest);
    }

    @Override
    public boolean doesObjectExist(String s, String s1) throws SdkClientException {
        return delegate.doesObjectExist(s, s1);
    }

    @Override
    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(String s) throws SdkClientException {
        return delegate.getBucketAccelerateConfiguration(s);
    }

    @Override
    public BucketAccelerateConfiguration getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) throws SdkClientException {
        return delegate.getBucketAccelerateConfiguration(getBucketAccelerateConfigurationRequest);
    }

    @Override
    public void setBucketAccelerateConfiguration(String s, BucketAccelerateConfiguration bucketAccelerateConfiguration) throws SdkClientException {
        delegate.setBucketAccelerateConfiguration(s, bucketAccelerateConfiguration);
    }

    @Override
    public void setBucketAccelerateConfiguration(SetBucketAccelerateConfigurationRequest setBucketAccelerateConfigurationRequest) throws SdkClientException {
        delegate.setBucketAccelerateConfiguration(setBucketAccelerateConfigurationRequest);
    }

    @Override
    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(String s, String s1) throws SdkClientException {
        return delegate.deleteBucketMetricsConfiguration(s, s1);
    }

    @Override
    public DeleteBucketMetricsConfigurationResult deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) throws SdkClientException {
        return delegate.deleteBucketMetricsConfiguration(deleteBucketMetricsConfigurationRequest);
    }

    @Override
    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(String s, String s1) throws SdkClientException {
        return delegate.getBucketMetricsConfiguration(s, s1);
    }

    @Override
    public GetBucketMetricsConfigurationResult getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) throws SdkClientException {
        return delegate.getBucketMetricsConfiguration(getBucketMetricsConfigurationRequest);
    }

    @Override
    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(String s, MetricsConfiguration metricsConfiguration) throws SdkClientException {
        return delegate.setBucketMetricsConfiguration(s, metricsConfiguration);
    }

    @Override
    public SetBucketMetricsConfigurationResult setBucketMetricsConfiguration(SetBucketMetricsConfigurationRequest setBucketMetricsConfigurationRequest) throws SdkClientException {
        return delegate.setBucketMetricsConfiguration(setBucketMetricsConfigurationRequest);
    }

    @Override
    public ListBucketMetricsConfigurationsResult listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) throws SdkClientException {
        return delegate.listBucketMetricsConfigurations(listBucketMetricsConfigurationsRequest);
    }

    @Override
    public DeleteBucketOwnershipControlsResult deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) throws AmazonServiceException, SdkClientException {
        return delegate.deleteBucketOwnershipControls(deleteBucketOwnershipControlsRequest);
    }

    @Override
    public GetBucketOwnershipControlsResult getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) throws AmazonServiceException, SdkClientException {
        return delegate.getBucketOwnershipControls(getBucketOwnershipControlsRequest);
    }

    @Override
    public SetBucketOwnershipControlsResult setBucketOwnershipControls(String s, OwnershipControls ownershipControls) throws AmazonServiceException, SdkClientException {
        return delegate.setBucketOwnershipControls(s,ownershipControls);
    }

    @Override
    public SetBucketOwnershipControlsResult setBucketOwnershipControls(SetBucketOwnershipControlsRequest setBucketOwnershipControlsRequest) throws AmazonServiceException, SdkClientException {
        return delegate.setBucketOwnershipControls(setBucketOwnershipControlsRequest);
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(String s, String s1) throws SdkClientException {
        return delegate.deleteBucketAnalyticsConfiguration(s, s1);
    }

    @Override
    public DeleteBucketAnalyticsConfigurationResult deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) throws SdkClientException {
        return delegate.deleteBucketAnalyticsConfiguration(deleteBucketAnalyticsConfigurationRequest);
    }

    @Override
    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(String s, String s1) throws SdkClientException {
        return delegate.getBucketAnalyticsConfiguration(s, s1);
    }

    @Override
    public GetBucketAnalyticsConfigurationResult getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) throws SdkClientException {
        return delegate.getBucketAnalyticsConfiguration(getBucketAnalyticsConfigurationRequest);
    }

    @Override
    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(String s, AnalyticsConfiguration analyticsConfiguration) throws SdkClientException {
        return delegate.setBucketAnalyticsConfiguration(s, analyticsConfiguration);
    }

    @Override
    public SetBucketAnalyticsConfigurationResult setBucketAnalyticsConfiguration(SetBucketAnalyticsConfigurationRequest setBucketAnalyticsConfigurationRequest) throws SdkClientException {
        return delegate.setBucketAnalyticsConfiguration(setBucketAnalyticsConfigurationRequest);
    }

    @Override
    public ListBucketAnalyticsConfigurationsResult listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) throws SdkClientException {
        return delegate.listBucketAnalyticsConfigurations(listBucketAnalyticsConfigurationsRequest);
    }

    @Override
    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(String s, String s1) throws AmazonServiceException, SdkClientException {
        return delegate.deleteBucketIntelligentTieringConfiguration(s, s1);
    }

    @Override
    public DeleteBucketIntelligentTieringConfigurationResult deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) throws AmazonServiceException, SdkClientException {
        return delegate.deleteBucketIntelligentTieringConfiguration(deleteBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(String s, String s1) throws AmazonServiceException, SdkClientException {
        return delegate.getBucketIntelligentTieringConfiguration(s, s1);
    }

    @Override
    public GetBucketIntelligentTieringConfigurationResult getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) throws AmazonServiceException, SdkClientException {
        return delegate.getBucketIntelligentTieringConfiguration(getBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(String s, IntelligentTieringConfiguration intelligentTieringConfiguration) throws AmazonServiceException, SdkClientException {
        return delegate.setBucketIntelligentTieringConfiguration(s, intelligentTieringConfiguration);
    }

    @Override
    public SetBucketIntelligentTieringConfigurationResult setBucketIntelligentTieringConfiguration(SetBucketIntelligentTieringConfigurationRequest setBucketIntelligentTieringConfigurationRequest) throws AmazonServiceException, SdkClientException {
        return delegate.setBucketIntelligentTieringConfiguration(setBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public ListBucketIntelligentTieringConfigurationsResult listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) throws AmazonServiceException, SdkClientException {
        return delegate.listBucketIntelligentTieringConfigurations(listBucketIntelligentTieringConfigurationsRequest);
    }

    @Override
    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(String s, String s1) throws SdkClientException {
        return delegate.deleteBucketInventoryConfiguration(s, s1);
    }

    @Override
    public DeleteBucketInventoryConfigurationResult deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) throws SdkClientException {
        return delegate.deleteBucketInventoryConfiguration(deleteBucketInventoryConfigurationRequest);
    }

    @Override
    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(String s, String s1) throws SdkClientException {
        return delegate.getBucketInventoryConfiguration(s, s1);
    }

    @Override
    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) throws SdkClientException {
        return delegate.getBucketInventoryConfiguration(getBucketInventoryConfigurationRequest);
    }

    @Override
    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(String s, InventoryConfiguration inventoryConfiguration) throws SdkClientException {
        return delegate.setBucketInventoryConfiguration(s, inventoryConfiguration);
    }

    @Override
    public SetBucketInventoryConfigurationResult setBucketInventoryConfiguration(SetBucketInventoryConfigurationRequest setBucketInventoryConfigurationRequest) throws SdkClientException {
        return delegate.setBucketInventoryConfiguration(setBucketInventoryConfigurationRequest);
    }

    @Override
    public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) throws SdkClientException {
        return delegate.listBucketInventoryConfigurations(listBucketInventoryConfigurationsRequest);
    }

    @Override
    public DeleteBucketEncryptionResult deleteBucketEncryption(String s) throws SdkClientException {
        return delegate.deleteBucketEncryption(s);
    }

    @Override
    public DeleteBucketEncryptionResult deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) throws SdkClientException {
        return delegate.deleteBucketEncryption(deleteBucketEncryptionRequest);
    }

    @Override
    public GetBucketEncryptionResult getBucketEncryption(String s) throws SdkClientException {
        return delegate.getBucketEncryption(s);
    }

    @Override
    public GetBucketEncryptionResult getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) throws SdkClientException {
        return delegate.getBucketEncryption(getBucketEncryptionRequest);
    }

    @Override
    public SetBucketEncryptionResult setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest) throws SdkClientException {
        return delegate.setBucketEncryption(setBucketEncryptionRequest);
    }

    @Override
    public SelectObjectContentResult selectObjectContent(SelectObjectContentRequest selectObjectContentRequest) throws SdkClientException {
        return delegate.selectObjectContent(selectObjectContentRequest);
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public com.amazonaws.services.s3.model.Region getRegion() {
        return delegate.getRegion();
    }

    @Override
    public String getRegionName() {
        return delegate.getRegionName();
    }

    @Override
    public URL getUrl(String s, String s1) {
        return delegate.getUrl(s, s1);
    }

    @Override
    public AmazonS3Waiters waiters() {
        return delegate.waiters();
    }
}
