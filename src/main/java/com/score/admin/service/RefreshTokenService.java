package com.score.admin.service;

import com.score.admin.domain.RefreshToken;
import com.score.admin.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveToken(String jti, String username, long expiresAt) {
        RefreshToken token = new RefreshToken();
        token.setJti(jti);
        token.setUsername(username);
        token.setExpiresAt(expiresAt);
        token.setRevoked(false);
        repository.save(token);
    }

    public boolean isRevoked(String jti) {
        Optional<RefreshToken> opt = repository.findByJti(jti);
        return opt.map(RefreshToken::getRevoked).orElse(true);
    }

    @Transactional
    public void revoke(String jti) {
        repository.findByJti(jti).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }
}