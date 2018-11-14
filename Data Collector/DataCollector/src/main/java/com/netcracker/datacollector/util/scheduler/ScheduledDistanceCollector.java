package com.netcracker.datacollector.util.scheduler;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrixElement;
import com.netcracker.datacollector.data.model.Distance;
import com.netcracker.datacollector.data.repository.DistanceRepository;
import com.netcracker.datacollector.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ScheduledDistanceCollector {

    private final int AMOUNT_OF_POINTS = 1560;
    private final int AMOUNT_OF_DESTINATIONS_PER_REQUEST = 25;

    private final DistanceRepository distanceRepository;
    private final DistanceUtil distanceUtil;

    private Yaml yaml = new Yaml();
    private Integer fromPointCounter = 0;

//    ОПАСНО! НЕ СНИМАТЬ!
//    @Scheduled(fixedDelay = 1)
    public void saveDistances() {
        try (FileReader reader = new FileReader("distanceCounter.yaml")) {
            Map<String, Integer> loadedData = yaml.load(reader);
            fromPointCounter = loadedData.get("fromPointCounter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> destinations = distanceUtil.findDestinations();
        String[] arrayOfDestinations = destinations.toArray(new String[0]);
        for (int fromPoint = fromPointCounter; fromPoint < AMOUNT_OF_POINTS; fromPoint++) {

            for (int j = 0; j <= arrayOfDestinations.length; j += AMOUNT_OF_DESTINATIONS_PER_REQUEST) {

                String[] shortenedArray = Arrays.copyOfRange(arrayOfDestinations, j, j + AMOUNT_OF_DESTINATIONS_PER_REQUEST);
                DistanceMatrixElement[] distanceMatrixElements = new DistanceMatrixElement[0];
                try {
                    distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[fromPoint], shortenedArray);
                } catch (InterruptedException | ApiException | IOException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < distanceMatrixElements.length; i++) {
                    if (i + j >= AMOUNT_OF_POINTS) break;
                    saveDistance(fromPoint, i + j, distanceMatrixElements[i]);
                }
            }
            try (FileWriter writer = new FileWriter("distanceCounter.yaml")) {
                Map<String, Integer> counter = new HashMap<>(); //Сохраняем значения счётчиков в yaml файл
                counter.put("fromPointCounter", fromPoint + 1);
                yaml.dump(counter, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDistance(int fromPoint, int toPoint, DistanceMatrixElement distanceMatrixElement) {
        Distance distance = new Distance();
        distance.setFromPoint(fromPoint);
        distance.setToPoint(toPoint);
        if (distanceMatrixElement.distance == null) {
            distance.setDistance(null);
        } else {
            distance.setDistance(distanceMatrixElement.distance.inMeters);
        }
        distanceRepository.save(distance);
    }
}
