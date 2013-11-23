package com.kikbak.client.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kikbak.client.service.v1.StoryTemplateService;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlySharedDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.Shared;
import com.kikbak.dto.User;
import com.kikbak.jaxb.v2.share.ChannelType;
import com.kikbak.jaxb.v2.share.StoryType;

@Service
public class StoryTemplateServiceImpl implements StoryTemplateService {

    @Autowired
    private PropertiesConfiguration config;

    @Autowired
    @Qualifier("ReadOnlySharedDAO")
    ReadOnlySharedDAO roSharedDao;

    @Autowired
    private ReadOnlyGiftDAO roGiftDAO;

    @Autowired
    private ReadOnlyUserDAO roUserDAO;

    @Autowired
    private ReadOnlyLocationDAO roLocationDAO;

    @Autowired
    private ReadOnlyMerchantDAO roMerchantDAO;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public StoryType getStory(String code, String platform, ChannelType channel) {
        Shared share = roSharedDao.findByReferralCode(code);
        if (share == null)
            throw new IllegalArgumentException("No share for given code");

        HashMap<String, String> rmap = getReplacmentMap(share);
        StoryType story = getStory(platform, channel, rmap);
        return story;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<StoryType> getStories(String code, String platform) {
        Shared share = roSharedDao.findByReferralCode(code);
        if (share == null)
            throw new IllegalArgumentException("No share for given code");

        HashMap<String, String> rmap = getReplacmentMap(share);
        List<StoryType> stories = new ArrayList<StoryType>();
        for (ChannelType channel : ChannelType.values()) {
            if (isEnabled(platform, channel)) {
                StoryType story = getStory(platform, channel, rmap);
                stories.add(story);
            }
        }
        return stories;
    }

    private boolean isEnabled(String platform, ChannelType channel) {
        String validKey = "share.template." + channel.name().toLowerCase() + "." + platform.toLowerCase();
        return config.getBoolean(validKey, false);
    }

    private StoryType getStory(String platform, ChannelType channel, Map<String, String> replacmentMap) {
        String subjectKey = "share.template." + channel.name().toLowerCase() + "." + platform.toLowerCase()
                + ".subject";
        String subjectTemplate = config.getString(subjectKey, null);
        String subject = fillTemplate(subjectTemplate, replacmentMap);

        String bodyKey = "share.template." + channel.name().toLowerCase() + "." + platform.toLowerCase() + ".body";
        String bodyTemplate = config.getString(bodyKey, null);
        String body = fillTemplate(bodyTemplate, replacmentMap);

        String loginUrl = getLoginUrl(replacmentMap.get("%CODE%"));

        StoryType story = new StoryType();
        story.setSubject(subject);
        story.setBody(body);
        story.setLandingUrl(loginUrl);
        story.setType(channel);
        return story;
    }

    private String fillTemplate(String template, Map<String, String> replecmentMap) {
        if (template == null)
            return null;

        for (String key : replecmentMap.keySet()) {
            template = template.replace(key, replecmentMap.get(key));
        }
        return template;
    }

    /**
     * 
     * %ADDRESS% <br>
     * %CAPTION% <br>
     * %CODE% <br>
     * %DESC% <br>
     * %DESC_DETAIL% <br>
     * %EMPLOYEE_ID% <br>
     * %IMAGE_URL% <br>
     * %LOGIN_URL% <br>
     * %MERCHANT% <br>
     * %NAME% <br>
     */
    private HashMap<String, String> getReplacmentMap(Shared share) {
        Gift gift = roGiftDAO.findByOfferId(share.getOfferId());
        User user = roUserDAO.findById(share.getUserId());
        Merchant merchant = roMerchantDAO.findById(share.getMerchantId());
        Location location = share.getLocationId() == null ? null : roLocationDAO.findById(share.getLocationId());

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("%ADDRESS%", getAddress(location));
        map.put("%CAPTION%", StringUtils.trimToEmpty(share.getCaption()));
        map.put("%CODE%", StringUtils.trimToEmpty(share.getReferralCode()));
        map.put("%DESC%", StringUtils.trimToEmpty(gift.getDescription()));
        map.put("%DESC_DETAIL%", StringUtils.trimToEmpty(gift.getDetailedDesc()));
        map.put("%EMPLOYEE_ID%", StringUtils.trimToEmpty(share.getEmployeeId()));
        map.put("%IMAGE_URL%", StringUtils.trimToEmpty(share.getImageUrl()));
        map.put("%LOGIN_URL%", getLoginUrl(share.getReferralCode()));
        map.put("%MERCHANT%", StringUtils.trimToEmpty(merchant.getName()));
        map.put("%NAME%", getUserName(user));
        return map;
    }

    private String getAddress(Location location) {
        if (location == null)
            return StringUtils.EMPTY;

        String address = StringUtils.trimToEmpty(location.getAddress1())
                + (StringUtils.isBlank(location.getAddress2()) ? "" : " " + location.getAddress2()) + ", "
                + StringUtils.trimToEmpty(location.getCity()) + ", " + StringUtils.trimToEmpty(location.getState());
        return address;
    }

    private String getLoginUrl(String code) {
        return config.getString("share.template.login.url").replace("%CODE%", code);
    }

    private String getUserName(User user) {
        String name = StringUtils.trimToEmpty(user.getFirstName());
        if (!StringUtils.isEmpty(name))
            return name;

        name = StringUtils.trimToEmpty(user.getLastName());
        if (!StringUtils.isEmpty(name))
            return name;

        name = StringUtils.trimToEmpty(user.getManualName());
        return name;
    }

}
