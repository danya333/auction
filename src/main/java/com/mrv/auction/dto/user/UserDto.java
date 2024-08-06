package com.mrv.auction.dto.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mrv.auction.validation.OnCreate;
import com.mrv.auction.validation.OnUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull(message = "Name must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @Length(
            min = 2, max = 20,
            message = "Name length must be more than 2 symbols and smaller than 20 symbols.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String name;

    @NotNull(message = "Surname must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @Length(
            min = 2, max = 20,
            message = "Surname length must be more than 2 symbols and smaller than 20 symbols.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String surname;

    @NotNull(message = "Username must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @Length(
            min = 2, max = 20,
            message = "Username length must be more than 2 symbols and smaller than 20 symbols.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String username;

    @NotNull(message = "Password must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @Length(
            min = 4,
            message = "Password length must be more than 4 symbols",
            groups = {OnCreate.class, OnUpdate.class}
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Transient
    @NotNull(message = "Password confirmation must not be null.", groups = {OnUpdate.class, OnCreate.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordConfirmation;

}
