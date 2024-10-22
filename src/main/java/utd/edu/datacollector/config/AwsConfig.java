/*
 * Copyright (c) 2024.
 * Created by Gokul G.K
 */

package utd.edu.datacollector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * The Aws config : Bean for aws client config.
 */
@Configuration
public class AwsConfig {

    /**
     * The Access key.
     */
    @Value("${aws.accessKey}")
    private String accessKey;

    /**
     * The Secret key.
     */
    @Value("${aws.secretKey}")
    private String secretKey;

    /**
     * The Region.
     */
    @Value("${aws.region}")
    private String region;

    /**
     * Amazon s3 client amazon s3. Generate client based on key,secret and region
     *
     * @return the amazon s3 Client
     */
    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    }
}
