package com.netcracker.datacollector.util.implementation;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.netcracker.datacollector.data.model.Point;
import com.netcracker.datacollector.util.Variables;
import com.netcracker.distancecollector.util.DistanceUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class DistanceUtilImpl implements DistanceUtil {
    /**
     *  Возвращает лист всех центров (широта, долгота) квадратов размером 500х500
     **/
    @Override
    public ArrayList<String> findDestinations() {
        ArrayList<String> list = new ArrayList<>();
        double currentX = 30.18035;
        double currentY = 60.02781;
        do {
            Point point = new Point();
            point.setX(currentX);
            point.setY(currentY);
            list.add(point.toString());
            if (currentX > 30.52101) {
                currentX = 30.18035;
                currentY = currentY - 0.004495;
            } else currentX = currentX + 0.008965;
        } while (currentY >= 59.84802);
        return list;
    }

    /**
     * @param origin начальная точка
     * @param addresses массив точек, куда проложить маршрут
     * @return массив расстояний
     */
    @Override
    public DistanceMatrixElement[] getDistance(String origin, String... addresses) throws InterruptedException, ApiException, IOException {
        GeoApiContext distCalcer = new GeoApiContext.Builder()
                .apiKey(Variables.SECRET_KEY)
                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer);
        DistanceMatrix result = req.origins(origin)
                .destinations(addresses)
                .mode(TravelMode.WALKING)
                .units(Unit.METRIC)
                .await();

        return result.rows[0].elements;
    }
}
