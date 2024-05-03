cmake_minimum_required(VERSION 3.22.2)
project("vocalstar")

include_directories(include)

set (APP_SOURCES
    native-lib.cpp
    HelloOboeEngine.cpp
    LatencyTuningCallback.cpp
)

add_library(vocalstar SHARED native-lib.cpp)
find_library(log-lib log)
find_package (oboe REQUIRED CONFIG)
target_link_libraries(vocalstar ${log-lib} oboe::oboe)

