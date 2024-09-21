cmake_minimum_required(VERSION 3.29)
project(mokt_cpp_bindings)

set(CMAKE_CXX_STANDARD 20)
set(MOKT_SOURCES src/mokt.cpp)
set(MOKT_HEADERS include)

add_library(mokt_cpp_bindings STATIC ${MOKT_SOURCES})
target_include_directories(mokt_cpp_bindings PRIVATE ${MOKT_HEADERS})
