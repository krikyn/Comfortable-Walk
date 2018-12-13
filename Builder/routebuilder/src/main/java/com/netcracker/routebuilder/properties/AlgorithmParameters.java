package com.netcracker.routebuilder.properties;

import com.netcracker.routebuilder.util.enums.DistanceType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Class for accessing constants participating in the path finding algorithm
 */
@Configuration
@Data
public class AlgorithmParameters {

    @Value("${algorithm.maxAllowableIncrease}")
    private double maxAllowableIncrease; //Во сколько раз маршрут нашего алгоритма может превышать длину гугловского маршрута

    @Value("${algorithm.minDistBetweenStartEnd}")
    private int minDistBetweenStartEnd; //Если расстояние между точками начала и конца меньше, то сразу используется гугловский алгоритм

    @Value("${algorithm.scale}")
    private int scale; //Размер клетки потенциального поля на которой будет работать алгоритм

    @Value("${algorithm.ApiKey}")
    private String ApiKey; //API-key for Google API Direction

    @Value("${algorithm.distanceType}")
    private DistanceType distanceType; //какой алгоритм для расчета расстояния до цели (H) использовать

    @Value("${algorithm.maxCountOfWaypoints}")
    private int maxCountOfWaypoints; //max count of waypoint in Gooogle's Api

    @Value("${algorithm.normalFactorH}")
    private double normalFactorH; // нормализующий коэффициент для оценки расстояния до цели

    @Value("${algorithm.normalFactorG}")
    private double normalFactorG; //нормализующий коэффициент для стоимости пути от начальной вершины

    @Value("${algorithm.weatherFieldFactor}")
    private double weatherFieldFactor; //насколько учитывается потенциальная карта погоды в работе алгоритма

    @Value("${algorithm.placesFieldFactor}")
    private double placesFieldFactor; //насколько учитывается потенциальная карта мест в работе алгоритма

    @Value("${algorithm.routeFieldFactor}")
    private double routeFieldFactor; //насколько учитывается потенциальная карта исходного маршрута в работе алгоритма

    @Value("${algorithm.maxIterationsNum}")
    private double maxIterationsNum; //максимальное количество итераций в алгоритме, при превышении которого, выдается обычный гугловский маршрут
}
