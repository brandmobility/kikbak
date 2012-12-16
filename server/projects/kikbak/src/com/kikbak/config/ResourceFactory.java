package com.kikbak.config;

import java.io.File;
import java.io.IOException;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

public class ResourceFactory implements ResourceLoaderAware {

    /** The path to the resource based on Spring's notation for specifying resources */
    private final String location;

    /** The resource loader from the application context */
    private ResourceLoader rsrcLoader;

    /**
     * Construct with the location of the resource using Spring's resource location notation.
     * 
     * @param location the location of the resource using Spring's resource location notation
     */
    public ResourceFactory(final String location) {
        this.location = location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.rsrcLoader = resourceLoader;
    }

    /**
     * Returns a resource as a file.
     * 
     * @return the resource as a file
     * 
     * @throws IOException thrown when the resource is not found at the provided location
     */
    public File getFile() throws IOException {
        final Resource rsrc = rsrcLoader.getResource(location);
        if (null == rsrc) {
            throw new IOException("No resource found at location, '" + location + "'");
        }
        return rsrc.getFile();
    }

}
