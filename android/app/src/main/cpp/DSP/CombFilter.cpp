//
// Created by Mathias Dietrich on 14.05.24.
//

#include "dsp_lib/CombFilter.h"
#include <assert.h>
#include <memory.h>

CombFilter::CombFilter(int decimationFactor, int numberOfSections, int differentialDelay):
        R(decimationFactor),
        N(numberOfSections),
        M(differentialDelay)
{
    this->buffer_comb = NULL;
    this->buffer_integrator = NULL;
    this->offset_comb = NULL;
    this->buffer_integrator = new float[N];
    this->offset_comb = new int[N];
    this->buffer_comb = new float*[N];
    for (int i = 0; i < N; i++){
        buffer_comb[i] = new float[M];
    }
}

CombFilter::~CombFilter(){
    delete this->buffer_integrator;
    delete this->offset_comb;
    for (int i = 0; i < this->N; i++) {
        delete this->buffer_comb[i];
    }
    delete this->buffer_comb;
}

void CombFilter::reset(){
    for (int i = 0; i < N; i++){
        this->buffer_integrator[i] = 0;
        this->offset_comb[i] = 0;
        for (int j = 0; j < M; j++)
            this->buffer_comb[i][j] = 0;
    }
}

float CombFilter::filter(float *input, int length){
    if (length != this->R) {
        return 0;
    }
    float attenuation = 1.0;
    float tmp_out = 0;

    for (int i = 0; i < R; i++){
        tmp_out = input[i];
        for (int j = 0; j < N; j++)
            tmp_out = this->buffer_integrator[j] = this->buffer_integrator[j] + tmp_out;
    }

    // Comb part
    for (int i = 0; i < N; i++){
        this->offset_comb[i] = (this->offset_comb[i] + 1) % M;
        float tmp = this->buffer_comb[i][this->offset_comb[i]];
        this->buffer_comb[i][this->offset_comb[i]] = tmp_out;
        tmp_out = tmp_out - tmp;
    }
    return attenuation * tmp_out;
}
