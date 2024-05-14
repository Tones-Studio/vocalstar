//
// Created by Mathias Dietrich on 14.05.24.
//

#ifndef VOCALSTAR_CIC_H
#define VOCALSTAR_CIC_H

#include <stdio.h>
#include <cmath>

class CIC {

public:
    // R:Decimation factor;
    // N:Number of sections;
    // M:Differential delay;
    CIC(int decimationFactor, int numberOfSections, int diferrencialDelay);

    // destructor
    ~CIC();

    // the actual filter function
    // the parameter input shuld be R-dimensional vector(R continuous samples) and the parameter length should be R
    // the output is double, corresponding to the downsampled output
    double filter(float *input, int length);

    // reset the buffer
    void reset();

private:
    int			R, N, M;
    float		*buffer_integrator;	// buffer of the integrator part
    float		**buffer_comb;		// buffer of the comb part
    int			*offset_comb;
};

#endif //VOCALSTAR_CIC_H
