package expression;

import java.util.Map;
import java.util.Objects;

public class Implication implements Expression {
    private Expression left, right;

    public Implication(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toTree() {
        return "(->," + left.toTree() + "," + right.toTree() + ")";
    }

    @Override
    public boolean isBaseFor(Expression other, Map<Expression, Expression> pool) {
        if (getClass() != other.getClass()) return false;
        Implication that = (Implication) other;
        return left.isBaseFor(that.left, pool) && right.isBaseFor(that.right, pool);
    }

    @Override
    public Expression ModusPonens(Expression other) {
        return left.equals(other) ? right : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Implication that = (Implication) obj;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "->" + right.toString() + ")";
    }
}
