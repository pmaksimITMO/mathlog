package utils;

import java.io.*;
import java.util.StringTokenizer;

public class FastScanner implements AutoCloseable {
    private final BufferedReader br;
    private StringTokenizer st;
    public FastScanner() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    public String next() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    @Override
    public void close() throws Exception {
        br.close();
    }
}
