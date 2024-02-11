package expression;

public class Const implements Ordinal {
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
