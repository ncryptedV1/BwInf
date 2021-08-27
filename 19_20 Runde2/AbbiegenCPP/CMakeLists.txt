cmake_minimum_required(VERSION 3.19)
project(AbbiegenCPP)

set(CMAKE_CXX_STANDARD 17)

add_executable(AbbiegenCPP abbiegen.cpp dfs.cpp dfs.h dijkstra.cpp dijkstra.h IntPoint.cpp IntPoint.h)