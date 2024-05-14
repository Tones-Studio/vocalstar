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

    delayLineL.resize(48000,0);
    delayLineR.resize(48000,0);
    delayLineM.resize(48000,0);

    filterL.setButterworth(type_highshelf,  7000.0,  0.1,  1.2);
    filterR.setButterworth(type_highshelf,  7000.0,  0.1,  1.2);

    filterL.prepareToPlay(sampleRate,blockSize);
    filterR.prepareToPlay(sampleRate,blockSize);
     reverb.configure(sampleRate);
}

void  DSP::stop(){
    isActive = false;
}

void DSP::render(const float * bufferIn, float * bufferOut, int blocksize){
    if(!isActive){
        return;
    }

    float ML[blocksize];
    float MR[blocksize];

    int index = 0;

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

        l = filterL.processButterworth(l);
        r = filterL.processButterworth(r);

        // add Delay
        l += 0.1 * delayLineL.read(12000);
        r += 0.1 * delayLineR.read(8000);

        l+= 0.05 * delayLineM.read(24000);
        r+= 0.05 * delayLineM.read(24000);

        // send to delays
        delayLineL.write(l);
        delayLineR.write(r);
        delayLineM.write((l + r) * 0.5);

        // send to reverb
        std::array<float, 2> array;
        array[0,0] =  l;
        array[0,1] =  r;
        auto res = reverb.process(array);
        l = l + res[0,0] * 0.2;
        r = r + res[0,1] * 0.2;

        // send back
        ML[index] = l;
        MR[index] = r;
        ++index;
    }

    index = 0;
    for(int i =0; i < blocksize; i = i + 2){
        bufferOut[i] = ML[index] ;
        bufferOut[i + 1] = MR[index];
        ++index;
    }

    /*
    for(unsigned i = 0; i < blocksize; ++i)
    {
        float c = 0.1;
        float input =  bufferOut[i];
        bufferOut[i] = state + c * bufferOut[i];
        state = input - c * bufferOut[i];
    }
     */
}



