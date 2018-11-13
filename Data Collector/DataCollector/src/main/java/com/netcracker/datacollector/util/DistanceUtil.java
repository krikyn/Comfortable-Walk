package com.netcracker.distancecollector.util;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrixElement;

import java.io.IOException;
import java.util.ArrayList;

public interface DistanceUtil {
    ArrayList<String> findDestinations();
    DistanceMatrixElement[] getDistance(String origin, String... addresses) throws InterruptedException, ApiException, IOException;
}
