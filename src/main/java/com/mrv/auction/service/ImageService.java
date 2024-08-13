package com.mrv.auction.service;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    Image create(Ad ad, MultipartFile image);

    String upload(MultipartFile image);

    List<byte[]> download(Long id);

    List<String> getImages(Long id);
}
