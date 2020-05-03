package com.github.geekuniversity_java_215.cmsbackend.authserver.service;

import com.github.geekuniversity_java_215.cmsbackend.authserver.entities.BlacklistedToken;
import com.github.geekuniversity_java_215.cmsbackend.authserver.repository.BlacklistedTokenRepository;
import com.github.geekuniversity_java_215.cmsbackend.authserver.repository.token.AccessTokenRepository;
import com.github.geekuniversity_java_215.cmsbackend.authserver.repository.token.RefreshTokenRepository;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.UserRole;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.User;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.oauth2.token.AccessToken;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.oauth2.token.RefreshToken;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.oauth2.token.Token;
import com.github.geekuniversity_java_215.cmsbackend.core.services.UserService;
import com.github.geekuniversity_java_215.cmsbackend.protocol.http.OauthResponse;
import com.github.geekuniversity_java_215.cmsbackend.protocol.token.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.geekuniversity_java_215.cmsbackend.core.configuration.SpringConfiguration.ISSUER;

@Service
@Transactional
@Slf4j
public class TokenService {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;


    @Autowired
    public TokenService(JwtTokenService jwtTokenService,
                        UserService userService,
                        AccessTokenRepository accessTokenRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        BlacklistedTokenRepository blacklistedTokenRepository) {

        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }


    /**
     * Create new token, delete previous(on refreshing)
     *
     * @param login login. Also maybe need to add @mail
     * @param refreshToken current refresh_token if available
     * @return
     */
    public OauthResponse issueTokens(String login, RefreshToken refreshToken) {

        //boolean doRefresh = refreshToken != null;

        RefreshToken oldRefreshToken = refreshToken;
        AccessToken accessToken = null;
        //
        String accessTokenString = null;
        String refreshTokenString = null;
        //
        //
        // find user
        User user = userService.findByLogin(login);
        if(user == null) {
            throw new UsernameNotFoundException("User doesn't not exists: " + login);
        }


        // 1. Refresh Token -------------------------------------------------------------------

        long ttl = TokenType.REFRESH.getTtl();
        Instant expiredAt = Instant.now().plusSeconds(ttl);

        refreshToken = new RefreshToken(user, expiredAt);
        refreshToken = refreshTokenRepository.save(refreshToken);

        Set<String> refreshRoles = new HashSet<>(Collections.singletonList(UserRole.REFRESH));

        refreshTokenString = jwtTokenService.createJWT(
                TokenType.REFRESH, refreshToken.getId(), ISSUER, user.getLogin(), refreshRoles, ttl);

        // 2. Access Token ---------------------------------------------------------------------

        ttl = TokenType.ACCESS.getTtl();
        expiredAt = Instant.now().plusSeconds(TokenType.ACCESS.getTtl());
        accessToken = new AccessToken(refreshToken, expiredAt);
        refreshToken.setAccessToken(accessToken);
        accessTokenRepository.save(accessToken);


        Set<String> roles =
                user.getRoles().stream().map(UserRole::getName).collect(Collectors.toSet());

        accessTokenString = jwtTokenService.createJWT(
                TokenType.ACCESS, accessToken.getId(), ISSUER, user.getLogin(), roles, ttl);

        // Delete here deprecated refresh_token and access_token, no blacklisting
        if (oldRefreshToken != null) {
            // will also delete access_token (FK CASCADE)
            delete(oldRefreshToken);
        }
        return new OauthResponse(accessTokenString, refreshTokenString);
    }



    public AccessToken findAccessToken(Long id) {

        AccessToken result = null;

        if (id != null) {
            result = accessTokenRepository.findById(id).orElse(null);
        }
        return result;
    }

    public RefreshToken findRefreshToken(Long id) {

        RefreshToken result = null;

        if (id != null) {
            result = refreshTokenRepository.findById(id).orElse(null);
        }
        return result;
    }


    /**
     * Delete refresh_token and move access_token to blacklist
     * @param refreshToken RefreshToken
     */
    public void revokeRefreshToken(RefreshToken refreshToken) {

        AccessToken accessToken = refreshToken.getAccessToken();
        if (accessToken != null) {
            blacklistedTokenRepository.save(new BlacklistedToken(accessToken.getId(), accessToken.getExpiredAt()));
        }
        // will also delete access_token (FK CASCADE)
        delete(refreshToken);
    }


    /**
     * Delete refresh_tokens and move theirs access_tokens to blacklist
     * @param tokenList RefreshTokenList
     */
    public void revokeRefreshToken(Collection<RefreshToken> tokenList) {

        for (RefreshToken token : tokenList) {
            revokeRefreshToken(token);
        }
    }


    /**
     * Get all blacklisted tokens from position to now
     * <br> Position from is Id in blacklisted_token table, not refresh_token id.
     * <br> Just remember max Id from last query and use it in next query
     * <br> to get new to you blacklisted access_tokens
     * @return Map.Entry<BlacklistTable id, refresh_token id>
     */
    public Map<Long,Long> getBlacklisted(Long from) {

        //ToDo: replace by projection ?
        return blacklistedTokenRepository.findByIdGreaterThanEqual(from).stream()
                .collect(Collectors.toMap(BlacklistedToken::getId, BlacklistedToken::getTokenId));
    }


    public void vacuum() {
        accessTokenRepository.vacuum();
        refreshTokenRepository.vacuum();
        blacklistedTokenRepository.vacuum();
    }


    // ===============================================================

    /**
     * Delete token without any blacklisting
     * @param token token
     */
    private void delete(Token token) {

        try {
            if (token instanceof AccessToken) {
                accessTokenRepository.delete((AccessToken) token);
            } else if (token instanceof RefreshToken) {
                // will also delete access_token due to FK CASCADE
                refreshTokenRepository.delete((RefreshToken) token);
            }
        } catch (Exception e) {
            log.error("delete token error: " + token.toString(), e);
        }
    }










}


//    public void deleteByUser(User user) {
//        refreshTokenRepository.deleteByUser(user);
//    }



//        Long accessS = Instant.now().getEpochSecond() - TokenType.ACCESS.getTtl();
//        Instant access = Instant.ofEpochSecond(accessS);
//
//        Long refreshS = Instant.now().getEpochSecond() - TokenType.REFRESH.getTtl();
//        Instant refresh = Instant.ofEpochSecond(refreshS);
//
//        tokenRepository.vacuum(access, refresh);






//    /**
//     * Return token that client used in bearer authentication (if has)
//     */
//    public Token getTokenUsedInAuthentication() {
//        return findById(getAuthTokenId());
//    }



//    /**
//     * Verify token type (if exists)
//     * <br> Token retrieved from session (originated from authentication)
//     * @param tokenType TokenType
//     * @param role role, issued to token
//     * @Throw AccessDeniedException if conditions failed. do nothing if token not exists.
//     */
//    public Long checkTokenAuthorization(TokenType tokenType, Set<String> roles) {
//
//        Long result = null;
//
//        ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
//        HttpSession session = attr.getRequest().getSession();
//
//        Claims claims = (Claims)session.getAttribute("claims");
//
//        // user has authorised by TOKEN - check it
//        if (claims != null) {
//
//            result = Long.valueOf(claims.getId());
//            Set<String> authorities = rolesToSet(claims.get("authorities"));
//
//            // token type and role
//            if (!claims.get("type").equals(tokenType.getName()) ||
//                !authorities.containsAll(roles)) {
//
//                throw new AccessDeniedException("Token unauthorized");
//            }
//
//        }
//
//        return result;
//    }







////user = new User(userDetails.getUsername(), "[PROTECTED]", authorities);
//        cUser = userService.findByName(userDetails.getUsername()).orElseThrow(
//                () -> new RuntimeException("User not exists: " + userDetails.getUsername()));
//
//
//
//
//
////cuser.setRolesEx(userDetails.getAuthorities());
//
////authorities = new ArrayList<>(userDetails.getAuthorities());
////user = new User(userDetails.getUsername(), "[PROTECTED]", authorities);

//authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "REFRESH"));


//    public boolean isTokenEnabled(Long id) {
//
//        AtomicBoolean result = new AtomicBoolean(false);
//        if (id != null) {
//            tokenRepository.findById(id).ifPresent(token -> result.set(token.isEnabled()));
//        }
//        return result.get();
//    }



