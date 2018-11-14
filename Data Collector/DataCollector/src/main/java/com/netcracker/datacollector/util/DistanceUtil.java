package com.netcracker.datacollector.util;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.netcracker.datacollector.data.model.bean.Point;
import com.netcracker.datacollector.util.Variables;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class DistanceUtil {

    private final double UPPER_LEFT_LONGITUDE = 30.18035;
    private final double UPPER_LEFT_LATITUDE = 60.02781;
    private final double BOTTOM_RIGHT_LONGITUDE = 30.52101;
    private final double BOTTOM_RIGHT_LATITUDE = 59.84802;
    private final double DISTANCE_FOR_LONGITUDE = 0.008965;
    private final double DISTANCE_FOR_LATITUDE = 0.004495;

    /**
     *  Возвращает лист всех центров (широта, долгота) квадратов размером 500х500
     **/
    public ArrayList<String> findDestinations() {
        ArrayList<String> list = new ArrayList<>();
        double currentX = UPPER_LEFT_LONGITUDE;
        double currentY = UPPER_LEFT_LATITUDE;
        do {
            Point point = new Point();
            point.setX(currentX);
            point.setY(currentY);
            list.add(point.toString());
            if (currentX > BOTTOM_RIGHT_LONGITUDE) {
                currentX = UPPER_LEFT_LONGITUDE;
                currentY = currentY - DISTANCE_FOR_LATITUDE;
            } else currentX = currentX + DISTANCE_FOR_LONGITUDE;
        } while (currentY >= BOTTOM_RIGHT_LATITUDE);
        return list;
    }

    /**
     * @param origin начальная точка
     * @param addresses массив точек, куда проложить маршрут
     * @return массив расстояний
     */
    public DistanceMatrixElement[] getDistance(String origin, String... addresses) throws InterruptedException, ApiException, IOException {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(Variables.SECRET_KEY)
                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(geoApiContext);
        DistanceMatrix result = req.origins(origin)
                .destinations(addresses)
                .mode(TravelMode.WALKING)
                .units(Unit.METRIC)
                .await();

        return result.rows[0].elements;
    }
}
