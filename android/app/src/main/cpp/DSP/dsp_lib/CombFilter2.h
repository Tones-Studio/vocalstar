//
// Created by Mathias Dietrich on 14.05.24.
//

#ifndef VOCALSTAR_COMBFILTER2_H
#define VOCALSTAR_COMBFILTER2_H
#include <stdio.h>

class CombFilter2 {

public:
    CombFilter2(float fFIRCoeff, float fIIRCoeff, int iDelayInSamples); // Constructor
    ~CombFilter2(); // Destructor
    float *combFilterBlock(float *input, int blockSize);
    void clearDelayLines();
    int getDelayInSamples() const;
    float getFIRCoeff() const;
    float getIIRCoeff() const;

private:
    float fFIRCoeff;
    float fIIRCoeff;
    int iDelayInSamples;
    float *fFIRDelay;
    float *fIIRDelay;
};

#endif //VOCALSTAR_COMBFILTER2_H
