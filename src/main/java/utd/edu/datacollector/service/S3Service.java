package utd.edu.datacollector.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * The S3 service - s3 based Service
 */
@Service
public class S3Service {

    /**
     * The Amazon s3 client.
     */
    @Autowired
    private AmazonS3 amazonS3Client;

    /**
     * The Bucket name.
     */
    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * The Object mapper.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Upload to s3 string.
     * Upload data to s3 bucket
     *
     * @param data the data
     * @param key  the key
     * @return the string
     * @throws IOException the io exception
     */
    public String uploadToS3(Object data, String key) throws IOException {
        byte[] contentAsBytes = objectMapper.writeValueAsBytes(data);
        ByteArrayInputStream contentsAsStream = new ByteArrayInputStream(contentAsBytes);
        ObjectMetadata md = new ObjectMetadata();
        md.setContentLength(contentAsBytes.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, contentsAsStream, md);
        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, key).toString();
    }
}
