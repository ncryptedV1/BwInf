#ifndef ABBIEGENCPP_DFS_H
#define ABBIEGENCPP_DFS_H

#include <vector>
#include <stack>
#include <tuple>
#include "IntPoint.h"

using std::vector;
using std::stack;
using std::tuple;
using std::pair;

typedef tuple<int, double, stack<int>> dfsResultTuple;

class DFS {
private:
    // base information
    vector<IntPoint> nodes;
    vector<vector<pair<int, double>>> graph;
    int maxPathLen;
    int startNode;
    int targetNode;

    // implementation variables
    int curNode;
    vector<vector<double>> angles;
    stack<int> path;
    vector<bool> inStack;

    // current result holder variables
    int minTurns;
    double minTurnDist;
    stack<int> turnPath;

    void dfsImpl(int curNode, int turns, double wayLen, double lastAngle);

public:
    DFS(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int maxPathLen, int startNode,
        int targetNode);

    dfsResultTuple dfs();
};

#endif // ABBIEGENCPP_DFS_H