package com.mrv.auction.controller;

import com.mrv.auction.dto.ad.AdDto;
import com.mrv.auction.mappers.AdMapper;
import com.mrv.auction.model.Ad;
import com.mrv.auction.service.AdService;
import com.mrv.auction.validation.OnCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;
    private final AdMapper adMapper;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> createAd(@Validated(OnCreate.class)
                                       @ModelAttribute AdDto adDto,
                                       @RequestParam("images") List<MultipartFile> images) {
        Ad ad = adMapper.toEntity(adDto);
        return new ResponseEntity<>(adService.create(ad, images), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Ad>> getAllAds() {
        List<Ad> ads = adService.getAllAds();
        List<AdDto> adDtos = adMapper.toDto(ads);
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

}
