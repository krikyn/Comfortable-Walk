import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        final int scale = 1;
        final geoCoordinates lefUpPoint = new geoCoordinates(60.02781, 30.18035);
        final geoCoordinates rightDownPoint = new geoCoordinates(59.84801, 30.52102);
        final Double lat1km = 0.00899;
        final Double lon1km = 0.01793;

        final int defaultPointsX = 20;
        final int defaultPointsY = 21;

        final int numPointsX = 20 * scale;
        final int numPointsY = 21;

        final geoCoordinates startPoint;
        final geoCoordinates endPoint;

        final fieldCoordinates startCell = convertGeoToFieldCoordinates(startPoint, scale);
        final fieldCoordinates endCell = convertGeoToFieldCoordinates(endPoint, scale);

        ArrayList<ArrayList<Cell>> potentialField;

        potentialFieldInitialization(potentialField, scale);

        fillPotentialField(potentialField, scale);

        PriorityQueue<Cell> nodes = new PriorityQueue<>(Comparator.comparing(Cell::getFx));

        Cell firstPoint = potentialField.get(startCell.getX()).get(startCell.getY());
        Double tempH = calcGlobalH(startPoint, endPoint);
        firstPoint.updateParameters(tempH, 0.0, tempH);

        nodes.add(firstPoint);

        while (!nodes.isEmpty()) {
            Cell curNode = nodes.poll();
            if (curNode.getCoordinates().equals(endCell)) {
                System.out.println("Нашли");
            }

            int x = curNode.getCoordinates().getX();
            int y = curNode.getCoordinates().getY();
            potentialField.get(x).get(y).setClosed(true);

            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    int newX = x + offsetX;
                    int newY = y + offsetY;

                    if (checkBorders(newX, newY)){
                        Double tentativeScore = g[current] + d(current, v);
                        Cell consideredCell = potentialField.get(newX).get(newY);
                        if (!consideredCell.isClosed() || tentativeScore < consideredCell.getGx()){
                            consideredCell.updateParameters(curNode.getCoordinates(), hx + tentativeScore,tentativeScore, hx);

                            if (!nodes.contains(consideredCell)){
                                nodes.add(consideredCell);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Не нашли");
    }
}
