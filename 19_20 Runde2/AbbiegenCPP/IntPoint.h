//
// Created by I538984 on 01.07.2021.
//

#ifndef ABBIEGENCPP_INTPOINT_H
#define ABBIEGENCPP_INTPOINT_H

#include <string>
#include <limits>

const static int MAX_INT = std::numeric_limits<int>::max();
const static double MAX_DOUBLE = std::numeric_limits<double>::max();

class IntPoint {
    int x;
    int y;

public:
    IntPoint(int x, int y);

    int getX();

    int getY();

    operator std::string() const;

    double getAngleTo(IntPoint &point2);
};


#endif //ABBIEGENCPP_INTPOINT_H
