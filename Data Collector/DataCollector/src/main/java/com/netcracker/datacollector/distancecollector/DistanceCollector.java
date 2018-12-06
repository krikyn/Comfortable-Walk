package com.netcracker.datacollector.distancecollector;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrixElement;
import com.netcracker.commons.data.model.Distance;
import com.netcracker.commons.data.model.bean.Graph;
import com.netcracker.commons.data.repository.DistanceRepository;
import com.netcracker.datacollector.distancecollector.service.DistanceGoogleClient;
import com.netcracker.datacollector.distancecollector.util.DistanceFindingAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Distance Collector for Path Finding Algorithm
 *
 * @author prokhorovartem
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class DistanceCollector {
    /**
     * The number of all points
     */
    private final int AMOUNT_OF_POINTS = 1599;
    /**
     * The number of horizontally points only
     */
    private final int HORIZONTALLY_POINTS = 39;
    /**
     * The number of vertically points only
     */
    private final int VERTICALLY_POINTS = 41;
    /**
     * The number of destinations per one request for first (expensive) variant to count distances
     */
    private final int AMOUNT_OF_DESTINATIONS_PER_REQUEST = 25;
    /**
     * Instance of DistanceRepository, which works with DB
     */
    private final DistanceRepository distanceRepository;
    /**
     * Instance of DistanceUtil, which finds destinations and distances
     */
    private final DistanceGoogleClient distanceGoogleClient;
    /**
     * Instance of Yaml class for working with *.yaml files
     */
    private Yaml yaml = new Yaml();
    /**
     * Field which guarantees to start calculate distances from zero point
     */
    private Integer fromPointCounter = 0;

    /**
     * First (expensive) variant to count distances. It counts from one point to all points
     */
    public void saveDistancesViaExpensiveVariant() {
        try (FileReader reader = new FileReader("distanceCounter.yaml")) {
            Map<String, Integer> loadedData = yaml.load(reader);
            fromPointCounter = loadedData.get("fromPointCounter");
        } catch (IOException e) {
            log.error("Error with loading data", e);
        }

        ArrayList<String> destinations = distanceGoogleClient.findDestinations();
        String[] arrayOfDestinations = destinations.toArray(new String[0]);
        for (int fromPoint = fromPointCounter; fromPoint < AMOUNT_OF_POINTS; fromPoint++) {

            for (int j = 0; j <= arrayOfDestinations.length; j += AMOUNT_OF_DESTINATIONS_PER_REQUEST) {

                String[] shortenedArray = Arrays.copyOfRange(arrayOfDestinations, j, j + AMOUNT_OF_DESTINATIONS_PER_REQUEST);
                DistanceMatrixElement[] distanceMatrixElements = new DistanceMatrixElement[0];
                try {
                    distanceMatrixElements = distanceGoogleClient.getDistance(arrayOfDestinations[fromPoint], shortenedArray);
                } catch (InterruptedException | ApiException | IOException e) {
                    log.error("Error with getting data from Google API", e);
                }

                for (int i = 0; i < distanceMatrixElements.length; i++) {
                    if (i + j >= AMOUNT_OF_POINTS) break;
                    saveDistance(fromPoint, i + j, distanceMatrixElements[i]);
                }
            }
            saveFromPointInFile(fromPoint);
        }
    }

    /**
     * Second (inaccurate) variant to count distances. It counts from one point to neighbours
     */
    public void saveDistancesWithLinks() {
        try (FileReader reader = new FileReader("distanceCounter.yaml")) {
            Map<String, Integer> loadedData = yaml.load(reader);
            fromPointCounter = loadedData.get("fromPointCounter");
        } catch (IOException e) {
            log.error("Error with loading data", e);
        }

        ArrayList<String> destinations = distanceGoogleClient.findDestinations();
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
                    distanceMatrixElements = distanceGoogleClient.getDistance(arrayOfDestinations[i], lastRowDestinationsArray);
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
                    distanceMatrixElements = distanceGoogleClient.getDistance(arrayOfDestinations[i], rightColumnDestinationsArray);
                    saveDistance(i, i + HORIZONTALLY_POINTS - 1, distanceMatrixElements[0]);
                    saveDistance(i, i + HORIZONTALLY_POINTS, distanceMatrixElements[1]);
                    continue;
                }
                //путь в правую нижнюю точку
                destinationsArray[3] = arrayOfDestinations[i + HORIZONTALLY_POINTS + 1];
                //если начало в крайней левой колонке
                if (i % HORIZONTALLY_POINTS == 0) {
                    String[] leftColumnDestinationsArray = {destinationsArray[0], destinationsArray[2], destinationsArray[3]};
                    distanceMatrixElements = distanceGoogleClient.getDistance(arrayOfDestinations[i], leftColumnDestinationsArray);
                    saveDistance(i, i + 1, distanceMatrixElements[0]);
                    saveDistance(i, i + HORIZONTALLY_POINTS, distanceMatrixElements[1]);
                    saveDistance(i, i + HORIZONTALLY_POINTS + 1, distanceMatrixElements[2]);
                    continue;
                }
                distanceMatrixElements = distanceGoogleClient.getDistance(arrayOfDestinations[i], destinationsArray);
            } catch (InterruptedException | ApiException | IOException e) {
                log.error("Error with getting data from Google API", e);
            }
            saveDistance(i, i + 1, distanceMatrixElements[0]);
            saveDistance(i, i + HORIZONTALLY_POINTS - 1, distanceMatrixElements[1]);
            saveDistance(i, i + HORIZONTALLY_POINTS, distanceMatrixElements[2]);
            saveDistance(i, i + HORIZONTALLY_POINTS + 1, distanceMatrixElements[3]);

            saveFromPointInFile(i);
        }
        calculateLinks();
    }

    /**
     * Counts another links for second (inaccurate) variant with Dijkstra's algorithm
     */
    private void calculateLinks() {
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

    /**
     * Saves next fromPoint in file to continue if program will suddenly stopped
     *
     * @param fromPoint at which point we have to continue
     */
    private void saveFromPointInFile(int fromPoint) {
        try (FileWriter writer = new FileWriter("distanceCounter.yaml")) {
            Map<String, Integer> counter = new HashMap<>(); //Сохраняем значения счётчиков в yaml файл
            counter.put("fromPointCounter", fromPoint + 1);
            yaml.dump(counter, writer);
        } catch (IOException e) {
            log.error("Error with saving data", e);
        }
    }

    /**
     * Save received values into DB
     *
     * @param fromPoint             from which point we found distance
     * @param toPoint               to which point we found distance
     * @param distanceMatrixElement distance between points
     */
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
