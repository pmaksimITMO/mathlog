package utils;

import java.util.Objects;

public class Pair<F, S> {
    private final F first;

    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final Pair<?, ?> tmp = (Pair<?, ?>) other;
        return Objects.equals(first, tmp.getFirst()) && Objects.equals(second, tmp.getSecond());
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}