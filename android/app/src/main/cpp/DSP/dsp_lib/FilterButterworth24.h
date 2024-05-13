//
// Created by Mathias Dietrich on 13.05.24.
//

#ifndef VOCALSTAR_FILTERBUTTERWORTH24_H
#define VOCALSTAR_FILTERBUTTERWORTH24_H

class FilterButterworth24 {
        public:
    FilterButterworth24();
    ~FilterButterworth24();

    void SetSampleRate(float fs);
    void Set(float cutoff, float q);
    float Run(float input);

    private:
    float t0, t1, t2, t3;
    float coef0, coef1, coef2, coef3;
    float history1, history2, history3, history4;
    float gain;
    float min_cutoff, max_cutoff;
};


#endif //VOCALSTAR_FILTERBUTTERWORTH24_H
