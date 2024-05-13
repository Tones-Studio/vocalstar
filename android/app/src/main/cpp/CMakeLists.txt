cmake_minimum_required(VERSION 3.22.2)
project("vocalstar")

set (OBOE_DIR ./oboe)
add_subdirectory (${OBOE_DIR} ./oboe)
include_directories (${OBOE_DIR}/include)
include_directories(${OBOE_DIR}/include ${OBOE_DIR}/samples/shared)
include_directories (include)

set (APP_SOURCES
        LatencyTuningCallback.cpp
        SoundGenerator.cpp
        native-lib.cpp
        LiveEffectEngine.cpp
        DSP/DSP.cpp
        DSP/FilterButterworth24.cpp
        DSP/MyFilter.cpp
)

add_library(vocalstar SHARED  ${APP_SOURCES})
find_library(log-lib log)
target_link_libraries(vocalstar ${log-lib} oboe)


