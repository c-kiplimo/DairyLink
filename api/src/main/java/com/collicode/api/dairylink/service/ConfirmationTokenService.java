package com.collicode.api.dairylink.service;


import com.collicode.api.dairylink.domain.ConfirmationToken;
import com.collicode.api.dairylink.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        ConfirmationToken  confirmedUser = confirmationTokenRepository.findByToken(token).orElseThrow(()->
                new IllegalStateException("Specified Token Not Found!"));

        confirmedUser.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmedUser);
    }
}
