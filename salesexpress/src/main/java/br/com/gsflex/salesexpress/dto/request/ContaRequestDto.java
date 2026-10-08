package br.com.gsflex.salesexpress.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaRequestDto(@NotBlank(message = "Informe a senha atual")
                              String senhaAtual,

                              String novoUsuario,

                              @Size(min = 6, message = "A nova senha deve ter ao menos 6 caracteres")
                              String novaSenha) {
}
