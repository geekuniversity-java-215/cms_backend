package com.github.geekuniversity_java_215.cmsbackend.authserver.controllers;

import com.github.geekuniversity_java_215.cmsbackend.authserver.configurations.AuthType;
import com.github.geekuniversity_java_215.cmsbackend.authserver.configurations.RequestScopeBean;
import com.github.geekuniversity_java_215.cmsbackend.authserver.configurations.aop.ValidAuthenticationType;
import com.github.geekuniversity_java_215.cmsbackend.authserver.service.TokenService;
import com.github.geekuniversity_java_215.cmsbackend.core.entities.oauth2.token.RefreshToken;
import com.github.geekuniversity_java_215.cmsbackend.core.services.user.UserService;
import com.github.geekuniversity_java_215.cmsbackend.oauth_protocol.protocol.OauthResponse;
import com.github.geekuniversity_java_215.cmsbackend.oauth_protocol.protocol.BlackListResponse;
import com.github.geekuniversity_java_215.cmsbackend.utils.data.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


// handmadezz oauzz serverz v0.3 - старые взгляды сквозь новые дыры


@RestController
@RequestMapping("/oauzz/token/")
@Slf4j
public class OauthController {

    private final TokenService tokenService;
    private final RequestScopeBean requestScopeBean;


    public OauthController(TokenService tokenService,
        RequestScopeBean requestScopeBean) {

        this.tokenService = tokenService;
        this.requestScopeBean = requestScopeBean;
    }




    /**
     * Obtain new access and refresh tokens
     * <br>Allowed Basic Authorization only
     */
    @PostMapping( value = "/get")
    @ValidAuthenticationType(AuthType.BASIC_AUTH)
    @Secured({UserRole.VAL.USER, UserRole.VAL.ADMIN})
    public ResponseEntity<?> getToken() {
        try {
            OauthResponse result;
            result = tokenService.issueTokens(UserService.getCurrentUsername(), null);
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            log.error("Adding new user error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                + ": " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Refresh tokens by refresh_token
     * <br> Allow Bearer Authorization by refresh_token only
     */
    @PostMapping(value = "/refresh")
    @ValidAuthenticationType(AuthType.REFRESH_TOKEN)
    @Secured(UserRole.VAL.REFRESH)
    public ResponseEntity<?> refreshToken() {

        try {

            OauthResponse result;
            RefreshToken refreshToken = requestScopeBean.getRefreshToken();

            // Выдается пара токенов ACCESS + REFRESH
            result = tokenService.issueTokens(UserService.getCurrentUsername(), refreshToken);

            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            log.error("Adding new user error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                + ": " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Return blacklisted access_token list
     * <br> Allow Bearer access_token Authorization
     * @param from from that index to last available
     * @return List of denied token id
     */
    @PostMapping(value = "/listblack")
    @ValidAuthenticationType({AuthType.ACCESS_TOKEN})
    @Secured({UserRole.VAL.RESOURCE, UserRole.VAL.ADMIN})
    public ResponseEntity<?> getBlackList(@Param("from") Long from) {

        try {

            BlackListResponse result = new BlackListResponse();
            result.setList(tokenService.getBlacklisted(from));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Adding new user error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                + ": " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/test")
    @ValidAuthenticationType({AuthType.BASIC_AUTH, AuthType.ACCESS_TOKEN})
    @Secured({UserRole.VAL.USER, UserRole.VAL.ADMIN, UserRole.VAL.RESOURCE})
    public ResponseEntity<String> hello() {
        return  ResponseEntity.ok("SERVLET CONTAINER GRRREET YOU!");
    }


    // ==============================================================================


//    // get current user name
//    private static String getUsername() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        UserDetailsCustom userDetails = (UserDetailsCustom)principal;
//        //UserDetails userDetails = (UserDetails)principal;
//        return userDetails.getUsername();
//    }

}













    // ---------------------------------------------------------------------------------------


//    private static UsernamePasswordAuthenticationToken getAuthentication() {
//
//        UsernamePasswordAuthenticationToken result = null;
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication instanceof UsernamePasswordAuthenticationToken) {
//            result = (UsernamePasswordAuthenticationToken)authentication;
//        }
//        return result;
//    }



//    // get current session
//    private static HttpSession getSession() {
//        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        return attr.getRequest().getSession(); // true == allow create
//    }


//    /**
//     * Return token id that user used in authentication
//     * <br>If has one
//     */
//    private Long getUsedInAuthenticationTokenId() {
//
//        Long result = null;
//
//        ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
//        HttpSession session = attr.getRequest().getSession();
//        Claims claims = (Claims)session.getAttribute("claims");
//
//        if (claims != null) {
//            result = Long.valueOf(claims.getId());
//        }
//        return result;
//    }


//    /**
//     * Return token that user used in authentication
//     * <br>If has one and token is permitted by server
//     */
//    private Token getUsedInAuthenticationToken() {
//        return tokenService.findById(getUsedInAuthenticationTokenId());
//    }



//    /**
//     * Check token type(if token present)
//     */
//    private void  checkTokenType(Token token, TokenType type) {
//        if (token != null && token.getType() != type) {
//            log.info("Wrong token type was: {}, should be: {}", token.getType(), type);
//            throw new AccessDeniedException("Unauthenticated");
//        }
//    }
//
//
//    /**
//     * Check tokem presence
//     */
//    private void  checkTokenPresent(Token token) {
//        if (token == null) {
//            log.info("Token should be present, but it was null");
//            throw new AccessDeniedException("Unauthenticated");
//        }
//    }









// ---------------------------------------------------------------------------------------


// @RequestParam("grant_type") String grantTypeStr
//GrantType grantType = GrantType.parse(grantTypeStr); // ??


//    /**
//     * Check that token, used in authentication has correct type
//     * @param type TokenType
//     * @return Token
//     */
//    private Token checkTokenType(TokenType type) {
//
//        Token result = tokenService.findById(getUsedInAuthenticationTokenId());
//        // checking token type
//        if (result != null && result.getType() != type) {
//            // Wrong token type
//            throw new AccessDeniedException("Unauthenticated");
//        }
//        return result;
//    }



// SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//final UserDetails userDetails = userDetailsService.loadUserByUsername(userDetailsContext.getUsername());
