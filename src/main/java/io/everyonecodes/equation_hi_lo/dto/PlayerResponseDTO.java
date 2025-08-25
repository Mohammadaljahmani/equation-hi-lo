package io.everyonecodes.equation_hi_lo.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlayerResponseDTO {

    private Long id;
    private String username;

    public PlayerResponseDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}