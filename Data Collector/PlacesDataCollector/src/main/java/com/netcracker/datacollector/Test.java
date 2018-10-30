package com.netcracker.datacollector;

public class Test {

    public void test() {
        double latBegin = 60.02781; // lat-широта-y
        double lngBegin = 30.18035; // lng-долгота-x
        double latKm = 0.00899;
        double lngKm = 0.01793;
        //String result = String.valueOf(latBegin) + "\\" + String.valueOf(lngBegin);
        String[][] centers = new String[21][20];

        for(int i = 0; i < 21; i++) {
            System.out.print("row: " + i + " | ");
            for(int j = 0; j < 20; j++) {
                if(i == 0 && j == 0) {
                    centers[i][j] = String.format("%.5f", latBegin) + "\\" + String.format("%.5f", lngBegin);
                } else {
                    centers[i][j] = String.format("%.5f", latBegin) + "\\" + String.format("%.5f", lngBegin);
                }
                System.out.print(centers[i][j] + " | ");
                lngBegin += lngKm;
            }
            System.out.println('\n');
            latBegin -= latKm;
            lngBegin = 30.18035;
        }

        System.out.println(centers[20][19]);
    }
}
