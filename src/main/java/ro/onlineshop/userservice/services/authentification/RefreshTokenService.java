package ro.onlineshop.userservice.services.authentification;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.onlineshop.userservice.entities.RefreshToken;
import ro.onlineshop.userservice.repositories.RefreshTokenRepository;
import ro.onlineshop.userservice.repositories.UserRepository;
import ro.onlineshop.userservice.utils.exception.TokenRefreshException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${onlineshop.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenDurationMs);
//        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date tokenExpiryDate = token.getExpiryDate();
        Date currentDate = new Date();

        String tokenExpiryTime = sdf.format(tokenExpiryDate);
        String currentTime = sdf.format(currentDate);

        if (tokenExpiryTime.compareTo(currentTime) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
