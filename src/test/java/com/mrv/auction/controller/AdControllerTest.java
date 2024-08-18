package com.mrv.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrv.auction.dto.ad.AdDto;
import com.mrv.auction.mappers.AdMapper;
import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Status;
import com.mrv.auction.model.User;
import com.mrv.auction.service.AdService;
import com.mrv.auction.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class AdControllerTest {

    @Mock
    AdService adService;

    @Mock
    ImageService imageService;

    @Mock
    AdMapper adMapper;


    @InjectMocks
    private AdController adController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adController).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    void createAd() throws Exception {
        Ad ad = new Ad();
        ad.setId(1L);
        ad.setMinPrice(100);
        ad.setTimer(1000);
        ad.setStatus(Status.ACTIVE);
        ad.setName("Ad1");
        ad.setStartPrice(100);
        ad.setDescription("Description1");
        ad.setCreationDate(LocalDateTime.now());
        ad.setUser(new User());

        AdDto adDto = new AdDto();
        adDto.setName("Ad1");
        adDto.setDescription("Description1");
        adDto.setStartPrice(100);
        adDto.setTimer(1000);

        MultipartFile image1 = new MockMultipartFile(
                "image1", "image1.jpg", MediaType.IMAGE_PNG_VALUE, "image1".getBytes());
        MultipartFile image2 = new MockMultipartFile(
                "image2", "image2.jpg", MediaType.IMAGE_PNG_VALUE, "image2".getBytes());


        when(adMapper.toEntity(adDto)).thenReturn(ad);
        when(adService.create(any(), any())).thenReturn(ad);

        mockMvc.perform(multipart("/api/ads/create")
                        .file((MockMultipartFile) image1)
                        .file((MockMultipartFile) image2)
                        .param("name", adDto.getName())
                        .param("description", adDto.getDescription())
                        .param("startPrice", String.valueOf(adDto.getStartPrice()))
                        .param("timer", String.valueOf(adDto.getTimer()))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startPrice").value(100))
                .andExpect(jsonPath("$.timer").value(1000))
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.name").value("Ad1"));
    }


    @Test
    void getAllAds() throws Exception {
        Ad ad = new Ad();
        ad.setId(1L);
        ad.setMinPrice(100);
        ad.setTimer(1000);
        ad.setStatus(Status.ACTIVE);
        ad.setName("Ad1");
        ad.setStartPrice(100);
        ad.setDescription("Description1");
        ad.setCreationDate(LocalDateTime.now());
        ad.setUser(new User());
        ad.setImages(Collections.emptyList());

        String sortDirection = "asc";
        String sortBy = "name";
        String size = "10";
        String page = "0";

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));

        Page<Ad> adPage = new PageImpl<>(List.of(ad), pageable, 10);
        when(adService.getAllAds(any())).thenReturn(adPage);

        mockMvc.perform(get("/api/ads/list")
                        .param("page", page)
                        .param("size", size)
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Ad1"))
                .andExpect(jsonPath("$.content[0].description").value("Description1"))
                .andExpect(jsonPath("$.content[0].startPrice").value(100))
                .andExpect(jsonPath("$.content[0].minPrice").value(100))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].timer").value(1000));

        verify(adService, times(1)).getAllAds(any());
    }

    @Test
    void raisePrice() throws Exception {
        Ad ad = new Ad();
        ad.setId(1L);
        ad.setMinPrice(100);
        ad.setTimer(1000);
        ad.setStatus(Status.ACTIVE);
        ad.setName("Ad1");
        ad.setStartPrice(100);
        ad.setDescription("Description1");
        ad.setCreationDate(LocalDateTime.now());
        ad.setUser(new User());
        ad.setImages(Collections.emptyList());

        Long id = 1L;
        int price = 100;

        when(adService.getAd(id)).thenReturn(ad);
        when(adService.raiseThePrice(any(), any())).thenReturn("Price was raised");

        mockMvc.perform(post("/api/ads/{id}/raise", id)
                .contentType(MediaType.APPLICATION_JSON)
                .param("price", String.valueOf(price)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Price was raised"));

        verify(adService, times(1)).raiseThePrice(any(), any());
    }

    @Test
    void downloadImages() throws Exception {
        byte[] image1 = {1, 1, 1};
        byte[] image2 = {7, 7, 7};

        List<byte[]> images = new ArrayList<>();
        images.add(image1);
        images.add(image2);

        Long id = 1L;

        String base64Image1 = Base64.getEncoder().encodeToString(image1);
        String base64Image2 = Base64.getEncoder().encodeToString(image2);

        when(imageService.download(id)).thenReturn(images);

        mockMvc.perform(get("/api/ads/{id}/images", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(base64Image1))
                .andExpect(jsonPath("$[1]").value(base64Image2));

        verify(imageService, times(1)).download(1L);
    }

    @Test
    void deleteAd() throws Exception {
        Long adId = 1L;
        mockMvc.perform(delete("/api/ads/{id}", adId))
                .andExpect(status().isOk());
        verify(adService, times(1)).delete(adId);
    }
}