package com.kikbak.client.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.kikbak.KikbakTest;
import com.kikbak.client.service.v1.StoryTemplateService;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.jaxb.v2.share.ChannelType;
import com.kikbak.jaxb.v2.share.StoryType;

public class StoryTemplateServiceTest extends KikbakTest {

    private static final String CODE = "code1"; // referral code of existing share
    private static final String ANDROID = "android";
    private static final String IOS = "ios";
    private static final String PC = "pc";

    @Autowired
    StoryTemplateService storyService;

    @Autowired
    ReadOnlyUserDAO roUserDao;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        setupDbFromRes("StoryTemplateServiceTestDB.xml");
    }

    @Test
    public void testStory() {
        checkStory(ChannelType.EMAIL, IOS, true, true);
        checkStory(ChannelType.EMAIL, ANDROID, true, true);
        checkStory(ChannelType.EMAIL, PC, true, true);

        checkStory(ChannelType.SMS, IOS, false, true);
        checkStory(ChannelType.SMS, ANDROID, false, true);

        checkStory(ChannelType.FB, IOS, false, false);
        checkStory(ChannelType.FB, ANDROID, false, false);

        checkStory(ChannelType.YAHOO, PC, true, true);

        checkStory(ChannelType.GMAIL, PC, true, true);

        checkStory(ChannelType.TWITTER, IOS, false, true);
        checkStory(ChannelType.TWITTER, ANDROID, false, true);
        checkStory(ChannelType.TWITTER, PC, false, true);
    }

    private void checkStory(ChannelType channel, String platform, boolean hasSubject, boolean hasBody) {
        StoryType story = storyService.getStory(CODE, platform, channel);
        assertNotNull("Sharing disabled for " + channel + "." + platform, story);
        if (hasBody)
            checkTemplateContent(story.getBody(), channel + "." + platform + ".body");
        if (hasSubject)
            checkTemplateContent(story.getSubject(), channel + "." + platform + ".subject");
        assertFalse(StringUtils.isEmpty(story.getLandingUrl()));
    }

    private void checkTemplateContent(String str, String errInfo) {
        assertFalse("Missing template for " + errInfo, StringUtils.isEmpty(str));
        // check if there is no unresolved %PARAMS%
        assertFalse("Unresolved param in " + errInfo + ":" + str, Pattern.compile("%[A-Za-z_]+%").matcher(str).find());
    }
}
