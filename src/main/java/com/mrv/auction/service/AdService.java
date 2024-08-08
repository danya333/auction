package com.mrv.auction.service;

import com.mrv.auction.model.Ad;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdService {

    String raiseThePrice(Ad ad, Integer price);

    void changeStatus(Ad ad);

    Ad getAd(Long id);

    List<Ad> getAllAds();

    List<Ad> getUserAds();

    Ad create(Ad ad, List<MultipartFile> images);

    Ad update(Ad ad, List<MultipartFile> images);

    void delete(Long id);
}
