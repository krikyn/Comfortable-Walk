package com.netcracker.datacollector.data.model;

import com.google.maps.model.LatLng;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Grout on 28.10.2018.
 *
 * Класс описывающий сущность карты.
 * */

@Entity
@Table(name = "maps")
@Data
@NoArgsConstructor
// daba все замечания применимы к остальным сущностям
public class CityMap {

    @Id
    @NonNull
    // daba ID у хибера невозможно сделать нуловым, аннотация NotNull лишняя
    @GeneratedValue
    // daba и уж тем более, что значение генерируется :)
    private UUID id;

    // daba эта штука сгенерирует логику проверки на null в сеттер. Об опасности логики в сеттерах + Hibernate см. пункт 1.
    // Если хочется контроля нуллов на уровне валидации, используйте Java Bean Validation API.
    @NonNull
    // daba тривиальный маппинг, имя и так совпадает с названием поля. Перестраховка?
    @Column(name = "type")
    private String type;

    @Column(name = "grid")
    @Lob
    int[][] grid;

    @Column(name = "base_map")
    @Lob
    LatLng[][] baseMap;

}
