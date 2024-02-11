package utils;

import java.util.ArrayList;
import java.util.List;

public class CNF {
    public List<Pair<CNF, Integer>> params;

    public Integer free;

    public CNF() {
        params = new ArrayList<>();
        free = 0;
    }

    public CNF(List<Pair<CNF, Integer>> params, Integer free) {
        this.params = params;
        this.free = free;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<CNF, Integer> param : params) {
            sb.append(param.getSecond())
                    .append("*w^(")
                    .append(param.getFirst().toString())
                    .append(")+");
        }
        sb.append(free);
        return sb.toString();
    }
}
