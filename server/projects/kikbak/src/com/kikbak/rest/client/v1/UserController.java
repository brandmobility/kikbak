package com.kikbak.rest.client.v1;

import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.FbLoginService;
import com.kikbak.client.service.UserService;
import com.kikbak.client.service.impl.CookieAuthenticationFilter;
import com.kikbak.jaxb.v1.devicetoken.DeviceTokenUpdateRequest;
import com.kikbak.jaxb.v1.devicetoken.DeviceTokenUpdateResponse;
import com.kikbak.jaxb.v1.friends.UpdateFriendResponse;
import com.kikbak.jaxb.v1.friends.UpdateFriendsRequest;
import com.kikbak.jaxb.v1.offer.ClientOfferType;
import com.kikbak.jaxb.v1.offer.GetUserOffersRequest;
import com.kikbak.jaxb.v1.offer.GetUserOffersResponse;
import com.kikbak.jaxb.v1.offer.HasUserOffersResponse;
import com.kikbak.jaxb.v1.register.RegisterUserRequest;
import com.kikbak.jaxb.v1.register.RegisterUserResponse;
import com.kikbak.jaxb.v1.register.RegisterUserResponseStatus;
import com.kikbak.jaxb.v1.register.UserIdType;
import com.kikbak.jaxb.v1.register.UserType;
import com.kikbak.jaxb.v1.statustype.SuccessStatus;
import com.kikbak.jaxb.v1.userlocation.UserLocationType;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {

    @Autowired
    protected UserService userService;

    @Autowired
    private FbLoginService fbLoginService;

    @Autowired
    private PropertiesConfiguration config;

    static final String REFERRAL_CODE_KEY = "rc";

    private static final String USER_COOKIE_SECURE = "user.cookie.secure";

    private static final int COOKIE_EXPIRE_TIME = 10 * 365 * 24 * 60 * 60;

    private static final String USER_FRIENDS_MINIMUM_COUNT = "user.friends.minimum.count";

    private static final Logger logger = Logger.getLogger(UserController.class);

    @RequestMapping(value = "/register/fb/", method = RequestMethod.POST)
    public RegisterUserResponse registerFacebookUser(@RequestBody RegisterUserRequest request,
            final HttpServletResponse httpResponse) {
        try {
            RegisterUserResponse response = new RegisterUserResponse();
            response.setStatus(RegisterUserResponseStatus.OK);
            UserType user = fbLoginService.getUserInfo(request.getUser().getAccessToken());
            Collection<Long> friends = fbLoginService.getFriends(request.getUser().getAccessToken());

            if (friends.size() < config.getInt(USER_FRIENDS_MINIMUM_COUNT)) {
                logger.error("User " + user.getId() + " has too few friends: " + friends.size());
                response.setStatus(RegisterUserResponseStatus.TOO_FEW_FRIENDS);
                return response;
            }

            UserIdType userId = userService.registerUser(user);
            userService.updateFriendsList(userId.getUserId(), friends);

            response.setUserId(userId);
            String token = userService.getUserToken(userId.getUserId());
            Cookie cookie = new Cookie(CookieAuthenticationFilter.COOKIE_TOKEN_KEY, token);
            if (config.getBoolean(USER_COOKIE_SECURE)) {
                cookie.setSecure(true);
            }
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_EXPIRE_TIME);
            httpResponse.addCookie(cookie);

            cookie = new Cookie(CookieAuthenticationFilter.COOKIE_USER_ID_KEY, Long.toString(userId.getUserId()));
            if (config.getBoolean(USER_COOKIE_SECURE)) {
                cookie.setSecure(true);
            }
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_EXPIRE_TIME);
            httpResponse.addCookie(cookie);

            return response;
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error(e, e);
            return null;
        }
    }

    @RequestMapping(value = "/friends/fb/{userId}", method = RequestMethod.POST)
    public UpdateFriendResponse updateFriends(@PathVariable("userId") Long userId,
            @RequestBody UpdateFriendsRequest request, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            String accessToken = request.getAccessToken();
            Collection<Long> friends = fbLoginService.getFriends(accessToken);
            userService.updateFriendsList(userId, friends);

            UpdateFriendResponse response = new UpdateFriendResponse();
            response.setStatus(SuccessStatus.OK);
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/offer/{userId}/{merchantName}", method = RequestMethod.GET)
    public GetUserOffersResponse offersRequest(@PathVariable("userId") Long userId,
            @PathVariable("merchantName") String merchantName, final HttpServletResponse httpResponse) {
        try {
            if (StringUtils.isBlank(merchantName)) {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
            GetUserOffersResponse response = new GetUserOffersResponse();
            response.getOffers().addAll(userService.getOffers(userId, merchantName));
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/offer/{userId}", method = RequestMethod.POST)
    public GetUserOffersResponse offersRequest(@PathVariable("userId") Long userId,
            @RequestBody GetUserOffersRequest request, final HttpServletResponse httpResponse) {
        try {
            GetUserOffersResponse response = new GetUserOffersResponse();
            response.getOffers().addAll(userService.getOffers(userId, request.getUserLocation()));
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
    
    @RequestMapping(value = "/hasoffer/{longitude}/{latitude}", method = RequestMethod.OPTIONS)
    public HasUserOffersResponse hasOffersRequestOptions(@PathVariable("longitude") int longitude, @PathVariable("latitude") int latitude,
    		final HttpServletResponse httpResponse) {
        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");
        return null;
    }

    @RequestMapping(value = "/hasoffer/{longitude}/{latitude}", method = RequestMethod.GET)
    public HasUserOffersResponse hasOffersRequest(@PathVariable("longitude") int longitude, @PathVariable("latitude") int latitude,
    		final HttpServletResponse httpResponse) {
        try {
            httpResponse.addHeader("Access-Control-Allow-Origin", "*");
            httpResponse.addHeader("Access-Control-Allow-Methods", "GET");
            httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");
            
            HasUserOffersResponse response = new HasUserOffersResponse();
            UserLocationType location = new UserLocationType();
            location.setLatitude(latitude / 10000000.0);
            location.setLongitude(longitude / 10000000.0);
            Collection<ClientOfferType> offers = userService.hasOffers(location);
            if (offers.isEmpty()) {
            	response.setHasOffer(false);
            	return response;
            }
            response.setHasOffer(true);
            if (offers.size() == 1) {
            	response.setBrandName(offers.iterator().next().getMerchantName());
            }
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/devicetoken/{userId}", method = RequestMethod.POST)
    public DeviceTokenUpdateResponse deviceTokenUpdate(@PathVariable("userId") Long userId,
            @RequestBody DeviceTokenUpdateRequest request, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);
            userService.persistDeviceToken(userId, request.getToken());

            DeviceTokenUpdateResponse response = new DeviceTokenUpdateResponse();
            response.setStatus(SuccessStatus.OK);
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
