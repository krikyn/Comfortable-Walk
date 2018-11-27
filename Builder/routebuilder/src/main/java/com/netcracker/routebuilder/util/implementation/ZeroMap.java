package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.properties.AlgorithmParameters;
import com.netcracker.routebuilder.util.interfaces.AbstractPotentialMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.netcracker.routebuilder.util.implementation.Utils.initField;

@Component
@RequiredArgsConstructor
public class ZeroMap extends AbstractPotentialMap {

    private final AlgorithmParameters params;

    @PostConstruct
    public void init() {
        field = initField(params.getScale());
    }
}
