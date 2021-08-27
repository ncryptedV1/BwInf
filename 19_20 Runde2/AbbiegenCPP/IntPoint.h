//
// Created by I538984 on 01.07.2021.
//

#ifndef ABBIEGENCPP_INTPOINT_H
#define ABBIEGENCPP_INTPOINT_H

#include <string>
#include <limits>

constexpr static int MAX_INT = std::numeric_limits<int>::max();
constexpr static double MAX_DOUBLE = std::numeric_limits<double>::max();

class IntPoint {
    int x;
    int y;

public:
    IntPoint(int x, int y);

    int getX() const;

    int getY() const;

    operator std::string() const;

    double getAngleTo(const IntPoint &point2) const;
};


#endif //ABBIEGENCPP_INTPOINT_H
