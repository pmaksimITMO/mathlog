package expression;

import java.util.Objects;

public class Operation {
    public String operation;
    public String operand1;
    public String operand2;
    public int numb;

    public Operation(String statement) {
        operation = statement.substring(1, statement.indexOf(','));
        if (operation.equals("!")) {
            operand1 = statement.substring(statement.indexOf(',') + 1, statement.length() - 1);
            operand2 = "";
            numb = 1;
            return;
        }
        StringBuilder _operand1 = new StringBuilder();
        int countCommas = 0;
        int balance = 0;
        int ind = statement.indexOf(',') + 1;
        while (balance != 0 || countCommas == 0) {
            char sym = statement.charAt(ind);
            _operand1.append(sym);
            if (sym == '(') {
                balance += 1;
            } else if (sym == ')') {
                balance -= 1;
            } else if (sym == ',') {
                countCommas += 1;
            }
            ind += 1;
        }
        if (statement.charAt(ind) != ',') {
            ind -= 1;
            _operand1.deleteCharAt(_operand1.length() - 1);
        }
        operand1 = _operand1.toString();
        operand2 = statement.substring(ind + 1, statement.length() - 1);
        numb = 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation that = (Operation) o;
        return numb == that.numb && Objects.equals(operation, that.operation) && Objects.equals(operand1, that.operand1) && Objects.equals(operand2, that.operand2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, operand1, operand2, numb);
    }
}
