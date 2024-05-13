//
// Created by Mathias Dietrich on 13.05.24.
//

#include "DSP.h"

#include "Limiter.h"
#import "EnvelopeFollower.h"

LimiterAttackHoldRelease limiter;
NoiseGate noiseGate;

void DSP::setup(double sampleRate, int blockSize, int activeMic){
    isActive = false;
    sr = sampleRate;
    expectedBlocksize = blockSize;
    activeMicType = activeMic;
    limiter.configure(sampleRate);
    noiseGate.configure(sampleRate);
    isActive = true;
}

void  DSP::stop(){
    isActive = false;
}

void DSP::render(const float * bufferIn, float * bufferOut, int blocksize){
    if(!isActive){
        return;
    }
    for (int i=0; i<blocksize; ++i) {

        // Get
        float v = bufferIn[i];

        // boost above Gate
        //if(activeMicType!=1){ // we do not boost for the internal mic
            v = v * 1.3;
       // }

        // Gate
        v = noiseGate.process(v);

        // Push Limiter
        if(activeMicType!=1){
            v = v * 1.5;
        }

        // Limiter
        v = limiter.sample(v);

        // Vol
        v = v * micLevel;

        // mad boost
        if(activeMicType!=1){
            v = v * 2.5;
        }

        if(isMuted){
            v = 0;
        }

        // send back
        bufferOut[i] = v;
    }
}

