package com.netcracker.routebuilder.algorithm.implementation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathFindingAlgorithmTestConfiguration {
    @Bean
    public PathFindingAlgorithm pathFindingAlgorithm(){
        return new PathFindingAlgorithm();
    }
}
