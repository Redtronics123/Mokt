cmake_minimum_required(VERSION 3.29)

project(mokt_cpp_bindings)
set(CMAKE_CXX_STANDARD 20)

add_library(mokt_cpp_bindings STATIC mokt.cpp)
