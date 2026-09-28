package com.gustavo.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriarConviteRequestDTO {
    @NotBlank(message = "O e-mail da gestora financeira é obrigatório")
    @Email(message = "Informe um e-mail válido")
    @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres")
    private String email;
}
