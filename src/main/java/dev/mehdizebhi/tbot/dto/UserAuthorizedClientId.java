package dev.mehdizebhi.tbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthorizedClientId {

    private String clientRegistrationId;
    private String principalName;
}
