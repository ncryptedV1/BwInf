#ifndef ABBIEGENCPP_DIJKSTRA_H
#define ABBIEGENCPP_DIJKSTRA_H

#include <vector>
#include <limits>
#include "IntPoint.h"

using std::pair;
using std::vector;

const static int MAX_INT = std::numeric_limits<int>::max();

pair<double, vector<int>>
dijkstra(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int start, int end);

vector<int> constructPath(vector<int> &pred, int start, int end);

#endif // ABBIEGENCPP_DIJKSTRA_H