import expression.Operation;
import utils.Node;
import utils.Pair;
import utils.ProofNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import static expression.ParserExpression.normalizeExpr;
import static expression.ParserExpression.parseExpression;

public class Main {
    static String[] axioms = {
            parseExpression("a->b->a"),
            parseExpression("(a->b)->(a->b->c)->(a->c)"),
            parseExpression("a->b->a&b"),
            parseExpression("a&b->a"),
            parseExpression("a&b->b"),
            parseExpression("a->a|b"),
            parseExpression("b->a|b"),
            parseExpression("(a->c)->(b->c)->(a|b->c)"),
            parseExpression("(a->b)->(a->!b)->(!a)"),
            parseExpression("!!a->a")
    };

    public static void main(String[] args) throws FileNotFoundException {
        List<Node> statements = new ArrayList<>();
        List<String> rawStatements = new ArrayList<>();
//        Scanner sc = new Scanner(System.in);
        Scanner sc = new Scanner(new File("input.txt"));
        while (sc.hasNext()) {
            String line = sc.nextLine();
            statements.add(new Node(line));
            rawStatements.add(line);
        }

        Map<Map<String, Integer>, Map<String, List<Integer>>> statementsSet = new HashMap<>();
        Map<Map<String, Integer>, Map<String, List<Pair<Integer, String>>>> statementsSecondSet = new HashMap<>();
        Map<Pair<Map<String, Integer>, String>, Integer> mapForDeduction = new HashMap<>();
        for (int i = 0; i < statements.size(); i++) {
            Node statement = statements.get(i);
            if (!checkForAxiom(statement)) {
                if (!checkForHyp(statement)) {
                    if (!checkForMP(statements, i, statementsSet, statementsSecondSet)) {
                        checkForDeduction(statements, i, mapForDeduction);
                    }
                }
            }
            mapForDeduction.putIfAbsent(statement.enhancedContext, i);
            statementsSet.putIfAbsent(statement.contextCount, new HashMap<>());
            statementsSecondSet.putIfAbsent(statement.contextCount, new HashMap<>());
            Map<String, List<Integer>> statementSet = statementsSet.get(statement.contextCount);
            Map<String, List<Pair<Integer, String>>> statementSecondSet = statementsSecondSet.get(statement.contextCount);
            String stC = statement.statement;
            statementSet.putIfAbsent(stC, new ArrayList<>());
            statementSet.get(stC).add(i);
            if (stC.charAt(0) != '(') {
                continue;
            }
            Operation operation = new Operation(stC);
            if (operation.operation.equals("->")) {
                statementSecondSet.putIfAbsent(operation.operand2, new ArrayList<>());
                statementSecondSet.get(operation.operand2).add(new Pair<>(i, operation.operand1));
            }
        }

        List<ProofNode> proof = getProof(statements, statements.size() - 1);
        try (PrintWriter out = new PrintWriter("output.txt")) {
            out.println(rawStatements.get(rawStatements.size() - 1));
            for (ProofNode p : proof) {
                out.println(p.rawExpression);
            }
        } catch (Exception e) {
            //Do nothing.
        }
    }

    private static void checkForDeduction(List<Node> statements, int index, Map<Pair<Map<String, Integer>, String>, Integer> mapForDeduction) {
        Node statement = statements.get(index);
        if (mapForDeduction.containsKey(statement.enhancedContext)) {
            statement.reason = "Ded " + (mapForDeduction.get(statement.enhancedContext) + 1);
        }
    }

    private static boolean checkForMP(List<Node> statements, int index, Map<Map<String, Integer>, Map<String, List<Integer>>> statementsSet, Map<Map<String, Integer>, Map<String, List<Pair<Integer, String>>>> statementsSecondSet) {
        String st = statements.get(index).statement;
        Map<String, Integer> context = statements.get(index).contextCount;
        if (statementsSet.containsKey(context)) {
            Map<String, List<Integer>> statementSet = statementsSet.get(context);
            Map<String, List<Pair<Integer, String>>> statementSecondSet = statementsSecondSet.get(context);
            if (statementSecondSet.containsKey(st)) {
                List<Pair<Integer, String>> arr = statementSecondSet.get(st);
                for (Pair<Integer, String> pair : arr) {
                    int i = pair.getFirst();
                    String operand = pair.getSecond();
                    if (statementSet.containsKey(operand)) {
                        int num = statementSet.get(operand).get(0);
                        statements.get(index).reason = "MP " + (num + 1) + " " + (i + 1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkForHyp(Node line) {
        String statement = line.statement;
        Map<String, Integer> context = line.context;
        if (context.containsKey(statement)) {
            line.reason = "Hyp " + context.get(statement);
            return true;
        }
        return false;
    }

    private static boolean checkForAxiom(Node line) {
        String statement = line.statement;
        if (statement.charAt(0) != '(') {
            return false;
        }
        for (int i = 0; i < axioms.length; i++) {
            String axiom = axioms[i];
            if (equalsStatementToAxiom(statement, axiom, null)) {
                line.reason = "Ax " + (i + 1);
                return true;
            }
        }
        return false;
    }

    private static boolean equalsStatementToAxiom(String statement, String axiom, Map<String, String> elements) {
        if (elements == null) {
            elements = new HashMap<>();
        }
        if (axiom.charAt(0) != '(') {
            if (elements.containsKey(axiom)) {
                return statement.equals(elements.get(axiom));
            }
            elements.put(axiom, statement);
            return true;
        }
        if (statement.charAt(0) == '(' && axiom.charAt(0) == '(') {
            Operation axOperation = new Operation(axiom);
            String[] axOperands = {axOperation.operand1, axOperation.operand2};
            Operation operation = new Operation(statement);
            String[] operands = {operation.operand1, operation.operand2};
            if (!axOperation.operation.equals(operation.operation)) {
                return false;
            }
            for (int i = 0; i < axOperation.numb; i++) {
                String axOperand = axOperands[i];
                String operand = operands[i];
                boolean b = equalsStatementToAxiom(operand, axOperand, elements);
                if (!b) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static List<ProofNode> getProof(List<Node> statements, int index) {
        Node statement = statements.get(index);
        String reason = statement.reason;
        if (reason.charAt(0) == 'D') {
            return constructDeduction(
                    index,
                    Integer.parseInt(reason.split("\\s+")[1]) - 1,
                    statements
            );
        } else if (reason.charAt(0) == 'M') {
            List<ProofNode> proof1 = getProof(statements, Integer.parseInt(reason.split("\\s+")[1]));
            List<ProofNode> proof2 = getProof(statements, Integer.parseInt(reason.split("\\s+")[2]));
            for (ProofNode proofNode : proof2) {
                reason = proofNode.reason;
                if (reason.charAt(0) == 'M') {
                    int x1 = Integer.parseInt(reason.split("\\s+")[1]);
                    int x2 = Integer.parseInt(reason.split("\\s+")[2]);
                    proofNode.reason = "MP " + (x1 + proof1.size()) + " " + (x2 + proof1.size());
                }
            }
            List<ProofNode> proof = new ArrayList<>();
            proof.addAll(proof1);
            proof.addAll(proof2);
            proof.add(new ProofNode(
                    normalize(statement.rawExpression),
                    "MP " + (proof1.size()) + " " + (proof.size())
            ));
            return proof;
        } else {
            List<ProofNode> proof = new ArrayList<>();
            proof.add(new ProofNode(
                    normalize(statement.rawExpression),
                    "Hyp/Ax"
            ));
            return proof;
        }
    }

    private static List<ProofNode> constructDeduction(int index_new, int index_old, List<Node> statements) {
        List<String> statementNew = statements.get(index_new).enhancedContextList;
        List<String> statementOld = statements.get(index_old).enhancedContextList;
        List<ProofNode> curProof = getProof(statements, index_old);
        int i = statementNew.size() - 2;
        int j = statementOld.size() - 2;
        while (i >= 0 && j >= 0 && statementNew.get(i).equals(statementOld.get(j))) {
            i -= 1;
            j -= 1;
        }
        for (int k = 0; k <= j; k++) {
            String hyp = statementOld.get(k);
            String rest = String.join("->", statementOld.subList(k + 1, statementOld.size()));
            curProof = addHyp(hyp, rest, curProof);
        }
        while (i >= 0) {
            curProof = deleteHyp(statementNew.get(i), curProof);
            i--;
        }
        return curProof;
    }

    private static List<ProofNode> deleteHyp(String hyp, List<ProofNode> curProof) {
        List<ProofNode> newProof = new ArrayList<>();
        int[] mapping = new int[curProof.size()];
        for (int i = 0; i < curProof.size(); i++) {
            ProofNode tmp = curProof.get(i);
            String expression = tmp.rawExpression;
            int n = newProof.size();
            if (expression.equals(hyp)) {
                newProof.add(new ProofNode(
                        hyp + "->(" + hyp + "->" + hyp + ")",
                        "Ax"
                ));
                newProof.add(new ProofNode(
                        "(" + hyp + "->(" + hyp + "->" + hyp + "))->" +
                                "(" + hyp + "->(" + hyp + "->" + hyp + ")->" + hyp + ")->" +
                                "(" + hyp + "->" + hyp + ")",
                        "Ax"
                ));
                newProof.add(new ProofNode(
                        "(" + hyp + "->(" + hyp + "->" + hyp + ")->" + hyp + ")->" +
                                "(" + hyp + "->" + hyp + ")",
                        "MP " + (n + 1) + " " + (n + 2)
                ));
                newProof.add(new ProofNode(
                        hyp + "->(" + hyp + "->" + hyp + ")->" + hyp,
                        "Ax"
                ));
                newProof.add(new ProofNode(
                        hyp + "->" + hyp,
                        "MP " + (n + 4) + " " + (n + 3)
                ));
                mapping[i] = n + 5;
            } else if (tmp.reason.charAt(0) == 'M') {
                String reason = tmp.reason;
                int j = Integer.parseInt(reason.split("\\s+")[1]) - 1;
                int k = Integer.parseInt(reason.split("\\s+")[2]) - 1;
                String dj = curProof.get(j).rawExpression;
                newProof.add(new ProofNode(
                        "(" + hyp + "->" + dj + ")->" +
                                "(" + hyp + "->" + dj + "->" + expression + ")->" +
                                "(" + hyp + "->" + expression + ")",
                        "Ax"
                ));
                newProof.add(new ProofNode(
                        "(" + hyp + "->" + dj + "->" + expression + ")->" +
                                "(" + hyp + "->" + expression + ")",
                        "MP " + (mapping[j]) + " " + (n + 1)
                ));
                newProof.add(new ProofNode(
                        hyp + "->" + expression,
                        "MP " + (mapping[k]) + " " + (n + 2)
                ));
                mapping[i] = n + 3;
            } else {
                newProof.add(new ProofNode(
                        expression + "->" + hyp + "->" + expression, "Ax"
                ));
                newProof.add(new ProofNode(
                        expression, "Hyp/Ax"
                ));
                newProof.add(new ProofNode(
                        hyp +  "->" + expression, "MP " + (n + 2) + " " + (n + 1)
                ));
                mapping[i] = n + 3;
            }
        }
        for (ProofNode proofNode : newProof) {
            proofNode.rawExpression = normalize(proofNode.rawExpression);
        }
        return newProof;
    }

    private static List<ProofNode> addHyp(String hyp, String rest,
                                          List<ProofNode> curProof) {
        int n = curProof.size();
        List<ProofNode> res = new ArrayList<>(curProof);
        res.add(new ProofNode(hyp, "Hyp"));
        res.add(new ProofNode(rest, "MP " + (n + 1) + " " + (n)));
        return res;
    }

    private static String normalize(String raw_expression) {
        return normalizeExpr(parseExpression(raw_expression));
    }
}