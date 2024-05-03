cmake_minimum_required(VERSION 3.22.2)
project("vocalstar")

set (OBOE_DIR /Users/mat/gitroot/oboe)
add_subdirectory(${OBOE_DIR} ./oboe-bin)

set (CPLUS_DIR .)
include_directories(${CPLUS_DIR})
include_directories(${CPLUS_DIR}/include)

set (APP_SOURCES
        native-lib.cpp
        HelloOboeEngine.cpp
        LatencyTuningCallback.cpp
        SoundGenerator.cpp
)

add_library(vocalstar SHARED native-lib.cpp)
find_library(log-lib log)
find_package (oboe REQUIRED CONFIG)
target_link_libraries(vocalstar ${log-lib} oboe::oboe)

