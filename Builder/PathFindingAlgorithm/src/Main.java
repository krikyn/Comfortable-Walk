import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        final int scale = 1;
        final GeoCoordinates lefUpPoint = new GeoCoordinates(60.02781, 30.18035);
        final GeoCoordinates rightDownPoint = new GeoCoordinates(59.84801, 30.52102);
        final GeoCoordinates startPoint = new GeoCoordinates(60.02781, 30.18035);
        final GeoCoordinates endPoint = new GeoCoordinates(59.84801, 30.52102);


        final Double lat1km = 0.00899;
        final Double lon1km = 0.01793;
        final int defaultNumPointsX = 21;
        final int defaultNumPointsY = 20;

        final int numPointsX = Utils.recountWithNewScale(defaultNumPointsX, scale);
        final int numPointsY = Utils.recountWithNewScale(defaultNumPointsY, scale);

        final FieldCoordinates startCell = Utils.convertGeoToFieldCoordinates(startPoint, scale);
        final FieldCoordinates endCell = Utils.convertGeoToFieldCoordinates(endPoint, scale);


        ArrayList<ArrayList<Cell>> potentialField = new ArrayList<>();
        potentialFieldInitialization(potentialField, numPointsX, numPointsY);
        //TODO implement
        fillPotentialField(potentialField, scale);

        PriorityQueue<Cell> nodes = new PriorityQueue<>(Comparator.comparing(Cell::getFx));

        Cell firstPoint = potentialField.get(startCell.getX()).get(startCell.getY());
        Double firstPointH = calcGlobalH(startPoint, endPoint);
        firstPoint.updateParameters(null, 0d, firstPointH);

        nodes.add(firstPoint);

        while (!nodes.isEmpty()) {
            Cell curNode = nodes.poll();
            if (curNode.getFieldCoordinates().equals(endCell)) {
                System.out.println("Нашли");
            }

            int x = curNode.getFieldCoordinates().getX();
            int y = curNode.getFieldCoordinates().getY();
            potentialField.get(x).get(y).setClosed(true);

            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    int newX = x + offsetX;
                    int newY = y + offsetY;

                    if (newX >= 0 && newY >= 0 && newX < numPointsX && newY < numPointsY) {
                        Cell nextCell = potentialField.get(newX).get(newY);

                        Double tentativeScore = curNode.getGx() + calcGxBetweenPoints(potentialField, curNode, nextCell);

                        if (!nextCell.isClosed() || tentativeScore < nextCell.getGx()) {
                            nextCell.updateParameters(curNode.getFieldCoordinates(),
                                    tentativeScore, calcGlobalH(curNode.getGeoCoordinates(),endPoint));
                            if (!nodes.contains(nextCell)) {
                                nodes.add(nextCell);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Не нашли");
        //TODO функция восстановления пути
    }

    private static Double calcGxBetweenPoints(ArrayList<ArrayList<Cell>> potentialField, Cell from, Cell to) {
        return null;
    }

    private static void fillPotentialField(ArrayList<ArrayList<Cell>> potentialField, int scale) {
        AbstractMap weatherMap = ............
    }

    private static void potentialFieldInitialization(ArrayList<ArrayList<Cell>> potentialField, int x, int y) {
        for (int i = 0; i < x; i++) {
            potentialField.add(new ArrayList<>());
            for (int j = 0; j < y; j++) {
                potentialField.get(i).add(new Cell(0, new FieldCoordinates(i, j), new GeoCoordinates(............)));
            }
        }
    }

    private static Double calcGlobalH(GeoCoordinates startPoint, GeoCoordinates endPoint) {
        return null;
    }
}
