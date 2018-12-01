package com.netcracker.routebuilder.properties;

import com.netcracker.routebuilder.util.enums.DistanceType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AlgorithmParameters {

    @Value("${algorithm.maxAllowableIncrease}")
    private int maxAllowableIncrease; //Во сколько раз маршрут нашего алгоритма может превышать длину гугловского маршрута

    @Value("${algorithm.minDistBetweenStartEnd}")
    private int minDistBetweenStartEnd; //Если расстояние между точками начала и конца меньше, то сразу используется гугловский алгоритм

    @Value("${algorithm.scale}")
    private int scale; //Размер клетки потенциального поля на которой будет работать алгоритм

    @Value("${algorithm.ApiKey}")
    private String ApiKey; //API-key for Google API Direction

    @Value("${algorithm.distanceType}")
    private DistanceType distanceType; //какой алгоритм для расчета расстояния до цели (H) использовать

    @Value("${algorithm.MaxCountOfWaypoints}")
    private int MaxCountOfWaypoints; //Max count of waypoints in Google's API

    @Value("${algorithm.normalFactorH}")
    private double normalFactorH; // нормализующий коэффициент для оценки расстояния до цели

    @Value("${algorithm.normalFactorG}")
    private double normalFactorG; //нормализующий коэффициент для стоимости пути от начальной вершины
}
