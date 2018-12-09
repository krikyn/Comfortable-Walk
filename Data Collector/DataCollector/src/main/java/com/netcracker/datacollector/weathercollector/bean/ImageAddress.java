package com.netcracker.datacollector.weathercollector.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Class for saving radar image address
 *
 * @author Kirill.Vakhrushev
 */
@Getter
@Setter
public class ImageAddress {
    private String imageURI;
    private String imageURL;
}
