package com.kikbak.util.rest;


import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;


public class JsonJaxbObjectMapperFactory {

    public static ObjectMapper newObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        final JaxbAnnotationIntrospector ai = new JaxbAnnotationIntrospector();
        final DeserializationConfig deserializeConf = mapper.getDeserializationConfig()
                .with(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE).withAnnotationIntrospector(ai);
        mapper.setDeserializationConfig(deserializeConf);
        final SerializationConfig serializeConf = mapper.getSerializationConfig().withAnnotationIntrospector(ai);
        mapper.setSerializationConfig(serializeConf);
        return mapper;
    }

}
