//
// Created by Mathias Dietrich on 04.05.24.
//

#ifndef VOCALSTAR_ENGINE_H
#define VOCALSTAR_ENGINE_H

#include "oboe/Oboe.h"

class Engine {

public:
    Engine(){
        isRunning = false;
    }
    int start();
    void stop();
    double getLatency();
    void tap(bool isDown);

private:

   bool isRunning;
};


#endif //VOCALSTAR_ENGINE_H
