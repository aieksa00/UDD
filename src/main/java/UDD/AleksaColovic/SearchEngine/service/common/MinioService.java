package UDD.AleksaColovic.SearchEngine.service.common;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MinioService {

    @Value("${spring.minio.bucket}")
    private String bucketName;

    private final MinioClient minioClient;

    public void uploadFile(String fileName, MultipartFile file) throws Exception {
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .headers(Collections.singletonMap("Content-Disposition",
                            "attachment; filename=\"" + file.getOriginalFilename() + "\""))
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new Exception("Error while storing file in Minio.");
        }
    }

    public GetObjectResponse loadFile(String fileName) {
        try {
            // Get signed URL
            var argsDownload = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(60 * 5) // in seconds
                    .build();
            var downloadUrl = minioClient.getPresignedObjectUrl(argsDownload);
            System.out.println(downloadUrl);

            // Get object response
            var args = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            return minioClient.getObject(args);

        } catch (Exception e) {
            return null;
        }
    }

    public void deleteFile(String fileName) throws Exception {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new Exception("Error while deleting " + fileName + " from Minio.");
        }
    }
}
