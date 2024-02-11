package utils;

import java.util.Objects;

public class ProofNode {
    private String expression;

    public String reasonString;

    public ProofNode(String expression, String reasonString) {
        this.expression = expression;
        this.reasonString = reasonString;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getReasonString() {
        return reasonString;
    }

    public void setReasonString(String reasonString) {
        this.reasonString = reasonString;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, reasonString);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProofNode tmp = (ProofNode) obj;
        return Objects.equals(this.expression, tmp.expression)
                && Objects.equals(this.reasonString, tmp.reasonString);
    }
}
