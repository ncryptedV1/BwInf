package de.ncrypted.abbiegen;

import de.ncrypted.abbiegen.geometry.PointInt;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author ncrypted
 */
public class Dijkstra {

    public static Pair<Double, List<Integer>> dijkstra(List<PointInt> nodes, List<List<Pair<Integer, Double>>> graph, int start, int end) {
        List<Double> dist = new ArrayList<>(nodes.size());
        List<Integer> pred = new ArrayList<>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            dist.add(Integer.MAX_VALUE + 0.0);
            pred.add(-1);
        }
        dist.set(start, 0.0D);
        pred.set(start, -1);

        PriorityQueue<Pair<Double, Integer>> pq = new PriorityQueue<>((p1, p2) -> p1.getKey() > p2.getKey() ? 1 : -1);
        pq.add(new Pair<>(0.0D, start));
        while (!pq.isEmpty()) {
            int u = pq.peek().getValue();
            double uDist = pq.poll().getKey();
            if (dist.get(u) != uDist) {
                continue;
            }
            for (Pair<Integer, Double> edge : graph.get(u)) {
                int v = edge.getKey();
                double w = edge.getValue();
                if (uDist + w < dist.get(v)) {
                    dist.set(v, uDist + w);
                    pred.set(v, u);
                    pq.add(new Pair<>(uDist + w, v));
                }
            }
        }
        List<Integer> path = constructPath(pred, start, end);
        return new Pair<>(dist.get(end), path);
    }

    private static List<Integer> constructPath(List<Integer> pred, int start, int end) {
        List<Integer> path = new ArrayList<>();
        int cur = end;
        while (cur != start) {
            path.add(cur);
            cur = pred.get(cur);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }
}
