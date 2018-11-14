package com.netcracker.datacollector.service.implementation;

import com.netcracker.datacollector.data.model.CityMap;
import com.netcracker.datacollector.data.repository.CityMapRepository;
import com.netcracker.datacollector.service.CityMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


// daba Здесь и для остальных сервисов. Если у сервиса одна имплементация, интерфейс ему не нужен. То же самое с названиями.
// daba Если не получается по существу назвать сервис так, чтобы отличалось от названия его интерфейса и приходится добавлять impl в конец, то интерфейс не нужен
// daba есть ли вообще смысл у этого сервиса? он вызывает два метода репозитория, которые и так доступны из репозитория
// daba для сервисов есть стереотип @Service, можно использовать вместо @Component (на работу не влияет, просто стиль)
@Component
public class CityMapServiceImpl implements CityMapService {

    private final CityMapRepository repository;


    // daba ломбок
    @Autowired
    public CityMapServiceImpl(CityMapRepository repository) {
        this.repository = repository;
    }

    @Override
    public CityMap saveMap(CityMap cityMap) {
        return repository.saveAndFlush(cityMap);
    }

    @Override
    // daba Какие-то особые причины иметь здесь явный Transactional?
    @Transactional
    public CityMap loadCityMapByType(String type) {
        return repository.findCityMapByType(type);
    }
}
