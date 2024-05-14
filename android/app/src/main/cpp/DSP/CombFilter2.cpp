//
// Created by Mathias Dietrich on 14.05.24.
//

#include "dsp_lib/CombFilter2.h"
#include <iostream>

CombFilter2::CombFilter2(float fFIRCoeff, float fIIRCoeff, int iDelayInSamples) {
    this->fFIRCoeff = fFIRCoeff;
    this->fIIRCoeff = fIIRCoeff;
    this->iDelayInSamples = iDelayInSamples;
    fFIRDelay = new float[iDelayInSamples];
    fIIRDelay = new float[iDelayInSamples];
    clearDelayLines();
}

/* The destructor methods for filterAudio */
CombFilter2::~CombFilter2() {
    // Free all memory
    delete [] fFIRDelay;
    delete [] fIIRDelay;
}

/* A method to perform FIR and IIR comb filtering of an input block using the coefficients defined when constructing the filterAudio object. */
float * CombFilter2::combFilterBlock(float * fInput, int iBlockSize){
    float * fOutput = new float [iBlockSize];
    for(int j = 0; j < iBlockSize; j++){
        fOutput[j] = fInput[j] + fFIRCoeff*fFIRDelay[iDelayInSamples-1] + fIIRCoeff*fIIRDelay[iDelayInSamples-1];
        for(int k = iDelayInSamples-1; k>0; k--){
            fFIRDelay[k] = fFIRDelay[k-1];
            fIIRDelay[k] = fIIRDelay[k-1];
        }
        fFIRDelay[0] = fInput[j];
        fIIRDelay[0] = fOutput[j];
    }
    return fOutput;
}

void CombFilter2::clearDelayLines(){
    for(int k = 0; k < iDelayInSamples; k++) {
        fFIRDelay[k] = 0.0;
        fIIRDelay[k] = 0.0;
    }
}

int CombFilter2::getDelayInSamples() const{
    return iDelayInSamples;
}

float CombFilter2::getFIRCoeff() const{
    return fFIRCoeff;
}

float CombFilter2::getIIRCoeff() const{
    return fIIRCoeff;
}