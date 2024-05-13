//
// Created by Mathias Dietrich on 13.05.24.
//

#include "DSP.h"

void DSP::setup(double sampleRate, int blockSize, bool isMono){
    isActive = false;
    sr = sampleRate;
    expectedBlocksize = blockSize;
   _isMono = isMono;
    limiterl.configure(sampleRate);
    limiterr.configure(sampleRate);
    noiseGatel.configure(sampleRate);
    noiseGater.configure(sampleRate);
    isActive = true;
}

void  DSP::stop(){
    isActive = false;
}

void DSP::render(const float * bufferIn, float * bufferOut, int blocksize){
    if(!isActive){
        return;
    }
    for (int i=0; i<blocksize; i = i + 2) {

        // Get
        float l = bufferIn[i];
        float r = bufferIn[i + 1];

        // merge if mono
        if(_isMono){
            l = (l + r) * 0.5;
            r = l;
        }

        // boost above Gate
        l = l * 1.3;
        r = r * 1.3;

        // Gate
        l = noiseGatel.process(l);
        r = noiseGater.process(r);

        // Push Limiter
        l = l * 1.5;
        r = r * 1.5;

        // Limiter
        l = limiterl.sample(l);
        r = limiterr.sample(r);

        // Vol
        l = l * micLevel;
        r = r * micLevel;

        if(isMuted){
            l = 0;
            r = 0;
        }

        // send back
        bufferOut[i] = l;
        bufferOut[i+1] = r;
    }
}

