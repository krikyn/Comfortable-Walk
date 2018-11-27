package com.netcracker.datacollector.util.scheduler;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.service.CityMapService;
import com.netcracker.datacollector.service.PlaceService;
import com.netcracker.datacollector.util.PlaceSearcher;
import com.netcracker.datacollector.util.enums.PlacesType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Created by Grout on 28.10.2018.
 */

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ScheduledPlaceGrubber {

    private final int MILLIS_PER_MINUTE = 60000;

    private final PlaceSearcher searcher;
    private final PlaceService placeService;
    private final CityMapService cityMapService;

    private Yaml yaml = new Yaml();

    private int placeCounter;
    private int row;
    private int col;
    private PlacesType[] placesType = PlacesType.values();

    //@Scheduled(fixedDelay = MILLIS_PER_MINUTE)
    public void getPlacesFromGoogleMaps() throws Exception {
        loadCounters();
        CityMap baseMap = cityMapService.loadCityMapByType("baseCityMap1km"); //Загрузка базовой карты с координатами локаций
        if(placeCounter > placesType.length-1) { //Проверка на выход за пределы списка мест
            placeCounter = 0;
        }
        if (col > 19){ //Проверка на выход за предел карты по долготе
            col = 0;
        }
        if (row > 20){ //Проверка на выход за предел карты по широте
            row = 0;
        }
        String placeType = placesType[placeCounter].toString(); //Последовательный выбор мест из списка типов
        LatLng location = baseMap.getBaseMap()[row][col];
        List<PlacesSearchResult> resultAll= new ArrayList<>();
        PlacesSearchResponse resultResponse = searcher.findAllPlacesByType(placeType, location); //Поиск мест выбранного типа в определённой локации

        if(resultResponse != null) {
            Collections.addAll(resultAll, resultResponse.results);
            //Далее страшный костыль для сбора информации 60-ти объектов, а не только 20-ти
            loadNextPlacePage(resultResponse, placeType, resultAll, location);
        }
        log.info("Quantity of places found: " + resultAll.size());
        savePlaceData(resultAll, placeType);
        log.info("Place grubbed successfully! Place counter: " + placeCounter + " Row: " + row + " Column: " + col);
        if(row == 20 && col == 19) { //Если карта полностью пройдена, то меняем тип места на следующий
            placeCounter++;
        }
        if(col == 19){ //Если строка полностью пройдена, то переходим на следующую
            row++;
        }
        col++;

        saveCounters();
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
        FileWriter writer = new FileWriter("counters.yaml");
        yaml.dump(counters, writer);
        writer.close();
    }

    private void loadCounters() throws IOException {
        FileReader reader = new FileReader("counters.yaml"); //Загрузка значений счётчиков из yaml файла
        Map<String, Integer> loadedData = yaml.load(reader);
        reader.close();
        placeCounter = loadedData.get("placeCounter");
        row = loadedData.get("row");
        col = loadedData.get("col");
    }

    private void loadNextPlacePage(PlacesSearchResponse resultResponse, String placeType, List<PlacesSearchResult> resultAll, LatLng location) throws Exception{
        if(resultResponse.nextPageToken != null) {
            Thread.sleep(20000);
            resultResponse = searcher.findAllPlacesByType(placeType, location, resultResponse.nextPageToken);
            if(resultResponse != null) { //Костыль intensifies
                Collections.addAll(resultAll, resultResponse.results);
                if(resultResponse.nextPageToken != null) { //Костыль intensifies x2
                    Thread.sleep(20000);
                    resultResponse = searcher.findAllPlacesByType(placeType, location, resultResponse.nextPageToken);
                    if(resultResponse != null) { //Костыль intensifies ULTRA COMBOOO
                        Collections.addAll(resultAll, resultResponse.results);
                    }
                }
            }
        }
    }
}
