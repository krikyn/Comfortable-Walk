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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Component
public class DistanceUtil {

    private final BigDecimal UPPER_LEFT_LONGITUDE = BigDecimal.valueOf(30.18035);
    private final BigDecimal UPPER_LEFT_LATITUDE = BigDecimal.valueOf(60.02781);
    private final BigDecimal BOTTOM_RIGHT_LONGITUDE = BigDecimal.valueOf(30.52101);
    private final BigDecimal BOTTOM_RIGHT_LATITUDE = BigDecimal.valueOf(59.84802);
    private final BigDecimal DISTANCE_FOR_LONGITUDE = BigDecimal.valueOf(0.008965);
    private final BigDecimal DISTANCE_FOR_LATITUDE = BigDecimal.valueOf(0.004495);
    private final BigDecimal EPSILON_FOR_LATITUDE = BigDecimal.valueOf(0.00001);

    /**
     *  Возвращает лист всех центров (широта, долгота) квадратов размером 500х500 (39x41 квадратов)
     **/
    public ArrayList<String> findDestinations() {
        ArrayList<String> list = new ArrayList<>();
        BigDecimal currentX = UPPER_LEFT_LONGITUDE;
        BigDecimal currentY = UPPER_LEFT_LATITUDE;
        currentX.setScale(6, ROUND_HALF_UP);
        currentY.setScale(6, ROUND_HALF_UP);
        do {
            Point point = new Point();
            point.setX(currentX);
            point.setY(currentY);
            list.add(point.toString());
            if (currentX.compareTo(BOTTOM_RIGHT_LONGITUDE) > 0) {
                currentX = UPPER_LEFT_LONGITUDE;
                currentY = currentY.subtract(DISTANCE_FOR_LATITUDE);
            } else currentX = currentX.add(DISTANCE_FOR_LONGITUDE);
        } while (currentY.compareTo(BOTTOM_RIGHT_LATITUDE.subtract(EPSILON_FOR_LATITUDE)) >= 0);
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
