package com.netcracker.routebuilder.algorithm.implementation;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.netcracker.routebuilder.data.bean.GeoCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class GoogleRouteBuilder {
    private final GeoApiContext GCONTEXT;
    static Logger LOGGER = LoggerFactory.getLogger(GoogleRouteBuilder.class);
    private final String API_DIRECTION_KEY = "AIzaSyDsx7KAWwgcWwWdvaVbjLRfWwnqrqoShN0"; //API-key for Google API Direction
    private final int MAX_COUNT_OF_WAYPOINTS = 21;//max value of waypoints in Google API Direction

    public GoogleRouteBuilder() {
        GCONTEXT = new GeoApiContext.Builder().apiKey(API_DIRECTION_KEY).build();
    }

    //build route without waypoints
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, GeoCoordinates end) {
        return buildDirection(start, end);
    }
    //build route .Input arrayList include starting position,final position,array of waypoints
    public ArrayList<GeoCoordinates> buildRoute(ArrayList<GeoCoordinates> points) {
        ArrayList<GeoCoordinates> ListPoints = new ArrayList<>();
        //add start point of route
        ListPoints.add(points.get(0));
        GeoCoordinates starT = points.get(0);
        GeoCoordinates enD = points.get(points.size() - 1);
        int AllPoints = points.size();
        int startPosition = 0;
        //devide route by parts
        while (AllPoints > 0) {
            //array of intermediate points(not more maxCountOfWayPoints)
            LatLng[] latL;
            int leng_point = 0;
            // if quantity of intermediate points less then 21
            if (AllPoints / MAX_COUNT_OF_WAYPOINTS < 1) {
                latL = new LatLng[AllPoints % MAX_COUNT_OF_WAYPOINTS];
                enD = points.get(points.size() - 1);
            }
            // if quantity of intermediate points more then 21
            else {
                latL = new LatLng[MAX_COUNT_OF_WAYPOINTS];
                //add endpoint geoposition
                enD = points.get(startPosition + latL.length);
            }
            //add geoposition for clear array of  intermediate points from String array 'por'
            for (int i = startPosition; i < startPosition + latL.length; i++) {
                latL[leng_point++] = ConvertToLatLang(points.get(i));
            }
            //build route
            buildDirection(starT, latL, enD, ListPoints);
            startPosition += latL.length + 1;
            //condition of 'while'
            AllPoints -= latL.length + 1;
            starT = enD;
        }
        ListPoints.add(points.get(points.size() - 1));
        return ListPoints;
    }

    //build route with waypoints
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, ArrayList<GeoCoordinates> points, GeoCoordinates end) {
        ArrayList<GeoCoordinates> ListPoints = new ArrayList<>();
        //add start point of route
        ListPoints.add(start);
        GeoCoordinates starT = start;
        GeoCoordinates enD = end;
        int AllPoints = points.size();
        int startPosition = 0;
        //devide route by parts
        while (AllPoints > 0) {
            //array of intermediate points(not more maxCountOfWayPoints)
            LatLng[] latL;
            int leng_point = 0;
            // if quantity of intermediate points less then 21
            if (AllPoints / MAX_COUNT_OF_WAYPOINTS < 1) {
                latL = new LatLng[AllPoints % MAX_COUNT_OF_WAYPOINTS];
                enD = end;
            }
            // if quantity of intermediate points more then 21
            else {
                latL = new LatLng[MAX_COUNT_OF_WAYPOINTS];
                //add endpoint geoposition
                enD = points.get(startPosition + latL.length);
            }
            //add geoposition for clear array of  intermediate points from String array 'por'
            for (int i = startPosition; i < startPosition + latL.length; i++) {
                latL[leng_point++] = ConvertToLatLang(points.get(i));
            }
            //build route
            buildDirection(starT, latL, enD, ListPoints);
            startPosition += latL.length + 1;
            //condition of 'while'
            AllPoints -= latL.length + 1;
            starT = end;
        }
        ListPoints.add(end);
        return ListPoints;
    }

    //standart Google's method of building Routes without waypoints
    private ArrayList<GeoCoordinates> buildDirection(GeoCoordinates StartPoint, GeoCoordinates endPoint) {
        ArrayList<GeoCoordinates> listPoint = new ArrayList<>();
        listPoint.add(StartPoint);
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(GCONTEXT)
                    .origin(StartPoint.getX() + "," + StartPoint.getY())
                    .destination(endPoint.getX() + "," + endPoint.getY())
                    .mode(TravelMode.WALKING)
                    .await();
            //get steps with geoposition
            DirectionsStep[] Dirstep = result.routes[0].legs[0].steps;
            for (int i = 1; i < Dirstep.length; i++) {
                listPoint.add(ConvertToGeoCoordinates(Dirstep[i].endLocation));
            }
            listPoint.add(endPoint);
        } catch (ApiException e) {
            LOGGER.error("Not valid API=key");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupt Thread");
        } catch (IOException e) {
            LOGGER.error("IOException");
        }
        return listPoint;
    }

    //standart Google's method of building Routes
    private void buildDirection(GeoCoordinates StartPoint, LatLng[] waypoints,
                                GeoCoordinates endPoint, ArrayList<GeoCoordinates> Points) {
        if (waypoints.length > 21) throw new ArrayIndexOutOfBoundsException("Quantity of waypoints more  than 21");
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(GCONTEXT)
                    .origin(StartPoint.getX() + "," + StartPoint.getY())
                    .destination(endPoint.getX() + "," + endPoint.getY())
                    .mode(TravelMode.WALKING)
                    .waypoints(ConvertToWaypoints(waypoints))//add waypoints
                    .await();
            //get steps with geoposition
            DirectionsStep[] Dirstep = result.routes[0].legs[0].steps;
            for (int i = 1; i < Dirstep.length; i++) {
                Points.add(ConvertToGeoCoordinates(Dirstep[i].endLocation));
            }
        } catch (ApiException e) {
            LOGGER.error("Not valid API=key");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupt Thread");
        } catch (IOException e) {
            LOGGER.error("IOException");
        }
    }

    //Convert LatLng type to GeoCoordinates
    private GeoCoordinates ConvertToGeoCoordinates(LatLng point) {
        String[] st = point.toString().split(",");
        return new GeoCoordinates(Double.valueOf(st[0]), Double.valueOf(st[1]));
    }

    //Convert Latlang array to WayPoints array
    private DirectionsApiRequest.Waypoint[] ConvertToWaypoints(LatLng[] waypoints) {
        DirectionsApiRequest.Waypoint[] lat = new DirectionsApiRequest.Waypoint[waypoints.length];
        for (int i = 0; i < waypoints.length; i++) {
            lat[i] = new DirectionsApiRequest.Waypoint(waypoints[i], false);
        }
        return lat;
    }

    //Convert GeoCoordinates type to LatLng
    private LatLng ConvertToLatLang(GeoCoordinates point) {
        return new LatLng(point.getX(), point.getY());
    }}