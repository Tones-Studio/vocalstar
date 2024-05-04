//
// Created by Mathias Dietrich on 04.05.24.
//

#include "include/Engine.h"

using namespace std;

int Engine::start(){
    cout << "starting engine " << endl;
    int res = static_cast<int>(helloEngine.start());
    if(res == 0){
        isRunning = true;
    }
    return res;
}

void Engine::tap(bool isDown){
    isRunning = false;
    helloEngine.tap(isDown);
}

void Engine::stop(){
    isRunning = false;
    helloEngine.stop();
}

double Engine::getLatency() {
    if (isRunning) {
        return helloEngine.getCurrentOutputLatencyMillis();
    }else{
        return -1.0;
    }
}
