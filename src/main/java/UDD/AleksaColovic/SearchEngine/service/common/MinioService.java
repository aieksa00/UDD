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
    private final MinioClient minioClient;

    public void uploadFile(String fileName, MultipartFile file, String bucketName) throws Exception {
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

    public String loadFile(String fileName, String bucketName) throws Exception {
        try {
            // Get signed URL
            var argsDownload = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(60 * 5) // in seconds
                    .build();
            return minioClient.getPresignedObjectUrl(argsDownload);
//            System.out.println(downloadUrl);
//
//            // Get object response
//            var args = GetObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(fileName)
//                    .build();
//
//            return minioClient.getObject(args);

        } catch (Exception e) {
            throw new Exception("Error while loading file from Minio.");
        }
    }
    public boolean checkIfExists(String fileName, String bucketName)  {
        try {
            // Get signed URL
            var checkExists = StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();
            var exists = minioClient.statObject(checkExists);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void deleteFile(String fileName, String bucketName) throws Exception{
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
