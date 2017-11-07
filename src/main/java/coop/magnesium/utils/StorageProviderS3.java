package coop.magnesium.utils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rsperoni on 07/11/17.
 */
@Stateless
public class StorageProviderS3 {

    private static String ACCESS_KEY = System.getenv("S3_ACCESS_KEY");
    private static String SECRET_KEY = System.getenv("S3_SECRET_KEY");
    private static String BUCKET_NAME = "omicflows-jobs";
    private AmazonS3 amazonS3;

    @PostConstruct
    public void init() {
        amazonS3 = new AmazonS3Client(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
    }

    @Logged
    public String put(String filename, String folder, InputStream inputStream) throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        byte[] resultByte = IOUtils.toByteArray(inputStream);
        meta.setContentLength(resultByte.length);
        String finalName = folder + "/" + filename;
        amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, finalName, new ByteArrayInputStream(resultByte), meta));
        return amazonS3.getUrl(BUCKET_NAME, finalName).toString();
    }


}
