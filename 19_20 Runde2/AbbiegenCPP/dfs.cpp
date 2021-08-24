#include "dfs.h"

void setup(vector<vector<pair<int, double>>> &graph) {
    int n = graph.size();
    angles = vector<vector<double>>(n, vector<double>(n));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            angles[i][j] = MAX_INT;
        }
    }
    path = stack<int>();
    inStack = vector<bool>(n);
}

void
dfs(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int maxPathLen, int curNode, int targetNode,
    int turns,
    double wayLen, double lastAngle) {
    path.push(curNode);
    inStack[curNode] = true;
    if (curNode == targetNode) {
        minTurns = turns;
        minTurnDist = wayLen;
        turnPath = path;
        path.pop();
        inStack[curNode] = false;
        return;
    }
    for (pair<int, double> edge : graph[curNode]) {
        int nextNode = edge.first;
        double edgeLen = edge.second;
        if (inStack[nextNode]) {
            continue;
        }
        double newAngle = angles[curNode][nextNode];
        if (newAngle == MAX_INT) {
            angles[curNode][nextNode] = nodes[curNode].getAngleTo(nodes[nextNode]);
            newAngle = angles[curNode][nextNode];
        }
        int newTurns = (newAngle == lastAngle || lastAngle == -1) ? turns : turns + 1;
        double newWayLen = wayLen + edgeLen;
        if (newTurns > minTurns || newWayLen > maxPathLen ||
            (newTurns == minTurns && newWayLen >= minTurnDist)) {
            continue;
        }
        dfs(nodes, graph, maxPathLen, nextNode, targetNode, newTurns, wayLen + edgeLen, newAngle);
    }
    path.pop();
    inStack[curNode] = false;
}