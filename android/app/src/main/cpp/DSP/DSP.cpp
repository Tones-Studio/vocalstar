//
// Created by Mathias Dietrich on 13.05.24.
//

#include "dsp_lib/DSP.h"

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
    delayLineLong.resize(2 * 48000,0);

    filterL.setButterworth(type_highshelf,  7000.0,  0.1,  1.2);
    filterR.setButterworth(type_highshelf,  7000.0,  0.1,  1.2);
    filterL.prepareToPlay(sampleRate,blockSize);
    filterR.prepareToPlay(sampleRate,blockSize);

    reverb.configure(sampleRate);
    comp2L.clearDelayLines();
    comp2R.clearDelayLines();
}

void  DSP::stop(){
    isActive = false;
}

void DSP::render(const float * bufferIn, float * bufferOut, int blocksize){
    if(!isActive){
        return;
    }
    int blocksPerChannel = blocksize / 2;
    float ML[blocksize];
    float MR[blocksize];

    float ReverbL[blocksize];
    float ReverbR[blocksize];

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
        l += 0.1 * delayLineL.read(7907 * 2);
        r += 0.1 * delayLineR.read(7919);

        float refCenter = delayLineM.read(6000);
        float lOrg = l;
        float rOrg = r;

        l+= 0.05 * delayLineM.read(24050);
        r+= 0.05 * delayLineM.read(24000);

       // Echo
       float longDelay =  delayLineLong.read(80000);
        l+= 0.01 * longDelay;
        r+= 0.01 * longDelay;

        // send to delays
        delayLineL.write(lOrg);
        delayLineR.write(rOrg);
        delayLineM.write((lOrg+ rOrg) * 0.5);
        float feedback = 0.1;
        delayLineLong.write((lOrg + rOrg) * 0.5 + longDelay * feedback);

        // send to reverb
        std::array<float, 2> array;
        array[0,0] =  l;
        array[0,1] =  r;
        auto res = reverb.process(array);

        // AllPath Filter
        float c = 0.1;
        float inputL =  res[0,0];
        float inputR =  res[0,1];
        res[0,0] = stateL + c * inputL;
        res[0,1] = stateR + c * inputR;
        stateL = inputL - c * res[0,0];
        stateR = inputR - c * res[0,1];

        // add reverb return
        ReverbL[index] = res[0,0] * 0.03 + res[0,1] * 0.01; // not full stereo
        ReverbR[index] = res[0,1] * 0.03 +  res[0,0] * 0.01;

        // back into buffer
        ML[index] = l;
        MR[index] = r;
        ++index;
    }

    // Comp Filter the reverb
    auto combL = comp2L.combFilterBlock(ReverbL, blocksPerChannel);
    auto combR = comp2R.combFilterBlock(ReverbR, blocksPerChannel);

    // Add reverb and send back to caller
    index = 0;
    for(int i =0; i < blocksize; i = i + 2){
        bufferOut[i] = ML[index] + 0.3 * combL[index] +combR[index] * 0.1 ;
        bufferOut[i + 1] = MR[index] + 0.3 * combR[index] + combL[index] * 0.1;
        ++index;
    }
}



