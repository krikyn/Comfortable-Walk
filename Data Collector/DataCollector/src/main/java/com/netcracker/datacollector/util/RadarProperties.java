package com.netcracker.datacollector.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Class for accessing constants participating in the radar data collector
 */
@Configuration
@Data
public class RadarProperties {

    @Value("${spring.radar.radar-url}")
    private String radarURL;

    @Value("${spring.radar.image-uri-prefix}")
    private String imageURIPrefix;

    @Value("${spring.radar.image-uri-postfix}")
    private String imageURIPostfix;

}
