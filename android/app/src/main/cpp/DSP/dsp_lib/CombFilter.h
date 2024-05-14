//
// Created by Mathias Dietrich on 14.05.24.
//

#ifndef VOCALSTAR_COMBFILTER_H
#define VOCALSTAR_COMBFILTER_H

#include <stdio.h>
#include <cmath>

class CombFilter {
public:
    CombFilter(int decimationFactor, int numberOfSections, int differentialDelay);
    ~CombFilter();
    float filter(float *input, int length);
    void reset();

private:
    int	R, N, M;
    float *buffer_integrator;	// buffer of the integrator part
    float **buffer_comb;		// buffer of the comb part
    int *offset_comb;
};

#endif //VOCALSTAR_COMBFILTER_H
