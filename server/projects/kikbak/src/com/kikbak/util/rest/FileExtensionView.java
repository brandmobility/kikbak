package com.kikbak.util.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

public class FileExtensionView implements View {

    /** Logger for this class */
    private static final Logger LOGGER = Logger.getLogger(FileExtensionView.class);

    /** Used to extract information from a URL */
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

    /** The default file extension when one is not provided explicitly */
    private final String defaultExtension;

    /** Views keyed by file extension */
    private final Map<String, View> viewForFileExt;

    /** Construct with the map of views keyed by file extension */
    public FileExtensionView(final String defaultExtension, final Map<String, View> viewForFileExt) {
        this.defaultExtension = defaultExtension;
        this.viewForFileExt = viewForFileExt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.View#getContentType()
     */
    @Override
    public String getContentType() {
        // Not predetermined
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.View#render(java.util.Map, javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse reponse)
            throws Exception {
        String requestUri = urlPathHelper.getRequestUri(request);
        final String filename = WebUtils.extractFullFilenameFromUrlPath(requestUri);
        String extension = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(extension)) {
            extension = defaultExtension;
        }
        final View view = viewForFileExt.get(extension);
        if (null == view) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("No view for file extension, '" + extension + "'");
            }
        } else {
            view.render(model, request, reponse);
        }
    }
}
