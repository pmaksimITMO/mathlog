package expression;

public class Multiply implements Ordinal {
    private final Ordinal left, right;

    public Multiply(Ordinal left, Ordinal right) {
        this.left = left;
        this.right = right;
    }

    public Ordinal getLeft() {
        return left;
    }

    public Ordinal getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "*" + right.toString() + ")";
    }
}
