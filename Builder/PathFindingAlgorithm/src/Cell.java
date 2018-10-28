public class Cell {

    private int value;
    private fieldCoordinates parent;
    private boolean isClosed;
    private fieldCoordinates coordinates;

    private Double fx;
    private Double gx;
    private Double hx;

    Cell(int value){
        this.value = value;
        parent = null;
        isClosed = false;
    }

    public boolean isClosed(){
        return isClosed;
    }

    public void updateParameters(fieldCoordinates parent, Double fx, Double gx, Double hx){
        this.parent = parent;
        this.fx = fx;
        this.gx = gx;
        this.hx = hx;
    }

    public Double getFx(){
        return fx;
    }

    public fieldCoordinates getCoordinates() {
        return coordinates;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Double getGx() {
        return gx;
    }
}
