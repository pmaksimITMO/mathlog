package utils;

import expression.Expression;
import expression.Implication;

import java.util.*;

public class Node {
    private final Expression expression;

    private final Map<String, Integer> hypothesis;

    private Pair<Map<String, Integer>, String> extended;

    public String reasonString;

    private List<String> extList = new ArrayList<>();

    public Node(Expression expression, Map<String, Integer> hypothesis, String reasonString) {
        this.expression = expression;
        this.hypothesis = hypothesis;
        this.reasonString = reasonString;
        makeAllHyp();
    }

    public Node(Expression expression, Map<String, Integer> hypothesis, String reasonString,
                Pair<Map<String, Integer>, String> extended, List<String> extList) {
        this.expression = expression;
        this.hypothesis = hypothesis;
        this.reasonString = reasonString;
        this.extended = extended;
        this.extList = extList;
    }

    public Expression getExpression() {
        return expression;
    }

    public Map<String, Integer> getHypothesis() {
        return hypothesis;
    }

    private void makeAllHyp() {
        Map<String, Integer> extended_ = new HashMap<>(hypothesis);
        Expression shortExpression = expression;
        while (shortExpression instanceof Implication) {
            Expression k = ((Implication) shortExpression).getLeft();
            extended_.put(k.toString(), extended_.getOrDefault(k.toString(), 0) + 1);
            extList.add(((Implication) shortExpression).getLeft().toString());
            shortExpression = ((Implication) shortExpression).getRight();
        }
        extList.add(shortExpression.toString());
        extended = new Pair<>(extended_, shortExpression.toString());
    }

    public List<String> getExtList() {
        return extList;
    }

    public Pair<Map<String, Integer>, String> getExtended() {
        return extended;
    }

    public String getReasonString() {
        return reasonString;
    }
}
