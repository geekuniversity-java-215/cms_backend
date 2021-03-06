package com.github.geekuniversity_java_215.cmsbackend.core.entities.oauth2.token;

import com.github.geekuniversity_java_215.cmsbackend.core.entities.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Entity
@Table(indexes = {
        @Index(name = "idx_refresh_token_created", columnList = "created"),
        @Index(name = "idx_refresh_token_expired_at", columnList = "expired_at")})
public class RefreshToken extends Token {

    // link from refresh token to access token
    @OneToOne(mappedBy = "refreshToken", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private AccessToken accessToken;

    @NotNull
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="id")
    protected User user;

    protected RefreshToken() {}

    public RefreshToken(@NotNull User user, @NotNull Instant expiredAt) {
        super(expiredAt);
        this.user = user;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", expiredAt=" + expiredAt +
                ", user=" + user +
                ", accessToken=" + accessToken +
                '}';
    }
}
