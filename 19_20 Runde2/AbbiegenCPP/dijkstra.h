#ifndef ABBIEGENCPP_DIJKSTRA_H
#define ABBIEGENCPP_DIJKSTRA_H

#include <vector>
#include "IntPoint.h"

using std::pair;
using std::vector;

class Dijkstra {
public:
    static pair<double, vector<int>>
    dijkstra(vector<IntPoint> &nodes, vector<vector<pair<int, double>>> &graph, int start, int end);

private:
    static vector<int> constructPath(vector<int> &pred, int start, int end);
};

#endif // ABBIEGENCPP_DIJKSTRA_H