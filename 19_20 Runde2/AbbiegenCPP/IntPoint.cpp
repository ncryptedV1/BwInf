#include "IntPoint.h"
#include <string>

IntPoint::IntPoint(int x, int y) : x(x), y(y) {}

int IntPoint::getX() const {
    return x;
}

int IntPoint::getY() const {
    return y;
}

IntPoint::operator std::string() const {
    return "(" + std::to_string(x) + "," + std::to_string(y) + ")";
}

double IntPoint::getAngleTo(const IntPoint &point2) const {
    int x2 = point2.x;
    int y2 = point2.y;
    return abs((y2 - y) / (double) (x2 - x));
}