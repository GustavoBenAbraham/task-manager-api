package com.gustavo.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AceitarConviteRequestDTO {
    @NotBlank(message = "O código do convite é obrigatório")
    private String token;
}
