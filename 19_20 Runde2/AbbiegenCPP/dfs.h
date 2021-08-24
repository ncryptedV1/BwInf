#ifndef ABBIEGENCPP_DFS_H
#define ABBIEGENCPP_DFS_H

#include <vector>
#include <stack>
#include <limits>
#include "IntPoint.h"

using std::vector;
using std::stack;
using std::tuple;
using std::pair;

vector<vector<double>> angles;
stack<int> path;
vector<bool> inStack;
int minTurns;
double minTurnDist;
stack<int> turnPath;

const static int MAX_INT = std::numeric_limits<int>::max();

void setup(vector<vector<tuple<int, double>>> &graph);

void
dfs(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int maxPathLen, int curNode, int targetNode, int turns, double wayLen, double lastAngle);

#endif // ABBIEGENCPP_DFS_H