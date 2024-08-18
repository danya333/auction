package com.mrv.auction.controller;

import com.mrv.auction.dto.ad.AdDto;
import com.mrv.auction.mappers.AdMapper;
import com.mrv.auction.model.Ad;
import com.mrv.auction.service.AdService;
import com.mrv.auction.service.ImageService;
import com.mrv.auction.validation.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@Tag(name = "ad-controller",
        description = "Методы данного контроллера позволяют создать объявление, обновить и сделать ставку")
public class AdController {

    private final AdService adService;
    private final AdMapper adMapper;
    private final ImageService imageService;

    @Operation(summary = "Создать объявление",
            description = "Данный метод позволяет создать новое объявление." +
                    "Созданное объявление будет иметь статус ACTIVE и доступено в общем списке")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> createAd(@Validated(OnCreate.class)
                                       @ModelAttribute AdDto adDto,
                                       @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        Ad ad = adMapper.toEntity(adDto);
        return new ResponseEntity<>(adService.create(ad, images), HttpStatus.CREATED);
    }


    @Operation(summary = "Получить список доступных объявлений",
            description = "Данный метод позволяет отображать объявления со статусом ACTIVE с применением сортировки")
    @GetMapping("/list")
    public ResponseEntity<Page<Ad>> getAllAds(@Parameter(description = "Номер страницы сортировки")
                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                              @Parameter(description = "Количество элементов на странице")
                                              @RequestParam(name = "size", defaultValue = "10") int size,
                                              @Parameter(description = "Параметр сортировки")
                                              @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
                                              @Parameter(description = "Направление сортировки")
                                              @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Ad> ads = adService.getAllAds(pageRequest);
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }


    @Operation(summary = "Сделать ставку",
            description = "Данный метод позволяет сделать ставку на объявление. " +
                    "Принимаются ставки (цены), значение которых строго выше минимальной ставки (цены)")
    @PostMapping("/{id}/raise")
    public ResponseEntity<String> raisePrice(@Parameter(description = "id объявления со статусом ACTIVE")
                                             @PathVariable("id") Long id,
                                             @Parameter(description = "Новая ставка (цена)")
                                             @RequestParam Integer price) {
        Ad ad = adService.getAd(id);
        return new ResponseEntity<>(adService.raiseThePrice(ad, price), HttpStatus.OK);
    }


    @Operation(summary = "Получить изображения",
            description = "Данный метод позволяет подгрузить список byte-массивов изображений объявления по его id")
    @GetMapping("/{id}/images")
    public ResponseEntity<List<byte[]>> downloadImages(@Parameter(description = "id объявления")
                                                       @PathVariable("id") Long id) {
        List<byte[]> response = imageService.download(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Удалить объявление",
            description = "Данный метод позволяет удалить объявление. Удалить объявление может только тот пользователь," +
                    "который его создал")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAd(@Parameter(description = "id объявления")
                                           @PathVariable("id") Long id) {
        return new ResponseEntity<>(adService.delete(id), HttpStatus.OK);
    }
}
