#include "dijkstra.h"
#include <queue>
#include <algorithm>

using std::priority_queue;
using std::make_pair;

typedef pair<double, int> diPair;

pair<double, vector<int>>
Dijkstra::dijkstra(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int start, int end) {
    int n = nodes.size();
    vector<double> dist(n, MAX_INT);
    vector<int> pred(n, -1);
    dist[start] = 0;

    priority_queue<diPair, vector<diPair>, std::greater<diPair>> pq;
    pq.push(make_pair(0, start));
    while (!pq.empty()) {
        int u = pq.top().second;
        double uDist = pq.top().first;
        if (dist[u] != uDist) {
            continue;
        }
        for (pair<int, double> edge : graph[u]) {
            int v = edge.first;
            int w = edge.second;
            if (uDist + w < dist[v]) {
                dist[v] = uDist + w;
                pred[v] = u;
                pq.push(make_pair(uDist + w, v));
            }
        }
    }
    vector<int> path = constructPath(pred, start, end);
    return make_pair(dist[end], path);
}

vector<int> Dijkstra::constructPath(vector<int> &pred, int start, int end) {
    vector<int> path;
    int cur = end;
    while (cur != start) {
        path.push_back(cur);
        cur = pred[cur];
    }
    path.push_back(start);
    std::reverse(path.begin(), path.end());
    return path;
}