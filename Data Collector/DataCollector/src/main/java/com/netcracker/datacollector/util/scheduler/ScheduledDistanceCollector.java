package com.netcracker.datacollector.util.scheduler;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrixElement;
import com.netcracker.datacollector.data.model.Distance;
import com.netcracker.datacollector.data.model.bean.Graph;
import com.netcracker.datacollector.data.repository.DistanceRepository;
import com.netcracker.datacollector.util.DistanceFindingAlgorithm;
import com.netcracker.datacollector.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ScheduledDistanceCollector {

    private final int AMOUNT_OF_POINTS = 1599;
    private final int HORIZONTALLY_POINTS = 39;
    private final int VERTICALLY_POINTS = 41;
    private final int AMOUNT_OF_DESTINATIONS_PER_REQUEST = 25;

    private final DistanceRepository distanceRepository;
    private final DistanceUtil distanceUtil;

    private Yaml yaml = new Yaml();
    private Integer fromPointCounter = 0;

//    ОПАСНО! НЕ СНИМАТЬ!
//    @Scheduled(fixedDelay = 1)
//    public void saveDistances() {
//        try (FileReader reader = new FileReader("distanceCounter.yaml")) {
//            Map<String, Integer> loadedData = yaml.load(reader);
//            fromPointCounter = loadedData.get("fromPointCounter");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ArrayList<String> destinations = distanceUtil.findDestinations();
//        String[] arrayOfDestinations = destinations.toArray(new String[0]);
//        for (int fromPoint = fromPointCounter; fromPoint < AMOUNT_OF_POINTS; fromPoint++) {
//
//            for (int j = 0; j <= arrayOfDestinations.length; j += AMOUNT_OF_DESTINATIONS_PER_REQUEST) {
//
//                String[] shortenedArray = Arrays.copyOfRange(arrayOfDestinations, j, j + AMOUNT_OF_DESTINATIONS_PER_REQUEST);
//                DistanceMatrixElement[] distanceMatrixElements = new DistanceMatrixElement[0];
//                try {
//                    distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[fromPoint], shortenedArray);
//                } catch (InterruptedException | ApiException | IOException e) {
//                    e.printStackTrace();
//                }
//
//                for (int i = 0; i < distanceMatrixElements.length; i++) {
//                    if (i + j >= AMOUNT_OF_POINTS) break;
//                    saveDistance(fromPoint, i + j, distanceMatrixElements[i]);
//                }
//            }
//            try (FileWriter writer = new FileWriter("distanceCounter.yaml")) {
//                Map<String, Integer> counter = new HashMap<>(); //Сохраняем значения счётчиков в yaml файл
//                counter.put("fromPointCounter", fromPoint + 1);
//                yaml.dump(counter, writer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    @Scheduled(fixedDelay = 1000000000)
    public void saveDistancesWithLinks() {
        try (FileReader reader = new FileReader("distanceCounter.yaml")) {
            Map<String, Integer> loadedData = yaml.load(reader);
            fromPointCounter = loadedData.get("fromPointCounter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> destinations = distanceUtil.findDestinations();
        String[] arrayOfDestinations = destinations.toArray(new String[0]);
        for (int i = fromPointCounter; i < arrayOfDestinations.length - 1; i++) {
            DistanceMatrixElement[] distanceMatrixElements = new DistanceMatrixElement[0];
            String[] destinationsArray = new String[4];
            try {
                //путь в правую точку
                destinationsArray[0] = arrayOfDestinations[i + 1];
                //если начало в последней строчке
                if (i >= AMOUNT_OF_POINTS - HORIZONTALLY_POINTS) {
                    String[] lastRowDestinationsArray = {destinationsArray[0]};
                    distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[i], lastRowDestinationsArray);
                    saveDistance(i, i + 1, distanceMatrixElements[0]);
                    continue;
                }
                //путь в левую нижнюю точку
                destinationsArray[1] = arrayOfDestinations[i + HORIZONTALLY_POINTS - 1];
                //путь в нижнюю точку
                destinationsArray[2] = arrayOfDestinations[i + HORIZONTALLY_POINTS];
                //если начало в крайней правой колонке
                if (i % HORIZONTALLY_POINTS == 38) {
                    String[] rightColumnDestinationsArray = {destinationsArray[1], destinationsArray[2]};
                    distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[i], rightColumnDestinationsArray);
                    saveDistance(i, i + HORIZONTALLY_POINTS - 1, distanceMatrixElements[0]);
                    saveDistance(i, i + HORIZONTALLY_POINTS, distanceMatrixElements[1]);
                    continue;
                }
                //путь в правую нижнюю точку
                destinationsArray[3] = arrayOfDestinations[i + HORIZONTALLY_POINTS + 1];
                //если начало в крайней левой колонке
                if (i % HORIZONTALLY_POINTS == 0) {
                    String[] leftColumnDestinationsArray = {destinationsArray[0], destinationsArray[2], destinationsArray[3]};
                    distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[i], leftColumnDestinationsArray);
                    saveDistance(i, i + 1, distanceMatrixElements[0]);
                    saveDistance(i, i + HORIZONTALLY_POINTS, distanceMatrixElements[1]);
                    saveDistance(i, i + HORIZONTALLY_POINTS + 1, distanceMatrixElements[2]);
                    continue;
                }
                distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[i], destinationsArray);
            } catch (InterruptedException | ApiException | IOException e) {
                e.printStackTrace();
            }
            saveDistance(i, i + 1, distanceMatrixElements[0]);
            saveDistance(i, i + HORIZONTALLY_POINTS - 1, distanceMatrixElements[1]);
            saveDistance(i, i + HORIZONTALLY_POINTS, distanceMatrixElements[2]);
            saveDistance(i, i + HORIZONTALLY_POINTS + 1, distanceMatrixElements[3]);

            try (FileWriter writer = new FileWriter("distanceCounter.yaml")) {
                Map<String, Integer> counter = new HashMap<>(); //Сохраняем значения счётчиков в yaml файл
                counter.put("fromPointCounter", i + 1);
                yaml.dump(counter, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        calculateLinks();
    }

    //    @Scheduled(fixedDelay = 1000000000)
    public void calculateLinks() {
        List<Distance> distances = distanceRepository.findAll();

        Graph graph = new Graph(distances.size());

        for (Distance distance : distances) {
            graph.addArc(distance.getFromPoint(), distance.getToPoint(), distance.getDistance());
            graph.addArc(distance.getToPoint(), distance.getFromPoint(), distance.getDistance());
        }

        DistanceFindingAlgorithm distanceFindingAlgorithm = new DistanceFindingAlgorithm(graph);
        for (int fromPoint = 0; fromPoint < AMOUNT_OF_POINTS - 1; fromPoint++) {
            for (int toPoint = fromPoint + 1; toPoint < AMOUNT_OF_POINTS; toPoint++) {
                Long distance = distanceFindingAlgorithm.getDistances(fromPoint)[toPoint];
                if (distance == Integer.MAX_VALUE)
                    continue;
                if (fromPoint >= AMOUNT_OF_POINTS - HORIZONTALLY_POINTS)
                    if (toPoint == fromPoint + 1)
                        continue;
                if (fromPoint % HORIZONTALLY_POINTS == 38)
                    if (toPoint == fromPoint + HORIZONTALLY_POINTS - 1 || toPoint == fromPoint + HORIZONTALLY_POINTS)
                        continue;
                if (fromPoint % HORIZONTALLY_POINTS == 0)
                    if (toPoint == fromPoint + 1 || toPoint == fromPoint + HORIZONTALLY_POINTS ||
                            toPoint == fromPoint + HORIZONTALLY_POINTS + 1)
                        continue;
                if (toPoint == fromPoint + 1 || toPoint == fromPoint + HORIZONTALLY_POINTS - 1
                        || toPoint == fromPoint + HORIZONTALLY_POINTS || toPoint == fromPoint + HORIZONTALLY_POINTS + 1)
                    continue;
                Distance newDistance = new Distance();
                newDistance.setFromPoint(fromPoint);
                newDistance.setToPoint(toPoint);
                newDistance.setDistance(distance);
                distanceRepository.save(newDistance);
            }
        }
    }

    private void saveDistance(int fromPoint, int toPoint, DistanceMatrixElement distanceMatrixElement) {
        Distance distance = new Distance();
        distance.setFromPoint(fromPoint);
        distance.setToPoint(toPoint);
        if (distanceMatrixElement.distance == null) {
            return;
        } else {
            distance.setDistance(distanceMatrixElement.distance.inMeters);
        }
        distanceRepository.save(distance);
    }
}
