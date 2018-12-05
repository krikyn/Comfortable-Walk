package com.netcracker.routebuilder.util.implementation;

import com.netcracker.routebuilder.properties.AlgorithmParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.netcracker.routebuilder.util.implementation.Utils.initField;

/**
 * Abstract class describing a zero map
 *
 * @author Kirill.Vakhrushev
 */
@Component
@RequiredArgsConstructor
public class ZeroMap extends AbstractPotentialMap {

    private final AlgorithmParameters params;

    /**
     * Zero potential map initialization
     */
    @PostConstruct
    public void init() {
        field = initField(params.getScale());
    }
}
