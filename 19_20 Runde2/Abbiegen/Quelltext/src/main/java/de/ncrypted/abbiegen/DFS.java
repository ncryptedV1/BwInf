package de.ncrypted.abbiegen;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author ncrypted
 */
public class DFS {

    private static double[][] angles;
    private static Stack<Integer> path;
    private static boolean[] inStack;

    public static void setup(List<List<Pair<Integer, Double>>> graph) {
        int n = graph.size();
        angles = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                angles[i][j] = 90;
            }
        }
        path = new Stack<>();
        inStack = new boolean[n];
    }

    public static void dfs(List<List<Pair<Integer, Double>>> graph, int curNode, int targetNode, int turns, double wayLen, double lastAngle) {
        path.push(curNode);
        inStack[curNode] = true;
        if (curNode == targetNode) {
            Abbiegen.minTurns = turns;
            Abbiegen.minTurnDist = wayLen;
            Abbiegen.turnPath = new ArrayList<>(path);
            path.pop();
            inStack[curNode] = false;
            return;
        }
        for (Pair<Integer, Double> edge : graph.get(curNode)) {
            int nextNode = edge.getKey();
            double edgeLen = edge.getValue();
            if (inStack[nextNode]) {
                continue;
            }
            double newAngle = angles[curNode][nextNode];
            if (newAngle == 90) {
                angles[curNode][nextNode] = Abbiegen.nodes.get(curNode).getAngleTo(Abbiegen.nodes.get(nextNode));
                newAngle = angles[curNode][nextNode];
            }
            int newTurns = (newAngle == lastAngle || lastAngle == -1) ? turns : turns + 1;
            double newWayLen = wayLen + edgeLen;
            if (newTurns > Abbiegen.minTurns || newWayLen > Abbiegen.maxPathLen ||
                    (newTurns == Abbiegen.minTurns && newWayLen >= Abbiegen.minTurnDist)) {
                continue;
            }
            dfs(graph, nextNode, targetNode, newTurns, wayLen + edgeLen, newAngle);
        }
        path.pop();
        inStack[curNode] = false;
    }
}
