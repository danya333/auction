package com.mrv.auction.dto.ad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mrv.auction.model.Image;
import com.mrv.auction.model.Status;
import com.mrv.auction.model.User;
import com.mrv.auction.validation.OnCreate;
import com.mrv.auction.validation.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdDto {

    @NotNull(message = "Name must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @Length(
            min = 3, max = 20,
            message = "Name length must be more than 2 symbols and smaller than 20 symbols.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String name;
    @NotNull(message = "Description must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @Length(
            min = 5, max = 100,
            message = "Name length must be more than 5 symbols and smaller than 100 symbols.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String description;
    @NotNull(message = "Price must not be null.", groups = {OnUpdate.class, OnCreate.class})
    private Integer startPrice;
    @NotNull(message = "Timer must not be null.", groups = {OnUpdate.class, OnCreate.class})
    private Integer timer;
}
