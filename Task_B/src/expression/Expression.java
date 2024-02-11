package expression;

import java.util.Map;

public interface Expression {
    String toTree();

    boolean isBaseFor(Expression other, Map<Expression, Expression> pool);

    default Expression ModusPonens(Expression other) {return null;}
}
