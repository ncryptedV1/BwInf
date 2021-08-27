#include "dfs.h"

DFS::DFS(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int maxPathLen, int startNode,
         int targetNode) : nodes(nodes), graph(graph), maxPathLen(maxPathLen), startNode(startNode),
                           targetNode(targetNode) {
    // init implementation variables
    int n = graph.size();
    angles = vector<vector<double>>(n, vector<double>(n));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            angles[i][j] = MAX_INT;
        }
    }
    path = stack<int>();
    inStack = vector<bool>(n);

    // init result holder variables
    minTurns = MAX_INT;
    minTurnDist = MAX_DOUBLE;
    turnPath = stack<int>();
}

dfsResultTuple DFS::dfs() {
    dfsImpl(startNode, 0, 0, MAX_INT);
    return make_tuple(minTurns, minTurnDist, turnPath);
}

void DFS::dfsImpl(int curNode, int turns, double wayLen, double lastAngle) {
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
    for (auto [nextNode, edgeLen] : graph[curNode]) {
//        int nextNode = edge.first;
//        double edgeLen = edge.second;
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
        dfsImpl(nextNode, newTurns, wayLen + edgeLen, newAngle);
    }
    path.pop();
    inStack[curNode] = false;
}