package com.openclassroom.chatpoc.message.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Le contenu du message est obligatoire.")
    @Size(max = 2000, message = "Le contenu du message ne doit pas dépasser 2000 caractères.")
    private String content;
}
