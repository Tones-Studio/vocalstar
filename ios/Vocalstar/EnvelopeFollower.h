//
//  EnvelopeFollower.h
//  Vocalstar
//
//  Created by Mathias Dietrich on 15.07.23.
//

#ifndef EnvelopeFollower_h
#define EnvelopeFollower_h

class EnvelopeFollower{
    
public:
    
    EnvelopeFollower()
     {
         m_env = 0;
     }
    
    void configure (double sampleRate, double attackMs, double releaseMs){
        sr = sampleRate;
      m_a = pow (0.01, 1.0 / (attackMs * sampleRate * 0.001));
      m_r = pow (0.01, 1.0 / (releaseMs * sampleRate * 0.001));
    }

    float getValue(){
        return m_env;
    }
    
    float process (float v){
        v = std::abs(v);
        if (v > e){
            e = m_a * (e - v) + v;
        }
        else{
            e = m_r * (e - v) + v;
        }
        m_env = e;
        return m_env;
    }
    
    double e = 0;
    double m_env;
    float sr = 44100;
    double m_a;
    double m_r;
};

enum GateState{
    OPEN,
    CLOSING,
    OPENING,
    CLOSED
};

class NoiseGate{
    
public:
    
    GateState state = CLOSED;
    float gain = 1.0;

    void configure(float sampleRate){
        sr = sampleRate;
        envelopeFollower.configure(sampleRate, 5, 400);
    }
    
    float process(float v){
        float env =  envelopeFollower.process(v);
        if(env < 0.006){
            state = CLOSING;
        }
        if(env > 0.008){
            state = OPENING;
        }
        
        if(state == CLOSING){
            gain = gain * 0.999;
            if(gain < 0.001){
                state = CLOSED;
            }
        }
        
        if(state == OPENING){
            gain = gain * 1.09;
            if(gain > 1.0){
                gain = 1.0;
                state = OPEN;
            }
        }
        
        if(state == OPEN){
            return v;
        }
        if(state == CLOSED){
            return 0;
        }
        return v * gain;
    }
    
private:
    EnvelopeFollower envelopeFollower;
    float sr = 44100;
};

#endif /* EnvelopeFollower_h */



