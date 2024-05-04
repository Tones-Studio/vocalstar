//
// Created by Mathias Dietrich on 04.05.24.
//

#include "include/Engine.h"

using namespace std;

int Engine::start(){
    cout << "starting engine " << endl;
    helloEngine.start();
    return 0;
}

double Engine::getLatency() {
    if (isRunning) {
        return helloEngine.getCurrentOutputLatencyMillis();
    }else{
        return -1;
    }
}
