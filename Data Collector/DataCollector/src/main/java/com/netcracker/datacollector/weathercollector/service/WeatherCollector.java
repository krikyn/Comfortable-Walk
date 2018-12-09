package com.netcracker.datacollector.weathercollector.service;

import com.netcracker.commons.data.repository.WeatherPotentialMapRepository;
import com.netcracker.datacollector.weathercollector.bean.ImageAddress;
import com.netcracker.datacollector.weathercollector.properties.RadarProperties;
import com.netcracker.datacollector.weathercollector.util.WeatherTypesFiller;
import io.github.mvpotter.model.Coordinate;
import io.github.mvpotter.model.Size;
import io.github.mvpotter.model.YandexMap;
import io.github.mvpotter.model.polyline.Curve;
import io.github.mvpotter.model.polyline.Polygon;
import io.github.mvpotter.urlbuilder.YandexApiUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for get data from weather radar
 *
 * @author Kirill.Vakhrushev
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WeatherCollector {

    private final WeatherPotentialMapRepository repository;
    private final RadarProperties radarProperties;
    WeatherTypesFiller weatherTypesFiller;

    final int defaultNumPointsX = 21;
    final int defaultNumPointsY = 20;

    /**
     * Method for get data from weather radar and rewrite weather potential map in DB
     */
    public void collect() {

        String fileExtension = "PNG";

        //На случай, если радар делает снимки в произвольное время

        ImageAddress imageAddress = new ImageAddress();
        BufferedImage image = downloadImageFromRadarAlternative(imageAddress);
        if (image == null) return;

        saveImageFromRadar(fileExtension, imageAddress.getImageURI(), image);

        BufferedImage negativeImage = uploadNegativeImage();
        if (negativeImage == null) return;

        File fileAfterProcessing = new File("processed-images/processed" + imageAddress.getImageURI());
        BufferedImage imageAfterProcessing = image.getSubimage(140, 135, 220, 220);


        Map<Integer, Integer> weatherTypes = new HashMap<>();

        weatherTypesFiller = new WeatherTypesFiller();
        weatherTypesFiller.fillWeatherTypes(weatherTypes);


        removingLabelsAndLinesFromImage(negativeImage, imageAfterProcessing);

        imageAfterProcessing = medianFilter(imageAfterProcessing);

        saveProcessedImage(fileExtension, fileAfterProcessing, imageAfterProcessing);


        //потенциальное поле разбитое на клетки 1 на 1 км, всего получается 20 на 21 клетка
        int[][] map = new int[defaultNumPointsX][defaultNumPointsY];

        SelectionPotentialMapFromImage(imageAfterProcessing, weatherTypes, map);

        logTotalSum(map);

        updateMapInDB(map);
        log.info("New image from the radar received and saved in the database");

        //Печатает ссылку на яндекс карту с потенциальным полем
        //String webStaticMap = createWebMap(30.705, 59.946, imageAfterProcessing);
        //System.out.println(webStaticMap);
    }

    private void logTotalSum(int[][] map) {
        int sum = 0;
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 20; j++) {
                sum += map[i][j];
            }
        }
        log.info("Total sum: " + sum);
    }

    private void SelectionPotentialMapFromImage(BufferedImage imageAfterProcessing, Map<Integer, Integer> weatherTypes, int[][] map) {
        int imageCenterX = 110;
        int imageCenterY = 110;

        int coord1 = 0, coord2;

        for (int x = imageCenterX - 8; x <= imageCenterX + 12; x++) {
            coord2 = 0;
            for (int y = imageCenterY - 15; y <= imageCenterY + 4; y++) {
                map[coord1][coord2] = weatherTypes.get(imageAfterProcessing.getRGB(y, x));
                coord2++;
            }
            coord1++;
        }
    }

    private void removingLabelsAndLinesFromImage(BufferedImage negativeImage, BufferedImage imageAfterProcessing) {
        final int grayInt = new Color(208, 208, 208).getRGB();
        final int strangeGrayInt = new Color(122, 122, 122).getRGB();

        for (int x = 0; x < imageAfterProcessing.getWidth(); x++) {
            for (int y = 0; y < imageAfterProcessing.getHeight(); y++) {
                if (negativeImage.getRGB(x, y) != strangeGrayInt || imageAfterProcessing.getRGB(x, y) == Color.black.getRGB()) {
                    imageAfterProcessing.setRGB(x, y, grayInt);
                }
            }
        }
    }

    private void saveProcessedImage(String fileExtension, File fileAfterProcessing, BufferedImage imageAfterProcessing) {
        try {
            //окрашивает центральный пикслеь изображения в красный цвет
            //imageAfterProcessing.setRGB(110, 110, new Color(255, 0, 0).getRGB());
            ImageIO.write(imageAfterProcessing, fileExtension, fileAfterProcessing);
        } catch (IOException e) {
            log.warn("Error with writing image after processing from radar: " + e.getMessage());
        }
    }

    private BufferedImage uploadNegativeImage() {
        BufferedImage negativeImage = null;
        try {
            negativeImage = ImageIO.read(new File("auxiliary-images/negative.PNG"));
        } catch (IOException e) {
            log.error("Error with reading negative image: " + e.getMessage());
        }
        return negativeImage;
    }

    private void saveImageFromRadar(String fileExtension, String imageURI, BufferedImage image) {
        try {
            File file = new File("source-images/" + imageURI);
            ImageIO.write(image, fileExtension, file);
        } catch (IOException e) {
            log.warn("Error with writing new image from radar: " + e.getMessage());
        }
    }

    private BufferedImage downloadImageFromRadar(String imageURL) {
        BufferedImage image = null;
        try {
            URL url = new URL(imageURL);
            image = ImageIO.read(url);
        } catch (IOException e) {
            log.warn("The image is not ready yet. Try again later.");
        }

        return image;
    }

    private BufferedImage downloadImageFromRadarAlternative(ImageAddress imageAddress) {
        String radarPage;
        final int nameLen = 35;

        try {
            radarPage = getContentOfHTTPPage("http://weather.rshu.ru/radar/", "UTF-8");
        } catch (IOException e) {
            log.error("The radar's site is not available");
            return null;
        }

        //http://weather.rshu.ru/radar/data/P_100_26061_2018_11_27_2039_MRL.PNG
        String prefix = "<img id=\"radar\" src=\"data/";
        int pos = radarPage.indexOf(prefix);

        imageAddress.setImageURI(radarPage.substring(pos + prefix.length(), pos + prefix.length() + nameLen));

        imageAddress.setImageURL(radarProperties.getRadarURL() + imageAddress.getImageURI());

        log.info("URI: " + imageAddress.getImageURI());
        log.info("URL: " + imageAddress.getImageURL());

        BufferedImage image = null;
        try {
            URL url = new URL(imageAddress.getImageURL());
            image = ImageIO.read(url);
        } catch (IOException e) {
            log.warn("The image is not ready yet. Try again later.");
        }

        return image;
    }

    private static String getContentOfHTTPPage(String pageAddress, String codePage) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL pageURL = new URL(pageAddress);
        URLConnection uc = pageURL.openConnection();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), codePage))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        return sb.toString();
    }

    private String[] setCurrentImageURLAndURI(String fileExtension) {

        String radarURL = radarProperties.getRadarURL();
        String imageURIPrefix = radarProperties.getImageURIPrefix();
        String imageURIPostfix = radarProperties.getImageURIPostfix();

        LocalDateTime date = LocalDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH");
        String imageURINamePrefix = date.format(formatter);
        String imageURINamePostfix = String.valueOf(date.getMinute() / 10) + '0';

        String[] parameters = new String[2];

        parameters[0] = imageURIPrefix + imageURINamePrefix + imageURINamePostfix + imageURIPostfix + "." + fileExtension;
        parameters[1] = radarURL + parameters[0];

        return parameters;
    }

    private BufferedImage medianFilter(BufferedImage image) {
        BufferedImage imageAfterProcessing = image;
        int[] pixels = new int[9];

        for (int x = 1; x < image.getWidth() - 1; x++) {
            for (int y = 1; y < image.getHeight() - 1; y++) {

                int count = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        pixels[count] = image.getRGB(x + i, y + j);
                        count++;
                    }
                }

                Arrays.sort(pixels);
                imageAfterProcessing.setRGB(x, y, pixels[4]);
            }
        }

        return imageAfterProcessing;
    }

    //Пока не используется из-за ограничений яндекс карт
    private String createWebMap(Double centerLon, Double centerLat, BufferedImage image) {

        double lat1KM = 0.00898; //1 км в градусах широты
        double lon1KM = 0.01440; //1 км в градусах долготы

        YandexMap yandexMap = new YandexMap();

        yandexMap.setMapType(YandexMap.MapType.MAP);
        yandexMap.setCenter(new Coordinate(centerLon.toString(), centerLat.toString()));
        yandexMap.setViewport(new Coordinate("0.25", "0.25"));
        yandexMap.setScale(7);
        yandexMap.setSize(new Size(650, 450));
        yandexMap.setLanguage(YandexMap.Language.RUSSIAN);

        int centerX = 110;
        int centerY = 110;
        int halfHigh = 2;
        int halfWight = 0;

        for (int biasX = -halfWight; biasX <= halfWight; biasX++) {
            for (int biasY = -halfHigh; biasY <= halfHigh; biasY++) {

                Polygon polygon = new Polygon();
                polygon.setFillingColor(new Color(image.getRGB(centerX + biasX, centerY + biasY)));
                polygon.setWidth(0);

                Curve curve = new Curve();
                curve.addPoint(new Coordinate(Double.toString(centerLon - lon1KM * (biasX + (double) 1 / 2)), Double.toString(centerLat - lat1KM * (biasY + (double) 1 / 2))));
                curve.addPoint(new Coordinate(Double.toString(centerLon + lon1KM * (biasX + (double) 1 / 2)), Double.toString(centerLat - lat1KM * (biasY + (double) 1 / 2))));
                curve.addPoint(new Coordinate(Double.toString(centerLon + lon1KM * (biasX + (double) 1 / 2)), Double.toString(centerLat + lat1KM * (biasY + (double) 1 / 2))));
                curve.addPoint(new Coordinate(Double.toString(centerLon - lon1KM * (biasX + (double) 1 / 2)), Double.toString(centerLat + lat1KM * (biasY + (double) 1 / 2))));

                polygon.addCurve(curve);
                yandexMap.addPolyline(polygon);

            }
        }

        YandexApiUrlBuilder yandexApiUrlBuilder = new YandexApiUrlBuilder();
        return yandexApiUrlBuilder.build(yandexMap);
    }

    private void updateMapInDB(int[][] map) {
        final int idOfMapInDB = 0;
        repository.updateAddress(map, idOfMapInDB);
    }
}