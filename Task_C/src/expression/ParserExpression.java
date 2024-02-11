package expression;

public class ParserExpression {
    String line;
    int pos;

    public ParserExpression(String line) {
        this.line = line + "#";
        pos = 0;
    }

    public String parseExpression() {
        return e();
    }

    private String e() {
        String x = dij();
        if (skip("->")) {
            x = "(->," + x + "," + e() + ")";
        }
        return x;
    }

    private String dij() {
        StringBuilder x = new StringBuilder(con());
        while (skip("|")) {
            x = new StringBuilder("(|," + x + "," + con() + ")");
        }
        return x.toString();
    }

    private String con() {
        StringBuilder x = new StringBuilder(nt());
        while (skip("&")) {
            x = new StringBuilder("(&," + x + "," + nt() + ")");
        }
        return x.toString();
    }

    private String nt() {
        if (skip("(")) {
            String x = e();
            skip(")");
            return x;
        }
        if (skip("!")) {
            return "(!," + nt() + ")";
        }
        StringBuilder x = new StringBuilder();
        while (Character.isDigit(line.charAt(pos)) || Character.isLetter(line.charAt(pos)) || line.charAt(pos) == '\'') {
            x.append(line.charAt(pos));
            pos += 1;
        }
        return x.toString();
    }

    private boolean skip(String s) {
        if (line.startsWith(s, pos)) {
            pos += s.length();
            return true;
        }
        return false;
    }

    public static String normalizeExpr(String expr) {
        if (expr.charAt(0) == '(') {
            Operation operation = new Operation(expr);
            if (operation.numb == 1) {
                return "!" + normalizeExpr(operation.operand1);
            } else {
                return "(" + normalizeExpr(operation.operand1) + operation.operation + normalizeExpr(operation.operand2) + ")";
            }
        }
        return expr;
    }

    public static String parseExpression(String line) {
        return new ParserExpression(line).parseExpression();
    }
}