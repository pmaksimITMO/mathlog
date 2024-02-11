package expression;

public class Add implements Ordinal {
    private final Ordinal left, right;

    public Add(Ordinal left, Ordinal right) {
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
        return "(" + left.toString() + "+" + right.toString() + ")";
    }
}
