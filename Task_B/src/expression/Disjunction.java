package expression;

import java.util.Map;
import java.util.Objects;

public class Disjunction implements Expression {
    private Expression left, right;

    public Disjunction(Expression left, Expression right) {
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
        return "(|," + left.toTree() + "," + right.toTree() + ")";
    }

    @Override
    public boolean isBaseFor(Expression other, Map<Expression, Expression> pool) {
        if (getClass() != other.getClass()) return false;
        Disjunction that = (Disjunction) other;
        return left.isBaseFor(that.left, pool) && right.isBaseFor(that.right, pool);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Disjunction that = (Disjunction) obj;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "|" + right.toString() + ")";
    }
}
