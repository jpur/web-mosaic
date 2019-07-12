package mosaic.data;

import mosaic.util.id.IdProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class S3ImageStore implements ImageStore {
    private final IdProvider idProvider;
    private final S3Client s3;
    private final String bucket;
    private final String rootDir;

    public S3ImageStore(IdProvider idProvider, S3Client s3, String bucket, String rootDir) {
        this.idProvider = idProvider;
        this.s3 = s3;
        this.bucket = bucket;
        this.rootDir = rootDir;
    }

    @Override
    public String add(BufferedImage img, String format) throws IOException {
        String key = idProvider.provide();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("format", format);

        PutObjectRequest req = PutObjectRequest.builder().bucket(bucket).key(key).metadata(metadata).build();

        // Get image bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, format, baos);
        byte[] bytes = baos.toByteArray();
        baos.close();

        s3.putObject(req, RequestBody.fromBytes(bytes));
        return key;
    }

    @Override
    public File get(String key) throws IOException {
        GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        ResponseBytes<GetObjectResponse> obj = s3.getObject(req, ResponseTransformer.toBytes());

        String format = obj.response().metadata().get("format");
        File file = new File(rootDir, String.format("%s.%s", key, format));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(obj.asByteArray());
        fos.close();

        return file;
    }
}
