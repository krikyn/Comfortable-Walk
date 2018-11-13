package com.netcracker.datacollector.service.implementation;

import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.data.repository.PlaceRepository;
import com.netcracker.datacollector.service.PlaceService;
import com.netcracker.datacollector.util.enums.PlacesType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Grout on 28.10.2018.
 */

@Component
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public Place savePlace(@NonNull Place place) {
        return placeRepository.saveAndFlush(place);
    }

    @Override
    public List<Place> loadAllPlacesByType(String type) {
        return placeRepository.findAllByType(type);
    }

    @Override
    public void deletePlace(UUID id) {
        placeRepository.deleteById(id);
    }
}
