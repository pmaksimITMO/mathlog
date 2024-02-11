package utils;

import expression.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static expression.ParserExpression.normalizeExpr;
import static expression.ParserExpression.parseExpression;

public class Node {
    public Map<String, Integer> context;
    public String statement;
    public String reason = "[Incorrect]";
    public Pair<Map<String, Integer>, String> enhancedContext;
    public Map<String, Integer> contextCount;
    public String rawExpression;
    public Map<String, Integer> rawContextCount;
    public ArrayList<String> enhancedContextList;
    public Node(String line) {
        this.context = new HashMap<>();
        this.contextCount = new HashMap<>();
        this.rawContextCount = new HashMap<>();
        int pos = 0;
        int num = 1;
        while (!line.startsWith("|-", pos)) {
            StringBuilder rawExpr = new StringBuilder();
            while (!line.startsWith(",", pos) && !line.startsWith("|-", pos)) {
                rawExpr.append(line.charAt(pos));
                pos += 1;
            }
            String expr = parseExpression(rawExpr.toString());
            context.put(expr, num);
            contextCount.put(expr, contextCount.getOrDefault(expr, 0) + 1);
            rawContextCount.put(rawExpr.toString(), rawContextCount.getOrDefault(rawExpr.toString(), 0) + 1);
            num += 1;
            if (line.startsWith(",", pos)) {
                pos += 1;
            }
        }
        String _statement = parseExpression(line.substring(pos + 2));
        rawExpression = line.substring(pos + 2);
        Map<String, Integer> _enhancedContext = new HashMap<>(contextCount);
        enhancedContextList = new ArrayList<>();
        statement = _statement;
        while (true) {
            if (statement.length() < 3 || !statement.startsWith("->", 1)) {
                enhancedContextList.add(normalizeExpr(statement));
                break;
            }
            Operation operation = new Operation(statement);
            _enhancedContext.put(operation.operand1, _enhancedContext.getOrDefault(operation.operand1, 0) + 1);
            enhancedContextList.add(normalizeExpr(operation.operand1));
            statement = operation.operand2;
        }
        enhancedContext = new Pair<>(_enhancedContext, statement);
        statement = _statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node statement1 = (Node) o;
        return Objects.equals(context, statement1.context) && Objects.equals(statement, statement1.statement) && Objects.equals(reason, statement1.reason) && Objects.equals(enhancedContext, statement1.enhancedContext) && Objects.equals(contextCount, statement1.contextCount) && Objects.equals(rawExpression, statement1.rawExpression) && Objects.equals(rawContextCount, statement1.rawContextCount) && Objects.equals(enhancedContextList, statement1.enhancedContextList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, statement, reason, enhancedContext, contextCount, rawExpression, rawContextCount, enhancedContextList);
    }
}