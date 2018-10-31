class Utils {
    static boolean checkBorders(int x, int y, int scale) {

        assert scale >= 1;
        assert scale % 2 == 0;

        //default field with cells 1x1 km
        int numX = 21;
        int numY = 20;

        for (int i = 0; i < scale / 2; i++) {
            numX = numX * 2 - 1;
            numY = numY * 2 - 1;
        }


        return x >= 0 && y >= 0 && x < numX && y < numY;
    }

    static Integer recountWithNewScale(int original, int scale) {
        int num = original;
        for (int i = 0; i < scale / 2; i++) {
            num = num * 2 - 1;
        }
        return num;
    }

    static FieldCoordinates convertGeoToFieldCoordinates(GeoCoordinates point, int scale) {
        final Double lat1 = 0.00899 / (double) scale;
        final Double lon1 = 0.01793 / (double) scale;

        return new FieldCoordinates((int) Math.floor(point.getX() / lat1), (int) Math.floor(point.getY() / lon1));
    }
}
