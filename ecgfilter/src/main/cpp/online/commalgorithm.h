//
// Created by zhenghuimin on 2019/4/29.
//

#ifndef DENOISE_COMMALGORITHM_H
#define DENOISE_COMMALGORITHM_H

#include <vector>
#include <algorithm>
#include <string>
#include <deque>


using namespace std;

int DoubleCompares(double doubleNum1, double doubleNum2);


double DoubleTripToZero(double doubleNum);

double StringToDouble(string str);


void DoubleToString(double doubleNum, string &str);


int DoubleIsTooNear(double doubleNum1, double doubleNum2);


class DataStreamStatistic {
public:
    void ResetMe();

    double DssMeanAdd91PercentStd(double data);

    double DssMeanDec25PercentStd(double data);

    double DssDataDivideMean(double data);

    double DssMean(double data);

    double DssStd(double data);

private:
    double mean;
    double squareMean;
    double sampleCnt;
public:
    DataStreamStatistic();
};


#endif
