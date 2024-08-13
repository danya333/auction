package com.mrv.auction.service.impl;

import com.mrv.auction.exception.FileDownloadException;
import com.mrv.auction.exception.FileUploadException;
import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Image;
import com.mrv.auction.repository.ImageRepository;
import com.mrv.auction.service.ImageService;
import com.mrv.auction.service.props.MinioProperties;
import io.minio.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public Image create(Ad ad, MultipartFile image) {
        Image newImage = new Image();
        newImage.setAd(ad);
        newImage.setName(this.upload(image));
        imageRepository.save(newImage);
        return newImage;
    }

    @Override
    @Transactional
    public String upload(MultipartFile file) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new FileUploadException("Image upload failed: "
                    + e.getMessage());
        }
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new FileUploadException("Image must have name.");
        }
        String imageName = generateImageName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            throw new FileUploadException("Image upload failed: "
                    + e.getMessage());
        }
        saveImage(inputStream, imageName);
        return imageName;
    }

    @Override
    public List<byte[]> download(Long id){
        List<String> images = this.getImages(id);
        List<byte[]> fileContents = new ArrayList<>();
        for (String imageName : images) {
            try {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket("auction")
                                .object(imageName)
                                .build());

                byte[] bytes = stream.readAllBytes();
                fileContents.add(bytes);
                stream.close();
            } catch (Exception e) {
                throw new FileDownloadException("Image download failed: " + e.getMessage());
            }
        }
        return fileContents;
    }

    @Override
    public List<String> getImages(Long id) {
        List<Image> images = imageRepository.findAllByAdId(id)
                .orElseThrow(() -> new NoSuchElementException("Images not found"));
        return images.stream().map(Image::getName).toList();
    }


    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    private String generateImageName(MultipartFile file) {
        String imageName = file.getOriginalFilename().replace(' ', '_');
        return UUID.randomUUID() + "_" + imageName;
    }


    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucket())
                .object(fileName)
                .build());
    }



}
