package com.netcracker.datacollector.util;

import com.netcracker.datacollector.data.model.WeatherPotentialMap;
import com.netcracker.datacollector.service.WeatherMapService;
import io.github.mvpotter.model.Coordinate;
import io.github.mvpotter.model.Size;
import io.github.mvpotter.model.YandexMap;
import io.github.mvpotter.model.polyline.Curve;
import io.github.mvpotter.model.polyline.Polygon;
import io.github.mvpotter.urlbuilder.YandexApiUrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherCollector {

    private final WeatherMapService weatherMapService;

    @Autowired
    public WeatherCollector(WeatherMapService weatherMapService) {
        this.weatherMapService = weatherMapService;
    }

    private BufferedImage downloadImage(String imageURL) {
        BufferedImage image = null;
        try {
            URL url = new URL(imageURL);
            image = ImageIO.read(url);
        } catch (IOException ignored) {
        }

        return image;
    }

    private String[] setCurrentImageURLAndURI(String ext) {
        String radarURL = "http://weather.rshu.ru/radar/data/";
        String imageURIPrefix = "P_100_26061_";
        String imageURIPostfix = "_MRL";

        LocalDateTime date = LocalDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH");
        String imageURINamePrefix = date.format(formatter);
        String imageURINamePostfix = ((date.getMinute() / 10)) + "0";

        String[] parameters = new String[2];

        parameters[0] = imageURIPrefix + imageURINamePrefix + imageURINamePostfix + imageURIPostfix + "." + ext;
        parameters[1] = radarURL + parameters[0];

        return parameters;
    }

    private void fillWeatherTypes(Map<Integer, Integer> weatherTypes) {
        weatherTypes.put(new Color(208, 208, 208).getRGB(), 0);
        weatherTypes.put(new Color(255, 255, 255).getRGB(), 1);
        weatherTypes.put(new Color(192, 255, 192).getRGB(), 2);
        weatherTypes.put(new Color(0, 255, 0).getRGB(), 3);
        weatherTypes.put(new Color(0, 191, 0).getRGB(), 4);
        weatherTypes.put(new Color(0, 127, 0).getRGB(), 5);
        weatherTypes.put(new Color(0, 255, 255).getRGB(), 6);
        weatherTypes.put(new Color(0, 150, 255).getRGB(), 7);
        weatherTypes.put(new Color(0, 0, 255).getRGB(), 8);
        weatherTypes.put(new Color(0, 0, 150).getRGB(), 9);
        weatherTypes.put(new Color(255, 192, 192).getRGB(), 10);
        weatherTypes.put(new Color(255, 0, 204).getRGB(), 11);
        weatherTypes.put(new Color(255, 0, 0).getRGB(), 12);
        weatherTypes.put(new Color(255, 255, 0).getRGB(), 13);
        weatherTypes.put(new Color(255, 180, 100).getRGB(), 14);
        weatherTypes.put(new Color(191, 66, 66).getRGB(), 15);
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

    private String createWebMap(Double centerLon, Double centerLat, BufferedImage image) {

        double lat1KM = 0.00898;
        double lon1KM = 0.01440;

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
            System.out.println();
        }

        YandexApiUrlBuilder yandexApiUrlBuilder = new YandexApiUrlBuilder();
        String url = yandexApiUrlBuilder.build(yandexMap);

        return url;
    }

    public void run() {

        String ext = "PNG";
        String[] parameters = setCurrentImageURLAndURI(ext);

        String imageURI = parameters[0];
        String imageURL = parameters[1];

        System.out.println("URI: " + imageURI);
        System.out.println("URL: " + imageURL);


        BufferedImage image = downloadImage(imageURL);
        if (image == null) {
            System.out.println("The image is not ready yet. Try again later.");
            return;
        }

        try {
            File file = new File("source-images/" + imageURI);
            ImageIO.write(image, ext, file);
        } catch (IOException e) {
            System.out.println("Error with writing new image from radar: " + e.getMessage());
        }


        BufferedImage negativeImage;
        try {
            negativeImage = ImageIO.read(new File("auxiliary-images/negative.PNG"));
        } catch (IOException e) {
            System.out.println("Error with reading negative image: " + e.getMessage());
            return;
        }

        LocalDateTime date = LocalDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd_HH_");
        String processedImageURI = date.format(formatter) + ((date.getMinute() / 10)) + "0";

        File fileAfterProcessing = new File("processed-images/processed" + processedImageURI + ".PNG");
        BufferedImage imageAfterProcessing = image.getSubimage(140, 135, 220, 220);

        Map<Integer, Integer> weatherTypes = new HashMap<>();
        fillWeatherTypes(weatherTypes);

        int grayInt = new Color(208, 208, 208).getRGB();
        int strangeGrayInt = new Color(122, 122, 122).getRGB();

        for (int x = 0; x < imageAfterProcessing.getWidth(); x++) {
            for (int y = 0; y < imageAfterProcessing.getHeight(); y++) {

                if (negativeImage.getRGB(x, y) != strangeGrayInt || imageAfterProcessing.getRGB(x, y) == Color.black.getRGB()) {
                    imageAfterProcessing.setRGB(x, y, grayInt);
                }

                //for create auxiliary image
                /*if (imageAfterProcessing.getRGB(x, y) == new Color(208, 208, 208).getRGB() ||
                        imageAfterProcessing.getRGB(x, y) == new Color(255, 255, 255).getRGB() ||
                        imageAfterProcessing.getRGB(x, y) == new Color(192, 255, 192).getRGB()) {
                    imageAfterProcessing.setRGB(x, y, strangeGrayInt);
                }*/
            }
        }

        imageAfterProcessing = medianFilter(imageAfterProcessing);

        try {
            //paint central cell in the red color
            //imageAfterProcessing.setRGB(110, 110, new Color(255, 0, 0).getRGB());
            ImageIO.write(imageAfterProcessing, ext, fileAfterProcessing);
        } catch (IOException e) {
            System.out.println("Error with writing image after processing from radar: " + e.getMessage());
        }


        Integer centerX = 110;
        Integer centerY = 110;
        Integer halfHigh = 20;
        Integer halfWight = 20;

        //Print potential field
        /*for (int x = centerX - halfWight; x <= centerX + halfWight; x++) {
            for (int y = centerY - halfHigh; y <= centerY + halfHigh; y++) {
                System.out.print(weatherTypes.get(imageAfterProcessing.getRGB(y, x)) + "|");
            }
            System.out.println();
        }*/

        int[][] map = new int[21][20];
        int c1 = 0, c2 = 0;
        for (int x = centerX - 8; x <= centerX + 12; x++) {
            c2 = 0;
            for (int y = centerY - 15; y <= centerY + 4; y++) {
                map[c1][c2] = weatherTypes.get(imageAfterProcessing.getRGB(y, x));
                c2++;
            }
            c1++;
        }

        updateMapInDB(map);

        System.out.println("New image from the radar received and saved");

        //print link to the yandex map with potential field
        //String webStaticMap = createWebMap(30.705, 59.946, imageAfterProcessing);
        //System.out.println(webStaticMap);
    }

    private void updateMapInDB(int[][] map) {
        WeatherPotentialMap potentialMap = new WeatherPotentialMap();
        potentialMap.setPotentialField(map);
        potentialMap.setScale(1);
        potentialMap.setId(0);

        weatherMapService.updateMap(potentialMap);
    }
}
