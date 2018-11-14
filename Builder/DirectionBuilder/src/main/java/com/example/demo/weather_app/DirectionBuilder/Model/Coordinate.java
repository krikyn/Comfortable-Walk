package com.example.demo.weather_app.DirectionBuilder.Model;

import com.example.demo.weather_app.DirectionBuilder.error.MassSizeException;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
// daba название класса неинформативно
// daba по названию - это скорее модельная сущность
// daba по наполнению - всё и сразу
// daba этот класс надо разбивать. Тут и модель, и сервис, и всё на свете.
public class Coordinate {
    private  final GeoApiContext Gcontext;
    private final String APIDirectionKey="AIzaSyBXwTfSt74U8zY0mNpULiJEGPv9ZYOgL7U";
    // daba невероятно информативный комментарий, вносящий ясность :)
    // daba а вот а том, почему тут именно 21, неплохо бы написать комментарий
    private final int maxCountOfWayPoints=21;//max value of points
    private String StartPoint="";
    private String endPoint="";
    private String waypoints="";//String with intermediate points,that we get from input field
    private ArrayList<LatLng> points; //list of  intermediate points
    // daba геттеры-сеттеры ломбоком
    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }


    public  Coordinate(){
        Gcontext = getDirectionContext(APIDirectionKey);

    }

    private   GeoApiContext getDirectionContext(String key) {
        return new GeoApiContext.Builder().apiKey(key).build();
    }

    public String getStartPoint() {
        return StartPoint;
    }

    public void setStartPoint(String startPoint) {
        StartPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    // method returns all of intermediate points in String type
    public String getPoints(){
        StringBuffer buffer=new StringBuffer();
        // daba Collectors.joining и стримы
        for (LatLng p:points){
            buffer.append(p.toString()+"\n");
        }

        return buffer.toString();


}
//Method builds routes
    public void MakeDirection(){
        points =new ArrayList<>();
        //add start point of route
        points.add(StringToLatLang(StartPoint));
                 if (waypoints.equals("")) {
                     //build withoud intermediate points
                     buildDirection(StartPoint,endPoint);
                 } else {
                     //build with intermediate points
                     String start=getStartPoint();
                     // daba этот метод возвращает значение переменной из этого же класса
                     String end=getEndPoint();
                     //get string array of waypoints from user's input
                     // daba пор?
                     String por[] = waypoints.replaceAll("\\s+", "").split(";");
                     int AllPoints=por.length;
                     int startPosition=0;
                     //devide route by parts
                     while(AllPoints>0){
                         //array of intermediate points(not more maxCountOfWayPoints)
                         LatLng [] latL;
                         int leng_point=0;
                         // if quantity of intermediate points less then 21
                         if(AllPoints/maxCountOfWayPoints<1){
                             latL =new LatLng[AllPoints%maxCountOfWayPoints] ;
                             end=getEndPoint();
                         }
                         // if quantity of intermediate points more then 21
                         else {
                             latL =new LatLng[maxCountOfWayPoints] ;
                             //add endpoint geoposition
                             end=por[startPosition+latL.length];
                         }
                         //add geoposition for clear array of  intermediate points from String array 'por'
                         for(int i=startPosition;i<startPosition+latL.length;i++){

                             latL[leng_point++]=StringToLatLang(por[i]);
                         }
                         //build route
                         buildDirection(start,latL,end);
                         startPosition+=latL.length+1;
                         //condition of 'while'
                         AllPoints-=latL.length+1;
                         start=end;
                         }
                         //add Endpoint(need to correct)
                         points.add(StringToLatLang(endPoint));

                 }





    }

//build route without intermediate points
        private void buildDirection(String StartPoint,String endPoint) {
           try {
               //Standart Google API method
               DirectionsResult result = DirectionsApi.newRequest(Gcontext)
                       .origin(StartPoint)
                       .destination(endPoint)
                       .mode(TravelMode.WALKING)
                       .language("ru")
                       .await();
               //get steps with geoposition
               DirectionsStep [] Dirstep=result.routes[0].legs[0].steps;
               for (int i=1;i< Dirstep.length;i++) {
                   points.add(Dirstep[i].endLocation);

               }
           }
            catch (ApiException e){
                // daba никогда
            }
             catch (InterruptedException e){
                 // daba так
            }
             catch (IOException e){
                 // daba не делайте
            }
        }
    //build route with intermediate points
        private void buildDirection(String StartPoint,LatLng[] waypoints,String endPoint ) {

        if(waypoints.length>21) throw new MassSizeException("Quantity of waypoints more  than 21");
        try {
            //Standart Google API method
            DirectionsResult result = DirectionsApi.newRequest(Gcontext)

                    .origin(StartPoint)
                    .destination(endPoint)
                    .mode(TravelMode.WALKING)
                    .waypoints(LatLangsToWaypoints(waypoints))//add waypoints
                    .language("ru")
                    .await();
            //get steps with geoposition
            DirectionsStep [] Dirstep=result.routes[0].legs[0].steps;
            for (int i=1;i< Dirstep.length;i++) {
                points.add(Dirstep[i].endLocation);

            }
            points.add(new LatLng(0.0000,0.0000));
            points.add(new LatLng(0.0000,0.0000));
        }
             catch (ApiException e){

            }
             catch (InterruptedException e){

            }
             catch (IOException e){

            }



        }
//Convert Google LatLang type to Google Waypoints type
        private DirectionsApiRequest.Waypoint [] LatLangsToWaypoints(LatLng [] waypoints){
            DirectionsApiRequest.Waypoint [] lat=new DirectionsApiRequest.Waypoint [waypoints.length];
            for (int i=0 ;i<waypoints.length;i++){
                lat[i]=new DirectionsApiRequest.Waypoint(  waypoints[i], false);
            }
            return lat;
        }


        private LatLng StringToLatLang(String point){
        String [] strMas=point.split(",");
        if(strMas.length>2){
            return null;
        }
        return new LatLng(Double.valueOf(strMas[0]),Double.valueOf(strMas[1]));
    }
}
