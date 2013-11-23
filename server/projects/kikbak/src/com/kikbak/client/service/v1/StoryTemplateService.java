package com.kikbak.client.service.v1;

import java.util.List;

import com.kikbak.jaxb.v2.share.ChannelType;
import com.kikbak.jaxb.v2.share.StoryType;

public interface StoryTemplateService {
    StoryType getStory(String code, String platform, ChannelType channel);

    List<StoryType> getStories(String code, String platform);
}
