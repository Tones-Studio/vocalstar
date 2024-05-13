//
// Created by Mathias Dietrich on 13.05.24.
//

#ifndef VOCALSTAR_DSP_H
#define VOCALSTAR_DSP_H

class DSP{

public:

    /*
     Setup can be called again  in case sample rate or expected blocksize changes when the user changes device
     */
    void setup(double sampleRate, int blockSize, int activeMic);

    void setMicLevel(double volume){
        micLevel = volume;
    }

    void setGate(double volume){

    }

    void setMute(bool muted){
        isMuted = muted;
    }

    void setActiveMicType(int id){
        activeMicType = id;
    }

    void stop();

    /*
     Add your DSP code here (no heap allocation, no locks, no file io, no socket calls, no Swift or Objective-C calls.
     You have only a few milliseconds time depending on buffersize and sample rate.
     Calculate: seconds = frames / samplerate
     For example 64 frames at 48000 sample rate gives 0.9583 msec to deliver all samples in this callback
     Blocksize can vary on a render call and can be different to expected block size
     */
    void render(const float * bufferIn, float * bufferOut, int blocksize);

private:
    double sr = 0.0;
    int expectedBlocksize = 0;
    double micLevel = 0.0;
    bool isMuted = false;
    bool isActive = false;
    int activeMicType;
};

#endif //VOCALSTAR_DSP_H