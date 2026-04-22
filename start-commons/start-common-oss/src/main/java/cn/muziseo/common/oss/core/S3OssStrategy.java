package cn.muziseo.common.oss.core;

import cn.muziseo.common.oss.entity.OssProperties;
import cn.muziseo.common.oss.entity.UploadResult;
import cn.muziseo.common.oss.enums.AccessPolicyType;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * S3 存储策略 (兼容 MinIO, 阿里云 OSS, 腾讯云 COS 等)
 *
 * @author 木子软件
 */
@Slf4j
public class S3OssStrategy extends AbstractOssStrategy {

    private final AmazonS3 client;

    public S3OssStrategy(OssProperties properties) {
        this.properties = properties;
        try {
            AWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setProtocol(properties.getIsHttps() ? Protocol.HTTPS : Protocol.HTTP);

            AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withClientConfiguration(clientConfig)
                    .withPathStyleAccessEnabled(true); // 兼容 MinIO

            if (properties.getEndpoint().contains("aliyuncs.com")) {
                // 阿里云特殊处理，通常不需要强制 PathStyle
                builder.withPathStyleAccessEnabled(false);
            }

            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion()));
            this.client = builder.build();
        } catch (Exception e) {
            log.error("初始化 S3 存储客户端失败: {}", e.getMessage());
            throw new RuntimeException("OSS 客户端初始化异常", e);
        }
    }

    @Override
    public UploadResult upload(byte[] data, String key, String contentType) {
        return upload(new ByteArrayInputStream(data), key, contentType);
    }

    @Override
    public UploadResult upload(InputStream inputStream, String key, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), key, inputStream, metadata);
            
            // 如果是公共读桶，设置 ACL
            if (properties.getAccessPolicy() == AccessPolicyType.PUBLIC_READ) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            
            client.putObject(putObjectRequest);
            
            return UploadResult.builder()
                    .url(getUrl(key))
                    .fileName(key)
                    .service(properties.getConfigKey())
                    .build();
        } catch (Exception e) {
            log.error("S3 文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传异常", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            client.deleteObject(properties.getBucketName(), key);
        } catch (Exception e) {
            log.error("S3 文件删除失败: {}", e.getMessage());
            throw new RuntimeException("文件删除异常", e);
        }
    }

    @Override
    public String getUrl(String key) {
        // 如果配置了自定义域名
        if (org.springframework.util.StringUtils.hasText(properties.getDomain())) {
            String domain = properties.getDomain();
            if (!domain.endsWith("/")) {
                domain += "/";
            }
            return domain + key;
        }
        
        // 否则使用 SDK 生成地址
        if (properties.getAccessPolicy() == AccessPolicyType.PRIVATE) {
            // 私有桶生成带签名的 URL (默认 1 小时)
            return client.generatePresignedUrl(properties.getBucketName(), key, 
                    new java.util.Date(System.currentTimeMillis() + 3600 * 1000)).toString();
        }
        
        return client.getUrl(properties.getBucketName(), key).toString();
    }
}
