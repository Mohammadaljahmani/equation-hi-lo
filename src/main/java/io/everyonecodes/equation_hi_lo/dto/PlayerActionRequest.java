package io.everyonecodes.equation_hi_lo.dto;

import io.everyonecodes.equation_hi_lo.domain.ActionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerActionRequest {

    private ActionType actionType;
    private int betAmount;
    private String equation;
    private String target;
}