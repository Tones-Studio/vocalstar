//
// Created by Mathias Dietrich on 04.05.24.
//

#ifndef VOCALSTAR_ENGINE_H
#define VOCALSTAR_ENGINE_H

#include "oboe/Oboe.h"
#include "HelloOboeEngine.h"

class Engine {

public:
    int start();
    double getLatency();

private:
   HelloOboeEngine helloEngine;
   bool isRunning = false;
};


#endif //VOCALSTAR_ENGINE_H
