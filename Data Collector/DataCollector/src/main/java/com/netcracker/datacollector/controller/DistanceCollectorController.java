package com.netcracker.datacollector.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrixElement;
import com.netcracker.datacollector.data.model.Square;
import com.netcracker.datacollector.data.repository.SquareRepository;
import com.netcracker.distancecollector.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/distanceApi")
public class DistanceCollectorController {

    private final SquareRepository squareRepository;
    private final DistanceUtil distanceUtil;

    // daba Можно использовать Lombok и @RequiredArgsConstructor вместо
    @Autowired
    public DistanceCollectorController(SquareRepository squareRepository, DistanceUtil distanceUtil) {
        this.squareRepository = squareRepository;
        this.distanceUtil = distanceUtil;
    }

    /**
     *  Сохраняет все возможные расстрояния между квадратами 39х40 (500х500 метров)
     **/
    // daba Что-то, что пишет в базу - это по умолчанию никак не Get. Помните, что гет-запросы могут предвыполняться браузером
    // daba Контроллер не должен бросать исключений, это финальная точка для приложения, здесь все ошибки должны обработаться
    // daba Обычно контроллер управляет вводом-выводом данных. Здесь явная бизнес-логика, лучше выделить это в сервис и вызывать отсюда
    @GetMapping("/save")
    public void saveDestinations() throws InterruptedException, ApiException, IOException {
        ArrayList<String> destinations = distanceUtil.findDestinations();
        String[] arrayOfDestinations = destinations.toArray(new String[0]);
        // daba Магические цифры во все поля. Всё в константы с именем.
        for (int fromPoint = 0; fromPoint < 1560; fromPoint++) {
            for (int j = 0; j <= arrayOfDestinations.length; j += 25) {
                // daba Уничтоженный массив
                String[] destructedArray = Arrays.copyOfRange(arrayOfDestinations, j, j + 25);
                DistanceMatrixElement[] distanceMatrixElements = distanceUtil.getDistance(arrayOfDestinations[fromPoint], destructedArray);

                for (int i = 0; i < distanceMatrixElements.length; i++) {
                    if (i + j >= 1560) break;
                    Square square = new Square();
                    square.setFromPoint(fromPoint);
                    square.setToPoint(i + j);
                    if (distanceMatrixElements[i].distance == null) {
                        square.setDistance(null);
                    } else {
                        square.setDistance(distanceMatrixElements[i].distance.inMeters);
                    }
                    squareRepository.save(square);
                }
            }
        }
    }

    /**
     *  Получает все возможные расстрояния между квадратами 39х40 (500х500 метров)
     **/
    @GetMapping("/get")
    public List<Square> getDestinations() {
        return squareRepository.findAll();
    }
}