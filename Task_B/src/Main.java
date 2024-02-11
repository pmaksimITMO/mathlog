import expression.Expression;
import utils.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static final String[] AXIOMS = {
            "A->B->A",
            "(A->B)->(A->B->C)->(A->C)",
            "A->B->A&B",
            "A&B->A",
            "A&B->B",
            "A->A|B",
            "B->A|B",
            "(A->C)->(B->C)->(A|B->C)",
            "(A->B)->(A->!B)->!A",
            "!!A->A"
    };

    public static void main(String[] args) throws Exception {
        List<Expression> axioms = new ArrayList<>();
        List<Node> lines = new ArrayList<>();
        Map<Pair<String, Map<String, Integer>>, int[]> forMP = new HashMap<>();
        Map<Pair<Map<String, Integer>, String>, Integer> forDeduction = new HashMap<>();
        List<String> raw = new ArrayList<>();
        for (String ax : AXIOMS) {
            axioms.add(ExpressionsParser.parse(ax));
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            int id = 1;
            while ((line = br.readLine()) != null) {
                raw.add(line);
                String reasonString = "Incorrect";
                boolean hasAnnotation = false;
                Expression expression = ExpressionsParser.parse(
                        line.replaceAll("\\s+", "").split("\\|-")[1]
                );

                //Axiom checking
                for (int j = 0; j < axioms.size(); ++j) {
                    if (axioms.get(j).isBaseFor(expression, new HashMap<>())) {
                        hasAnnotation = true;
                        reasonString = "Axiom " + (j + 1);
                        break;
                    }
                }

                //Hypothesis checking
                String[] tmp = line.replaceAll("\\s+", "").split("\\|-")[0].split(",");
                Map<String, Integer> hypothesis = new HashMap<>();
                for (int i = 0; i < tmp.length; i++) {
                    if (tmp[i].equals("")) continue;
                    Expression cur = ExpressionsParser.parse(tmp[i]);
                    hypothesis.put(cur.toString(), hypothesis.getOrDefault(cur.toString(), 0) + 1);
                    if (!hasAnnotation && cur.equals(expression)) {
                        hasAnnotation = true;
                        reasonString = "Hyp " + (i + 1);
                    }
                }

                Node w = new Node(expression, hypothesis, reasonString);

                //Modus Ponens checking
                if (!hasAnnotation && forMP.containsKey(new Pair<>(expression.toString(), hypothesis))) {
                    int[] pos = forMP.get(new Pair<>(expression.toString(), hypothesis));
                    hasAnnotation = true;
                    reasonString = "MP " + pos[0] + " " + pos[1];
                }

                //Deduction checking
                if (!hasAnnotation) {
                    Pair<Map<String, Integer>, String> extended = w.getExtended();
                    if (forDeduction.containsKey(extended)) {
                        reasonString = "Ded " + forDeduction.get(extended);
                    }
                }

                //Update Modus Ponens variants
                for (int j = 0; j < lines.size(); ++j) {
                    if (!Objects.equals(hypothesis, lines.get(j).getHypothesis())) continue;
                    Expression t = lines.get(j).getExpression().ModusPonens(expression);
                    if (t != null) {
                        forMP.put(new Pair<>(t.toString(), hypothesis), new int[]{id, j + 1});
                    }
                    t = expression.ModusPonens(lines.get(j).getExpression());
                    if (t != null) {
                        forMP.put(new Pair<>(t.toString(), hypothesis), new int[]{j + 1, id});
                    }
                }

                //Update deduction variants
                forDeduction.putIfAbsent(w.getExtended(), id);

                w.reasonString = reasonString;
                lines.add(w);
                id++;
            }
        }

        List<ProofNode> proof = makeProof(lines, raw, lines.size() - 1);
        System.out.println(raw.get(raw.size() - 1));
        for (ProofNode x : proof) {
            System.out.println(x.getExpression());
        }
    }

    public static String normalize(String expr) {
        return ExpressionsParser.parse(expr).toString();
    }

    public static List<ProofNode> makeProof(List<Node> lines, List<String> raw, int id) {
        Node cur = lines.get(id);
        if (cur.reasonString.charAt(0) == 'A') {
            List<ProofNode> res1 = new ArrayList<>();
            res1.add(new ProofNode(
                    normalize(raw.get(id).split("\\|-")[1]),
                    "H/A"));
            return res1;
        } else if (cur.reasonString.charAt(0) == 'H') {
            List<ProofNode> res2 = new ArrayList<>();
            res2.add(new ProofNode(
                    normalize(raw.get(id).split("\\|-")[1]),
                    "H/A"));
            return res2;
        } else if (cur.reasonString.charAt(0) == 'M') {
            List<ProofNode> p1 = makeProof(
                    lines,
                    raw,
                    Integer.parseInt(cur.getReasonString().split("\\s+")[1]) - 1
            );
            List<ProofNode> p2 = makeProof(
                    lines,
                    raw,
                    Integer.parseInt(cur.getReasonString().split("\\s+")[2]) - 1
            );
            for (ProofNode it : p2) {
                if (it.getReasonString().charAt(0) == 'M') {
                    int x1 = Integer.parseInt(it.getReasonString().split("\\s+")[1]);
                    int x2 = Integer.parseInt(it.getReasonString().split("\\s+")[2]);
                    it.setReasonString("MP " + (x1 + p1.size()) + " " + (x2 + p1.size()));
                }
            }
            List<ProofNode> res = new ArrayList<>(p1);
            res.addAll(p2);
            res.add(new ProofNode(
                    normalize(raw.get(id).split("\\|-")[1]),
                    "MP " + p1.size() + " " + res.size()));
            return res;
        } else {
            return deduction(
                    lines,
                    raw,
                    id,
                    Integer.parseInt(cur.getReasonString().split("\\s+")[1]) - 1
            );
        }
    }

    private static List<ProofNode> deduction(
            List<Node> lines, List<String> raw,
            int id_n, int id_o) {
        List<String> expNew = new ArrayList<>(lines.get(id_n).getExtList());
        List<String> expOld = new ArrayList<>(lines.get(id_o).getExtList());
        List<ProofNode> curProof = makeProof(lines, raw, id_o);
        int i = expNew.size() - 2;
        int j = expOld.size() - 2;
        while (i >= 0 && j >= 0 && expNew.get(i).equals(expOld.get(j))) {
            i--;
            j--;
        }
        for (int k = 0; k <= j; k++) {
            String hyp = expOld.get(k);
            String rest = String.join("->", expOld.subList(k + 1, expOld.size()));
            curProof = addHyp(hyp, rest, curProof);
        }
        while (i >= 0) {
            curProof = deleteHyp(expNew.get(i), curProof);
            i--;
        }
        return curProof;
    }

    private static List<ProofNode> deleteHyp(
            String hyp, List<ProofNode> curProof) {
        List<ProofNode> newProof = new ArrayList<>();
        int[] mapping = new int[curProof.size()];
        for (int i = 0; i < curProof.size(); i++) {
            String expression = curProof.get(i).getExpression();
            int n = newProof.size();
            if (expression.equals(hyp)) {
                newProof.add(new ProofNode(hyp + "->(" + hyp + "->" + hyp + ")", "Axiom"));
                newProof.add(new ProofNode(
                        "(" + hyp + "->(" + hyp + "->" + hyp + "))->" +
                                "(" + hyp + "->(" + hyp + "->" + hyp + ")->" + hyp + ")->" +
                                "(" + hyp + "->" + hyp + ")",
                        "Axiom"));
                newProof.add(new ProofNode(
                        "(" + hyp + "->(" + hyp + "->" + hyp + ")->" + hyp + ")->" +
                                "(" + hyp + "->" + hyp + ")",
                        "MP " + (n + 1) + " " + (n + 2)));
                newProof.add(new ProofNode(
                        hyp + "->(" + hyp + "->" + hyp + ")->" + hyp,
                        "Axiom"));
                newProof.add(new ProofNode(hyp + "->" + hyp, "MP " + (n + 4) + " " + (n + 3)));
                mapping[i] = n + 5;
            } else if (curProof.get(i).reasonString.charAt(0) == 'M') {
                String reason = curProof.get(i).getReasonString();
                int j = Integer.parseInt(reason.split("\\s+")[1]) - 1;
                int k = Integer.parseInt(reason.split("\\s+")[2]) - 1;
                String dj = curProof.get(j).getExpression();
                newProof.add(new ProofNode(
                        "(" + hyp + "->" + dj + ")->" +
                                "(" + hyp + "->" + dj + "->" + expression + ")->" +
                                "(" + hyp + "->" + expression + ")",
                        "Axiom"));
                newProof.add(new ProofNode(
                        "(" + hyp + "->" + dj + "->" + expression + ")->" +
                                "(" + hyp + "->" + expression + ")",
                        "MP " + (mapping[j]) + " " + (n + 1)));
                newProof.add(new ProofNode(
                        hyp + "->" + expression,
                        "MP " + (mapping[k]) + " " + (n + 2)));
                mapping[i] = n + 3;
            } else {
                newProof.add(new ProofNode(
                        expression + "->" + hyp + "->" + expression, "Axiom"));
                newProof.add(new ProofNode(
                        expression, "H/A"));
                newProof.add(new ProofNode(
                        hyp + "->" + expression, "MP " + (n + 2) + " " + (n + 1)));
                mapping[i] = n + 3;
            }
        }
        for (ProofNode node : newProof) {
            node.setExpression(normalize(node.getExpression()));
        }
        return newProof;
    }

    private static List<ProofNode> addHyp(String hyp, String rest,
                                          List<ProofNode> curProof) {
        int n = curProof.size();
        List<ProofNode> res = new ArrayList<>(curProof);
        res.add(new ProofNode(hyp, "Hypothesis"));
        res.add(new ProofNode(rest, "MP " + (n + 1) + " " + n));
        return res;
    }
}
