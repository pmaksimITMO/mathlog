package utils;

import expression.*;

import java.util.ArrayList;
import java.util.List;

public class OrdinalParser {
    public static Ordinal parse(String s) {
        return new Parser(s).getResult();
    }

    private static final class Parser {
        private final String exp;
        private int pos;
        private final Ordinal result;

        public Parser(String exp) {
            this.exp = exp;
            this.pos = 0;
            this.result = expression();
        }

        public Ordinal getResult() {
            return result;
        }

        //<Выражение> ::= <Слагаемое> | <Выражение> ‘+’ <Слагаемое>
        private Ordinal expression() {
            Ordinal tmp = summand();
            while (pos < exp.length() && exp.charAt(pos) == '+') {
                pos++;
                tmp = new Add(tmp, expression());
            }
            return tmp;
        }

        //<Слагаемое> ::= <Умножаемое> | <Слагаемое> ‘*’ <Умножаемое>
        private Ordinal summand() {
            Ordinal tmp = multiplier();
            while (pos < exp.length() && exp.charAt(pos) == '*') {
                pos++;
                tmp = new Multiply(tmp, multiplier());
            }
            return tmp;
        }

        //<Умножаемое> ::= <Терм> | <Терм> ‘^’ <Умножаемое>
        private Ordinal multiplier() {
            List<Ordinal> x = new ArrayList<>();
            x.add(term());
            while (pos < exp.length() && exp.charAt(pos) == '^') {
                pos++;
                x.add(term());
            }
            Ordinal res = x.get(x.size() - 1);
            for (int i = x.size() - 2; i >= 0; i--) {
                res = new Pow(x.get(i), res);
            }
            return res;
        }

        //<Терм> ::= 'w' | {'0'..'9'}+ | ‘(’ <Выражение> ‘)’
        private Ordinal term() {
            Ordinal tmp;
            switch (exp.charAt(pos)) {
                case 'w':
                    pos++;
                    tmp = new Omega();
                    break;
                case '(':
                    pos++;
                    tmp = expression();
                    pos++;
                    break;
                default:
                    int start = pos;
                    while (pos < exp.length() && Character.isDigit(exp.charAt(pos))) {
                        pos++;
                    }
                    tmp = new Const(Integer.parseInt(exp.substring(start, pos)));
                    break;
            }
            return tmp;
        }
    }
}
