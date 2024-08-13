package com.mrv.auction.service;

import com.mrv.auction.model.Ad;
import com.mrv.auction.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static java.lang.Thread.sleep;

@Data
@Slf4j
public class Bet implements Runnable {

    private int minPrice; // Актуальная минимальная цена объявления
    private Ad ad;
    private AdService adService;
    private Map<Long, Bet> adsInUse; // Словарь, содержащий все объявления, по которым идут торги
    private User user; // Пользователь, который сделал последнюю ставку (ему принадлежит минимальная цена)

    public Bet(int minPrice, Ad ad, AdService adService, Map<Long, Bet> adsInUse, User user) {
        this.minPrice = minPrice;
        this.ad = ad;
        this.adService = adService;
        this.adsInUse = adsInUse;
        this.user = user;
    }

    // Метод для поднятия ставки
    public synchronized boolean raisePrice(int minPrice, User user) {
        // Проверка, что новая ставка строго больше минимальной цены объявления
        if (this.minPrice < minPrice) {
            log.info("Пользователь {} перебил ставку {} пользователя {}. Минимальная ставка {}",
                    user.getId(), this.minPrice, this.user.getId(), minPrice );
            this.minPrice = minPrice;
            this.user = user;
            adService.changePrice(ad, minPrice);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        try {
            // Задержка потока t на время проведения торгов
            sleep(ad.getTimer());
        } catch (InterruptedException e) {
            e.getMessage();
        }
        // Звершение торгов. Изменение статуса объявления на FINISHED
        adService.changeStatus(ad);
        // Удаление объявления из словаря, содержащего объявления с активными торгами
        adsInUse.remove(ad.getId());
        log.info("Аукцион товара с id {} завершён. Победила ставка {} пользователя с id {}. ({} -> {})",
                ad.getId(), ad.getMinPrice(),  user.getId(), ad.getStartPrice(), ad.getMinPrice());
    }

}
