package utils;


import java.util.Objects;

public class ProofNode {
    public String rawExpression;
    public String reason;

    public ProofNode(String rawExpression, String reason) {
        this.rawExpression = rawExpression;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProofNode proofNode = (ProofNode) o;
        return Objects.equals(rawExpression, proofNode.rawExpression) && Objects.equals(reason, proofNode.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawExpression, reason);
    }
}