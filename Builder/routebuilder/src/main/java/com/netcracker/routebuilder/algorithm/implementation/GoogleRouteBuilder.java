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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class GoogleRouteBuilder {
    private final GeoApiContext Gcontext;
    private final String APIDirectionKey = "AIzaSyBXwTfSt74U8zY0mNpULiJEGPv9ZYOgL7U";
    private final int maxCountOfWayPoints = 21;//max value of points

    public GoogleRouteBuilder() {
        Gcontext = new GeoApiContext.Builder().apiKey(APIDirectionKey).build();
    }

    //build route without waypoints
    public ArrayList<GeoCoordinates> buildRoute(GeoCoordinates start, GeoCoordinates end) {
        return buildDirection(start, end);
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
            if (AllPoints / maxCountOfWayPoints < 1) {
                latL = new LatLng[AllPoints % maxCountOfWayPoints];
                enD = end;
            }
            // if quantity of intermediate points more then 21
            else {
                latL = new LatLng[maxCountOfWayPoints];
                //add endpoint geoposition
                enD = points.get(startPosition + latL.length);
            }
            //add geoposition for clear array of  intermediate points from String array 'por'
            for (int i = startPosition; i < startPosition + latL.length; i++) {
                latL[leng_point++] = GeoCoordinatesToLatLang(points.get(i));
            }
            //build route
            buildDirection(starT, latL, enD, ListPoints);
            startPosition += latL.length + 1;
            //condition of 'while'
            AllPoints -= latL.length + 1;
            start = end;
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
            DirectionsResult result = DirectionsApi.newRequest(Gcontext)
                    .origin(StartPoint.getX() + "," + StartPoint.getY())
                    .destination(endPoint.getX() + "," + endPoint.getY())
                    .mode(TravelMode.WALKING)
                    .await();
            //get steps with geoposition
            DirectionsStep[] Dirstep = result.routes[0].legs[0].steps;
            for (int i = 1; i < Dirstep.length; i++) {
                listPoint.add(LatLangToGeoCoordinates(Dirstep[i].endLocation));
            }
            listPoint.add(endPoint);
        } catch (ApiException e) {
        } catch (InterruptedException e) {
        } catch (IOException e) {
        }
        return listPoint;
    }

    //standart Google's method of building Routes
    private void buildDirection(GeoCoordinates StartPoint, LatLng[] waypoints,
                                GeoCoordinates endPoint, ArrayList<GeoCoordinates> Points) {
        if (waypoints.length > 21) throw new ArrayIndexOutOfBoundsException("Quantity of waypoints more  than 21");
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(Gcontext)
                    .origin(StartPoint.getX() + "," + StartPoint.getY())
                    .destination(endPoint.getX() + "," + endPoint.getY())
                    .mode(TravelMode.WALKING)
                    .waypoints(LatLangsToWaypoints(waypoints))//add waypoints
                    .await();
            //get steps with geoposition
            DirectionsStep[] Dirstep = result.routes[0].legs[0].steps;
            for (int i = 1; i < Dirstep.length; i++) {
                Points.add(LatLangToGeoCoordinates(Dirstep[i].endLocation));
            }
        } catch (ApiException e) {
        } catch (InterruptedException e) {
        } catch (IOException e) {
        }
    }

    private GeoCoordinates LatLangToGeoCoordinates(LatLng point) {
        String[] st = point.toString().split(",");
        return new GeoCoordinates(Double.valueOf(st[0]), Double.valueOf(st[1]));
    }

    //Convert Google LatLang type to Google Waypoints type
    private DirectionsApiRequest.Waypoint[] LatLangsToWaypoints(LatLng[] waypoints) {
        DirectionsApiRequest.Waypoint[] lat = new DirectionsApiRequest.Waypoint[waypoints.length];
        for (int i = 0; i < waypoints.length; i++) {
            lat[i] = new DirectionsApiRequest.Waypoint(waypoints[i], false);
        }
        return lat;
    }

    private LatLng GeoCoordinatesToLatLang(GeoCoordinates point) {
        return new LatLng(point.getX(), point.getY());
    }
}