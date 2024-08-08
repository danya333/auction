package com.mrv.auction.dto.image;

import com.mrv.auction.model.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private Long id;
    private String name;
    private Ad ad;
}
