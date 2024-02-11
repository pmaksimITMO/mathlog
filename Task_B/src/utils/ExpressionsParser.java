package utils;

import expression.*;

public class ExpressionsParser {
    public static Expression parse() throws Exception {
        try (FastScanner in = new FastScanner()) {
            String statement = in.next();
            return new Parser(statement).getResult();
        }
    }

    public static Expression parse(String s) {
        return new Parser(s).getResult();
    }

    private static final class Parser {
        private final String exp;
        private int pos;
        private final Expression result;

        public Parser(String exp) {
            this.exp = exp;
            this.pos = 0;
            this.result = expression();
        }

        public Expression getResult() {
            return result;
        }

        //<Выражение> ::= <Дизъюнкция> | <Дизъюнкция> ‘->’ <Выражение>
        private Expression expression() {
            Expression tmp = disjunction();
            while (pos < exp.length() && exp.charAt(pos) == '-') {
                pos += 2;
                tmp = new Implication(tmp, expression());
            }
            return tmp;
        }

        //<Дизъюнкция> ::= <Конъюнкция> | <Дизъюнкция> ‘|’ <Конъюнкция>
        private Expression disjunction() {
            Expression tmp = conjunction();
            while (pos < exp.length() && exp.charAt(pos) == '|') {
                pos++;
                tmp = new Disjunction(tmp, conjunction());
            }
            return tmp;
        }

        //<Конъюнкция> ::= <Отрицание> | <Конъюнкция> ‘&’ <Отрицание>
        private Expression conjunction() {
            Expression tmp = negation();
            while (pos < exp.length() && exp.charAt(pos) == '&') {
                pos++;
                tmp = new Conjunction(tmp, negation());
            }
            return tmp;
        }

        //<Отрицание> ::= ‘!’ <Отрицание> | <Переменная> | ‘(’ <Выражение> ‘)’
        private Expression negation() {
            Expression tmp;
            switch (exp.charAt(pos)) {
                case '!':
                    pos++;
                    tmp = new Negation(negation());
                    break;
                case '(':
                    pos++;
                    tmp = expression();
                    pos++;
                    break;
                default:
                    int start = pos;
                    while (pos < exp.length() &&
                            (Character.isLetterOrDigit(exp.charAt(pos)) || exp.charAt(pos) == '\'')) {
                        pos++;
                    }
                    tmp = new Variable(exp.substring(start, pos));
                    break;
            }
            return tmp;
        }
    }
}
