cmake_minimum_required(VERSION 3.22.2) #replace 3.x.x with your CMake Version
project("vocalstar")

add_library(vocalstar SHARED native-lib.cpp)
find_library(log-lib log)
find_package (oboe REQUIRED CONFIG)
target_link_libraries(vocalstar ${log-lib} oboe::oboe)

