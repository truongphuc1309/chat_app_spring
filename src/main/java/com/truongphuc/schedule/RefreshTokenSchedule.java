package com.truongphuc.schedule;

import com.truongphuc.entity.RefreshTokenEntity;
import com.truongphuc.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RefreshTokenSchedule {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedRate = 60*1000*60)
    public void cleanExpiredTokens() {
        List<RefreshTokenEntity> expiredTokens = refreshTokenRepository.findAllExpiredToken();
        refreshTokenRepository.deleteAll(expiredTokens);
    }
}
