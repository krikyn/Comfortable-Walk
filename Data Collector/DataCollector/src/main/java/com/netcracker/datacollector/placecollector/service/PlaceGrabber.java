package com.netcracker.datacollector.placecollector.service;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.commons.data.model.CityMap;
import com.netcracker.commons.data.model.Place;
import com.netcracker.commons.service.CityMapService;
import com.netcracker.commons.service.PlaceService;
import com.netcracker.datacollector.placecollector.service.PlaceGoogleClient;
import com.netcracker.datacollector.util.enums.PlacesType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class for scanning city map for places. Scanned places are stored in database.
 *
 * @author Ali
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class PlaceGrabber {

    private final PlaceGoogleClient searcher;
    private final PlaceService placeService;
    private final CityMapService cityMapService;

    private int placeCounter;
    private int row;
    private int col;
    private String fileCounter = "counters.yaml";
    private PlacesType[] placesType = PlacesType.values();
    private Yaml yaml = new Yaml();

    /**
     * Collect places info. Going over the base city map cells and scanning them for places of certain type. Place type
     * rotates after scanning the full map.
     */
    public void collect(){
        int baseRow = 20;
        int baseCol = 19;

        try {
            loadCounters();
        } catch (IOException e) {
            log.error("Counter loading error: ", e);
        }
        CityMap baseMap = cityMapService.loadCityMapByType("baseCityMap1km"); //Загрузка базовой карты с координатами локаций
        if(placeCounter > placesType.length-1) { //Проверка на выход за пределы списка мест
            placeCounter = 0;
        }
        if (col > baseCol){ //Проверка на выход за предел карты по долготе
            col = 0;
        }
        if (row > baseRow){ //Проверка на выход за предел карты по широте
            row = 0;
        }

        String placeType = placesType[placeCounter].toString(); //Последовательный выбор мест из списка типов
        LatLng location = baseMap.getBaseMap()[row][col];
        List<PlacesSearchResult> resultAll= new ArrayList<>();
        PlacesSearchResponse resultResponse = searcher.findAllPlacesByType(placeType, location, null); //Поиск мест выбранного типа в определённой локации

        if(resultResponse != null) {
            Collections.addAll(resultAll, resultResponse.results);
            try {
                loadNextPlacePage(resultResponse, placeType, resultAll, location);
            } catch (Exception e) {
                log.error("Error while getting next page of places: ", e);
            }
        }
        savePlaceData(resultAll, placeType);
        if(row == baseRow && col == baseCol) { //Если карта полностью пройдена, то меняем тип места на следующий
            placeCounter++;
        }
        if(col == baseCol){ //Если строка полностью пройдена, то переходим на следующую
            row++;
        }
        col++;

        try {
            saveCounters();
        } catch (IOException e) {
            log.error("Counter saving error: ", e);
        }
    }

    private void savePlaceData(List<PlacesSearchResult> places, String type) {
        for(PlacesSearchResult place: places) {
            Place newPlace = new Place();

            newPlace.setName(place.name);
            newPlace.setType(type);
            newPlace.setGooglePlaceId(place.placeId);
            if(place.formattedAddress == null) {
                newPlace.setAddress(place.vicinity);
            } else {
                newPlace.setAddress(place.formattedAddress);
            }
            newPlace.setLatitude(place.geometry.location.lat);
            newPlace.setLongitude(place.geometry.location.lng);

            placeService.savePlace(newPlace);
        }
    }

    private void saveCounters() throws IOException {
        Map<String, Integer> counters = new HashMap<>(); //Сохраняем значения счётчиков в yaml файл
        counters.put("placeCounter", placeCounter);
        counters.put("row", row);
        counters.put("col", col);
        FileWriter writer = new FileWriter(fileCounter);
        yaml.dump(counters, writer);
        writer.close();
    }

    private void loadCounters() throws IOException {
        FileReader reader = new FileReader(fileCounter); //Загрузка значений счётчиков из yaml файла
        Map<String, Integer> loadedData = yaml.load(reader);
        reader.close();
        placeCounter = loadedData.get("placeCounter");
        row = loadedData.get("row");
        col = loadedData.get("col");
    }

    private void loadNextPlacePage(PlacesSearchResponse resultResponse, String placeType,
                                   List<PlacesSearchResult> resultAll, LatLng location) throws Exception{
        if(resultResponse.nextPageToken != null) {
            Thread.sleep(20000); //Пауза перед запросом следущей страницы, что бы гугл не выдавал ошибку о превышении квоты на количество запросов
            resultResponse = searcher.findAllPlacesByType(placeType, location, resultResponse.nextPageToken);
            if(resultResponse != null) {
                Collections.addAll(resultAll, resultResponse.results);
                if(resultResponse.nextPageToken != null) {
                    Thread.sleep(20000); //Пауза перед запросом следущей страницы, что бы гугл не выдавал ошибку о превышении квоты на количество запросов
                    resultResponse = searcher.findAllPlacesByType(placeType, location, resultResponse.nextPageToken);
                    if(resultResponse != null) {
                        Collections.addAll(resultAll, resultResponse.results);
                    }
                }
            }
        }
    }
}
