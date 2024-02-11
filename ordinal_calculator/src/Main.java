import expression.*;
import utils.CNF;
import utils.OrdinalParser;
import utils.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(System.out)
//                BufferedReader br = new BufferedReader(
//                        new InputStreamReader(new FileInputStream("input.txt")));
//                PrintWriter out = new PrintWriter("output.txt");
        ) {
            String line = br.readLine();
//            while (line != null) {
                CNF first = makeCNF(OrdinalParser.parse(line.split("=")[0]));
                CNF second = makeCNF(OrdinalParser.parse(line.split("=")[1]));
//                out.println("CNF for first: " + first);
//                out.println("CNF for second: " + second);
                if (equalsKNF(first, second)) {
                    out.println("Равны");
                } else {
                    out.println("Не равны");
                }
//                line = br.readLine();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CNF makeCNF(Ordinal ordinal) {
        if (ordinal instanceof Const) {
            return new CNF(
                    new ArrayList<>(),
                    ((Const) ordinal).getValue()
            );
        } else if (ordinal instanceof Omega) {
            return new CNF(
                    List.of(new Pair<>(new CNF(new ArrayList<>(), 1), 1)),
                    0
            );
        } else if (ordinal instanceof Add) {
            return add(
                    makeCNF(((Add) ordinal).getLeft()),
                    makeCNF(((Add) ordinal).getRight())
            );
        } else if (ordinal instanceof Multiply) {
            return mul(
                    makeCNF(((Multiply) ordinal).getLeft()),
                    makeCNF(((Multiply) ordinal).getRight())
            );
        } else {
            return pow(
                    makeCNF(((Pow) ordinal).getBase()),
                    makeCNF(((Pow) ordinal).getDeg())
            );
        }
    }

    public static CNF add(CNF x, CNF y) {
        if (x.params.isEmpty() && y.params.isEmpty()) {
            return new CNF(
                    new ArrayList<>(),
                    x.free + y.free
            );
        } else if (y.params.isEmpty()) {
            return new CNF(
                    x.params,
                    x.free + y.free
            );
        } else if (x.params.isEmpty()) {
            return y;
        } else {
            Compare cmp = compare(x.params.get(0).getFirst(), y.params.get(0).getFirst());
            if (cmp == Compare.Less) {
                return y;
            } else if (cmp == Compare.Equals) {
                List<Pair<CNF, Integer>> tmp = new ArrayList<>(y.params);
                tmp.set(0,
                        new Pair<>(
                                x.params.get(0).getFirst(),
                                x.params.get(0).getSecond() + y.params.get(0).getSecond()
                        )
                );
                return new CNF(
                        tmp,
                        y.free
                );
            } else {
                CNF x1 = add(new CNF(x.params.subList(1, x.params.size()), x.free), y);
                List<Pair<CNF, Integer>> tmp = new ArrayList<>(List.of(x.params.get(0)));
                tmp.addAll(x1.params);
                return new CNF(
                        tmp,
                        x1.free
                );
            }
        }
    }

    public static CNF mul(CNF x, CNF y) {
        if (x.params.isEmpty() && x.free == 0) return x;
        if (y.params.isEmpty() && y.free == 0) return y;
        if (x.params.isEmpty()) {
            return new CNF(
                    y.params,
                    x.free * y.free
            );
        }
        if (y.params.isEmpty()) {
            List<Pair<CNF, Integer>> tmp = new ArrayList<>(x.params);
            tmp.set(0,
                    new Pair<>(
                            x.params.get(0).getFirst(),
                            x.params.get(0).getSecond() * y.free)
            );
            return new CNF(
                    tmp,
                    x.free
            );
        }
        CNF x1 = mul(x, new CNF(y.params.subList(1, y.params.size()), y.free));
        List<Pair<CNF, Integer>> tmp = new ArrayList<>(List.of(new Pair<>(
                add(x.params.get(0).getFirst(), y.params.get(0).getFirst()),
                y.params.get(0).getSecond()
        )));
        tmp.addAll(x1.params);
        return new CNF(
                tmp,
                x1.free
        );
    }

    public static CNF pow(CNF x, CNF y) {
        // x^0 = 1
        if (y.params.isEmpty() && y.free == 0) return new CNF(new ArrayList<>(), 1);
        // 0^x = 0
        if (x.params.isEmpty() && x.free == 0) return x;
        // 1^x = 1
        if (x.params.isEmpty() && x.free == 1) return new CNF(new ArrayList<>(), 1);
        // x^c = x * x * ... * x
        if (y.params.isEmpty()) {
            if (y.free == 1) return x;
            else return mul(x, pow(x, new CNF(y.params, y.free - 1)));
        }
        // c^x
        if (x.params.isEmpty()) {
            List<Pair<CNF, Integer>> tmp = new ArrayList<>();
            Integer tmp_free = 0;
            for (Pair<CNF, Integer> q : y.params) {
                if (q.getFirst().params.isEmpty()) {
                    int val = q.getFirst().free - 1;
                    if (val == 0) {
                        tmp_free = q.getSecond();
                    } else {
                        tmp.add(new Pair<>(new CNF(new ArrayList<>(), val), q.getSecond()));
                    }
                } else {
                    tmp.add(q);
                }
            }
            return new CNF(
                    new ArrayList<>(List.of(new Pair<>(
                            new CNF(tmp, tmp_free),
                            (int) Math.pow(x.free, y.free)
                    ))),
                    0
            );
        }

        return mul(
                new CNF(new ArrayList<>(List.of(
                        new Pair<>(
                                mul(
                                        x.params.get(0).getFirst(),
                                        new CNF(y.params, 0)
                                ),
                                1
                        ))),
                        0
                ),
                pow(x, new CNF(new ArrayList<>(), y.free))
        );
    }

    public static boolean equalsKNF(CNF x, CNF y) {
        if (x.params.isEmpty() && y.params.isEmpty()) return Objects.equals(x.free, y.free);
        if (x.params.isEmpty() || y.params.isEmpty()) return false;
        return equalsKNF(x.params.get(0).getFirst(), y.params.get(0).getFirst())
                && (Objects.equals(x.params.get(0).getSecond(), y.params.get(0).getSecond()))
                && equalsKNF(
                new CNF(x.params.subList(1, x.params.size()), x.free),
                new CNF(y.params.subList(1, y.params.size()), y.free)
        );
    }

    public static Compare compare(CNF x, CNF y) {
        if (x.params.isEmpty() && y.params.isEmpty()) {
            return compare(x.free, y.free);
        }
        if (x.params.isEmpty()) return Compare.Less;
        if (y.params.isEmpty()) return Compare.Greater;
        Compare q = compare(x.params.get(0).getFirst(), y.params.get(0).getFirst());
        Compare p = compare(x.params.get(0).getSecond(), y.params.get(0).getSecond());
        if (q != Compare.Equals) return q;
        if (p != Compare.Equals) return p;
        return compare(
                new CNF(x.params.subList(1, x.params.size()), x.free),
                new CNF(y.params.subList(1, y.params.size()), y.free)
        );
    }

    public static Compare compare(Integer x, Integer y) {
        if (x < y) return Compare.Less;
        else if (x.equals(y)) return Compare.Equals;
        else return Compare.Greater;
    }

    public enum Compare {
        Less, Greater, Equals
    }
}
