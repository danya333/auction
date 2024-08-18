package com.mrv.auction.service.impl;

import com.mrv.auction.service.Bet;
import com.mrv.auction.model.Ad;
import com.mrv.auction.model.Status;
import com.mrv.auction.repository.AdRepository;
import com.mrv.auction.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserService userService;
    private final ImageService imageService;

    // Словарь, содержащий в качестве ключа id объявления и в качестве значения - объект Bet (объявление по которому
    // начались торги)
    private final Map<Long, Bet> adsInUse = new HashMap<>();

    // Метод для поднятия ставки (минимальной цены) на объявление
    @Override
    public String raiseThePrice(Ad ad, Integer price) {
        // Если у объявления статус FINISHED, возвращается уведомление о завершении торгов
        if (ad.getStatus() == Status.FINISHED){
            log.info("Торги по объявлению с id {} уже завершены", ad.getId());
            return "Торги по объявлению завершены";
        }
        // Если по объявлению торги еще не начались (т.е. его нет в словаре adsInUse)
        if(!adsInUse.containsKey(ad.getId())) {
            // Создаётся экземпляр класса Bet
            Bet bet = new Bet(price, ad, this, adsInUse, userService.getCurrentUser());
            // Создается и запускается новый поток t
            Thread t = new Thread(bet);
            t.start();
            log.info("Auction for ad with id: {} is started with the price {}", ad.getId(), price);
            // Для контроля активных торгов, объект bet добавляется в словарь adsInUse
            adsInUse.put(ad.getId(), bet);
            return "The price has been raised to " + price;
        } else{
            // Если по объявлению уже запущены торги и они еще не завершились (т.е. оно есть в словаре adsInUse)
            // выполняется вызов метода raisePrice() у объекта класса Bet()
            boolean raiseResult = adsInUse.get(ad.getId()).raisePrice(price, userService.getCurrentUser());
            // Если ставка строго выше существующей, метод raisePrice() возвращает true
            if(raiseResult) {
                // Устанавливается новая минимальная цена
                this.changePrice(ad, price);
                return "The price of ad " + ad.getName() + " is raised to " + price;
            } else {
                // Иначе в контроллер возвращается сообщение, что ставка не перебита
                return "The price of ad " + ad.getName() + " must be greater than " +
                        adsInUse.get(ad.getId()).getMinPrice();
            }
        }
    }

    // Метод для изменения статуса объявления (данный метод используется при завершении торгов)
    @Override
    public void changeStatus(Ad ad) {
        ad.setStatus(Status.FINISHED);
        adRepository.save(ad);
    }

    // Метод для изменения минимальной цены объявления
    @Override
    public void changePrice(Ad ad, int minPrice) {
        ad.setMinPrice(minPrice);
        adRepository.save(ad);
    }

    @Override
    public Ad getAd(Long id) {
        return adRepository.findById(id).orElseThrow(()-> new NoSuchElementException("No ad found with id: " + id));
    }

    @Override
    public Page<Ad> getAllAds(PageRequest pageRequest) {
        return adRepository.findAllByStatus(Status.ACTIVE, pageRequest)
                .orElseThrow(()-> new NoSuchElementException("No ads found"));
    }


    @Override
    @Transactional
    public Ad create(Ad ad, List<MultipartFile> images) {
        ad.setCreationDate(LocalDateTime.now());
        ad.setStatus(Status.ACTIVE);
        ad.setUser(userService.getCurrentUser());
        ad.setMinPrice(ad.getStartPrice());
        ad = adRepository.save(ad);
        for (MultipartFile image : images) {
            ad.getImages().add(imageService.create(ad, image));
        }
        adRepository.save(ad);
        log.info("Created ad with id: {}", ad.getId());
        return ad;
    }

    @Override
    @Transactional
    public Ad update(Ad ad, List<MultipartFile> images) {
        Ad existingAd = this.getAd(ad.getId());
        existingAd.setName(ad.getName());
        existingAd.setDescription(ad.getDescription());
        existingAd.setStartPrice(ad.getStartPrice());
        existingAd.setTimer(ad.getTimer());
        adRepository.save(existingAd);
        log.info("Updated ad with id: {}", ad.getId());
        return existingAd;
    }

    @Override
    @Transactional
    public String delete(Long id) {
        if(this.getAd(id) == null){
            throw new NoSuchElementException("No ad found with id: " + id);
        }
        if(userService.getCurrentUser().equals(this.getAd(id).getUser())) {
            adRepository.deleteById(id);
            log.info("Deleted ad with id: {}", id);
            return "Deleted ad with id: " + id;
        } else {
            return "You are not allowed to delete this ad";
        }
    }
}
