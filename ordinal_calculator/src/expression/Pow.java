package expression;

public class Pow implements Ordinal {
    private final Ordinal base;

    private final Ordinal deg;

    public Pow(Ordinal base, Ordinal deg) {
        this.base = base;
        this.deg = deg;
    }

    public Ordinal getBase() {
        return base;
    }

    public Ordinal getDeg() {
        return deg;
    }

    @Override
    public String toString() {
        return "((" + base.toString() + ")^" + deg + ")";
    }
}
