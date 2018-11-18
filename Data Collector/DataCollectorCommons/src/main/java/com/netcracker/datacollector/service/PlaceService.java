package com.netcracker.datacollector.service;

import com.netcracker.datacollector.data.model.Place;
import com.netcracker.datacollector.data.repository.PlaceRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Grout on 28.10.2018.
 */

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlaceService {

    private final PlaceRepository placeRepository;

    public void savePlace(@NonNull Place place) {
        placeRepository.save(place);
        placeRepository.flush();
    }

    public List<Place> loadAllPlacesByType(String type) {
        return placeRepository.findAllByType(type);
    }

}
