package com.netcracker.controller;

import com.netcracker.data.model.bean.Path;
import com.netcracker.data.model.User;
import com.netcracker.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for login-page
 * @author prokhorovartem
 */
@RequiredArgsConstructor
@Controller
public class LoginController {

    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";
    private Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserUtil userUtil;

    /**
     * Mapping for adding registration services on login-page
     * @param model model for thymeleaf
     * @return name of thymeleaf template
     */
    @GetMapping("/")
    public String getLoginPage(Model model) {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        Objects.requireNonNull(clientRegistrations).forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "oauth_login";
    }

    /**
     * Mapping for adding information about client on main page
     * @param model model for thymeleaf
     * @param authentication token for access registration service
     * @return name of thymeleaf template
     */
    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            switch (userInfoEndpointUri) {
                case "https://www.googleapis.com/oauth2/v3/userinfo":
                    model.addAttribute("name", Objects.requireNonNull(userAttributes).get("name"));
                    model.addAttribute("picture", userAttributes.get("picture"));
                    userUtil.saveUser("google", userAttributes.get("sub"),
                            userAttributes.get("name"), userAttributes.get("picture"));
                    break;
                case "https://api.github.com/user":
                    model.addAttribute("name", Objects.requireNonNull(userAttributes).get("login"));
                    model.addAttribute("picture", userAttributes.get("avatar_url"));
                    userUtil.saveUser("github", userAttributes.get("id"),
                            userAttributes.get("login"), userAttributes.get("avatar_url"));
                    break;
                case "https://graph.facebook.com/me":
                    model.addAttribute("name", Objects.requireNonNull(userAttributes).get("name"));
                    model.addAttribute("picture", userAttributes.get("picture"));
                    userUtil.saveUser("facebook", userAttributes.get("id"),
                            userAttributes.get("name"), userAttributes.get("picture"));
                    break;
            }
        }

        model.addAttribute("path", new Path());

        return "loginSuccess";
    }
}