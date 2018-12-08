package com.netcracker.commons.service;

import com.netcracker.commons.data.model.Place;
import com.netcracker.commons.data.repository.PlaceRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for {@link Place}
 *
 * @author Ali
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
