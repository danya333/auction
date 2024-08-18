package com.mrv.auction.service.impl;

import com.mrv.auction.model.*;
import com.mrv.auction.repository.AdRepository;
import com.mrv.auction.service.AdService;
import com.mrv.auction.service.Bet;
import com.mrv.auction.service.ImageService;
import com.mrv.auction.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    AdService adService;

    @InjectMocks
    private AdServiceImpl adServiceImpl;

    private Ad ad;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Name1");
        user.setSurname("Surname");
        user.setUsername("username");
        user.setPassword("password");
        user.setPasswordConfirmation("password");
        user.setRole(Role.USER);

        ad = new Ad();
        ad.setId(1L);
        ad.setName("adName");
        ad.setDescription("adDescription");
        ad.setMinPrice(150);
        ad.setStartPrice(100);
        ad.setCreationDate(LocalDateTime.now());
        ad.setStatus(Status.ACTIVE);
        ad.setUser(user);
    }

    @Test
        // Торги по объявлению уже завершены, т.е. Status.FINISHED
    void raiseThePrice_AuctionAlreadyFinished() {
        ad.setStatus(Status.FINISHED);

        String result = adServiceImpl.raiseThePrice(ad, 200);

        assertEquals("Торги по объявлению завершены", result);
        verify(adService, never()).changePrice(any(Ad.class), anyInt());
        verify(adService, never()).changeStatus(any(Ad.class));
    }

    @Test
        // Торги по объявлению ещё не начинались (первая ставка)
    void raiseThePrice_AuctionNotStartedYet() throws Exception {

        when(userService.getCurrentUser()).thenReturn(user);

        Field field = AdServiceImpl.class.getDeclaredField("adsInUse");
        field.setAccessible(true);

        Map<Long, Bet> adsInUse = (Map<Long, Bet>) field.get(adServiceImpl);

        String result = adServiceImpl.raiseThePrice(ad, 200);

        assertEquals("The price has been raised to 200", result);
        verify(adService, never()).changePrice(any(Ad.class), anyInt());
        assertEquals(1, adsInUse.size());
        assertEquals(200, adsInUse.get(ad.getId()).getMinPrice());
    }

    @Test
        // Торги начаты, торги по объявлению уже нчались, новая цена выше минималной
    void raiseThePrice_AuctionAlreadyStarted_AndPriceRaised() throws Exception {

        Field field = AdServiceImpl.class.getDeclaredField("adsInUse");
        field.setAccessible(true);

        Map<Long, Bet> adsInUse = (Map<Long, Bet>) field.get(adServiceImpl);

        when(userService.getCurrentUser()).thenReturn(user);

        Bet existingBet = new Bet(100, ad, adService, adsInUse, user);
        adsInUse.put(ad.getId(), existingBet);

        String result = adServiceImpl.raiseThePrice(ad, 200);

        assertEquals("The price of ad adName is raised to 200", result);
        verify(adService).changePrice(ad, 200);
        assertEquals(200, existingBet.getMinPrice());
    }

    @Test
        // Торги начаты, торги по объявлению уже нчались, новая цена ниже минимальной
    void raiseThePrice_AuctionAlreadyStarted_AndPriceNotRaised() throws Exception{

        Field field = AdServiceImpl.class.getDeclaredField("adsInUse");
        field.setAccessible(true);

        Map<Long, Bet> adsInUse = (Map<Long, Bet>) field.get(adServiceImpl);

        when(userService.getCurrentUser()).thenReturn(user);

        Bet existingBet = new Bet(200, ad, adService, adsInUse, user);
        adsInUse.put(ad.getId(), existingBet);

        String result = adServiceImpl.raiseThePrice(ad, 150);

        assertEquals("The price of ad adName must be greater than 200", result);
        verify(adService, never()).changePrice(any(Ad.class), anyInt());
        assertEquals(200, existingBet.getMinPrice());
    }


    @Test
    void changeStatus() {
        ad.setStatus(Status.FINISHED);
        when(adRepository.save(ad)).thenReturn(ad);
        adServiceImpl.changeStatus(ad);
        assertEquals(ad.getStatus(), Status.FINISHED);
        verify(adRepository, times(1)).save(ad);
    }

    @Test
    void changePrice() {
        int price = 200;
        ad.setMinPrice(price);
        when(adRepository.save(ad)).thenReturn(ad);
        adServiceImpl.changePrice(ad, price);
        assertEquals(price, ad.getMinPrice());
        verify(adRepository, times(1)).save(ad);
    }

    @Test
    void getAd() {
        Long id = 1L;
        when(adRepository.findById(id)).thenReturn(Optional.of(ad));
        Ad result = adServiceImpl.getAd(id);
        assertEquals("adName", result.getName());
    }

    @Test
    void getAllAds() {

        String size = "10";
        String page = "0";

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        Page<Ad> adPage = new PageImpl<>(List.of(ad), pageable, 10);

        when(adRepository.findAllByStatus(Status.ACTIVE, pageable)).thenReturn(Optional.of(adPage));

        Optional<Page<Ad>> result = adRepository.findAllByStatus(Status.ACTIVE, pageable);

        result.ifPresent(res -> assertEquals("adName", adPage.getContent().get(0).getName()));
        verify(adRepository, times(1)).findAllByStatus(Status.ACTIVE, pageable);
    }

    @Test
    void create() {
        Ad newAd = new Ad();
        newAd.setId(1L);
        newAd.setName("adName");
        newAd.setDescription("adDescription");
        newAd.setMinPrice(100);
        newAd.setStartPrice(100);
        newAd.setCreationDate(LocalDateTime.now());
        newAd.setStatus(Status.ACTIVE);
        newAd.setUser(user);

        MultipartFile image1 = new MockMultipartFile(
                "image1", "image1.jpg", MediaType.IMAGE_PNG_VALUE, "image1".getBytes());
        MultipartFile image2 = new MockMultipartFile(
                "image2", "image2.jpg", MediaType.IMAGE_PNG_VALUE, "image2".getBytes());

        List<MultipartFile> images = List.of(image1, image2);

        Image image = new Image();
        image.setId(1L);
        image.setName("imageName");
        image.setAd(newAd);

        when(userService.getCurrentUser()).thenReturn(user);
        when(adRepository.save(any(Ad.class))).thenReturn(newAd);
        when(imageService.create(any(Ad.class), any(MultipartFile.class))).thenReturn(image);

        Ad result = adServiceImpl.create(ad, images);

        assertNotNull(result);
        assertEquals("adName", result.getName());
        assertEquals(Status.ACTIVE, result.getStatus());
        assertEquals("Name1", result.getUser().getName());
        assertEquals(100, result.getMinPrice());
    }

    @Test
    void update() {
        Long id = 1L;

        MultipartFile image1 = new MockMultipartFile(
                "image1", "image1.jpg", MediaType.IMAGE_PNG_VALUE, "image1".getBytes());
        MultipartFile image2 = new MockMultipartFile(
                "image2", "image2.jpg", MediaType.IMAGE_PNG_VALUE, "image2".getBytes());

        List<MultipartFile> images = List.of(image1, image2);

        ad.setStatus(Status.FINISHED);

        when(adRepository.findById(id)).thenReturn(Optional.of(ad));
        when(adRepository.save(ad)).thenReturn(ad);

        Ad result = adServiceImpl.update(ad, images);

        assertEquals(Status.FINISHED, result.getStatus());
        verify(adRepository, times(1)).save(ad);
    }

    @Test
    void delete() {
        Long id = 1L;
        when(userService.getCurrentUser()).thenReturn(user);
        when(adRepository.findById(id)).thenReturn(Optional.of(ad));

        String result = adServiceImpl.delete(id);
        assertEquals("Deleted ad with id: " + id, result);
        verify(adRepository, times(1)).deleteById(1L);
    }
}