//
//  Limiter.h
//  VocalstarFramework
//
//  Created by Mathias Dietrich on 15.07.23.
//


// https://signalsmith-audio.co.uk/writing/2022/limiter/#intro


#ifndef Limiter_h
#define Limiter_h

#include "dsp_lib/delay.h"
#include "dsp_lib/curves.h"
#include "dsp_lib/rates.h"
#include "dsp_lib/envelopes.h"
#include "dsp_lib/filters.h"
#include "dsp_lib/perf.h"
#include "dsp_lib/spectral.h"
#include "dsp_lib/common.h"
#include "dsp_lib/windows.h"

struct ConstantTimeRelease {
    signalsmith::envelopes::PeakHold<double> peakHold{0};
    double gradientFactor = 1;
    double output = 1;
    
    ConstantTimeRelease(int releaseSamples) {
        releaseSamples = std::max(releaseSamples, 1);
        // This will finish its release 0.01 samples too early
        // but that avoids numerical errors
        gradientFactor = 1.0/(releaseSamples - 0.01);
        peakHold.resize(releaseSamples);
        peakHold.reset(1); // start with gain 1
    }
    
    double step(double input) {
        // We need the peak from one sample back
        double prevMin = -peakHold.read();
        peakHold(-input);
        // Gradient is proportional to the difference
        output += (input - prevMin)*gradientFactor;
        output = std::min(output, input);
        return output;
    }
};

class ExponentialRelease {
    
public:
    double releaseSlew;
    double output = 1;
    
    ExponentialRelease(){
        releaseSlew = 1/(100 + 1);
    }
    
    ExponentialRelease(double releaseSamples) {
        // The exact value is `1 - exp(-1/releaseSamples)`
        // but this is a decent approximation
        releaseSlew = 1/(releaseSamples + 1);
    }
    
    double step(double input) {
        // Move towards input
        output += (input - output)*releaseSlew;
        output = std::min(output, input);
        return output;
    }
};

class LimiterAttackHoldRelease {
    
public:
    
    LimiterAttackHoldRelease(){
        
    }
    
    double limit = 0.25;
    double attackMs = 5;
    double holdMs = 15;
    double releaseMs = 40;
    
    signalsmith::envelopes::PeakHold<double> peakHold{0};
    signalsmith::envelopes::BoxStackFilter<double> smoother{0};
    // We don't need fractional delays, so this could be nearest-sample
    signalsmith::delay::Delay<double> delay;
    ExponentialRelease release; // see the previous example code
    
    int attackSamples = 0;
    void configure(double sampleRate) {
        attackSamples = attackMs*0.001*sampleRate;
        int holdSamples = holdMs*0.001*sampleRate;
        double releaseSamples = releaseMs*0.001*sampleRate;
        release = ExponentialRelease(releaseSamples);

        peakHold.resize(attackSamples + holdSamples);
        smoother.resize(attackSamples, 3);
        smoother.reset(1);
        
        delay.resize(attackSamples + 1);
    }
    int latencySamples() {
        return attackSamples;
    }
    
    double gain(double v) {
        double maxGain = 1;
        if (std::abs(v) > limit) {
            maxGain = limit/std::abs(v);
        }
        double movingMin = -peakHold(-maxGain);
        double releaseEnvelope = release.step(movingMin);
        return smoother(releaseEnvelope);
    }

    // use this one
    double sample(double v) {
        double g = gain(v);
        double delayedV = delay.write(v).read(attackSamples);
        return delayedV*g;
    }
};

#endif /* Limiter_h */
