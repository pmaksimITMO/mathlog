package expression;

import java.util.Map;
import java.util.Objects;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toTree() {
        return name;
    }

    @Override
    public boolean isBaseFor(Expression other, Map<Expression, Expression> pool) {
        if (pool.containsKey(this)) {
            return pool.get(this).equals(other);
        } else {
            pool.put(this, other);
            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Variable that = (Variable) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
