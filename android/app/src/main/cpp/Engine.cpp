//
// Created by Mathias Dietrich on 04.05.24.
//

#include "include/Engine.h"

using namespace std;

int Engine::start(){
    cout << "starting engine " << endl;
   return 0;
}

void Engine::tap(bool isDown){
    isRunning = false;
}

void Engine::stop(){
    isRunning = false;
}

double Engine::getLatency() {
    if (isRunning) {
       return -1.0;
    }else{
        return -1.0;
    }
}
