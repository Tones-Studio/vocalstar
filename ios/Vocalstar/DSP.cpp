//
//  DSP.cpp
//  Vocalstar
//
//  Created by Mathias Dietrich on 21.11.23.
//

#include <stdio.h>
#include "DSP.hpp"

#include "Limiter.h"
#import "EnvelopeFollower.h"

LimiterAttackHoldRelease limiter;
NoiseGate noiseGate;

void DSP::setup(double sampleRate, int expectedBlocksize, int activeMic){
    isActive = false;
    sr = sampleRate;
    blocksize = expectedBlocksize;
    activeMicType = activeMic;
    limiter.configure(sampleRate);
    noiseGate.configure(sampleRate);
    isActive = true;
}

void  DSP::stop(){
    isActive = false;
}

void DSP::render(float * buffer, int blocksize){
    if(!isActive){
        return;
    }
    for (int i=0; i<blocksize; ++i) {
        
        // Get
        float v = buffer[i];
        
        // boost above Gate
        if(activeMicType!=1){ // we do not boost for the internal mic
            v = v * 1.3;
        }
        
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
        buffer[i] = v;
    }
}
