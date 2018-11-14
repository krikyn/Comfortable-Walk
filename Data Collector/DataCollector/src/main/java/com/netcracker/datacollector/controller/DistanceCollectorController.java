package com.netcracker.datacollector.controller;

import com.netcracker.datacollector.data.model.Distance;
import com.netcracker.datacollector.data.repository.DistanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/distanceApi")
public class DistanceCollectorController {

    private final DistanceRepository distanceRepository;

    /**
     *  Получает все возможные расстрояния между квадратами 39х40 (500х500 метров)
     **/
    @GetMapping("/get")
    public List<Distance> getDestinations() {
        return distanceRepository.findAll();
    }
}