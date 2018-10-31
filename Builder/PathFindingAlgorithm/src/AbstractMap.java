import java.util.ArrayList;

public abstract class AbstractMap implements Map {
    private ArrayList<ArrayList<Integer>> field;
    private int scale;

    abstract void initField(int scale);

    AbstractMap(int scale){
            this.scale = scale;
            initField(scale);
    }

    @Override
    public int get(int x, int y){
        Utils.checkBorders(x, y, scale);

        return field.get(x).get(y);
    }
}
