package expression;

import java.util.Map;
import java.util.Objects;

public class Negation implements Expression {
    private Expression expression;

    public Negation(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toTree() {
        return "(!" + expression.toTree() + ")";
    }

    @Override
    public boolean isBaseFor(Expression other, Map<Expression, Expression> pool) {
        if (getClass() != other.getClass()) return false;
        Negation that = (Negation) other;
        return expression.isBaseFor(that.expression, pool);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Negation that = (Negation) obj;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return "!" + expression.toString();
    }
}
