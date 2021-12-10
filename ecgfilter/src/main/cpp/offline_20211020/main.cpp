#include <iostream>
#include <cstdio>
#include "swt.h"
#include <deque>
#include <string>
#include <fstream>
#include "commalgorithm.h"
#include "streamswtqua.h"
#include <cmath>

using namespace std;
StreamSwtQua streamSwtQua;

void readfile(string path, deque <double>& data)
{
	ifstream infile(path.c_str());
	string tempstr;
	data.clear();


	while (getline(infile, tempstr))
	{
		data.push_back(StringToDouble(tempstr));
	}

	infile.close();
}

void writefile(string path, deque <double>& data)
{
	ofstream outfile(path.c_str());

	for (int i = 0; i < data.size(); ++i)
	{
		outfile << data[i] << endl;
	}
	outfile.close();
}


int main()
{
	deque <double > inputt;
	deque <double > realInput;		// Panjie: 实际进行分析的数据

	int i = 0;
	int j = 0;
	int flag = 0;
	int cnt = 0;

	deque <double> outputPoints;
	deque <double> allSig;
	deque <double> outputsize;
	int lenthOfData		= 0;
	int ReduntLength	= 0;
	int MultipleSize		= 0;
	int padDataLen		= 0;		// Panjie: 需要补充的数据点数

	readfile("E:/ER2 DB/ER2-008.txt", inputt);		// Panjie：读取数据

	lenthOfData = inputt.size();
	MultipleSize = lenthOfData / 256;
	ReduntLength = lenthOfData - 256 * MultipleSize;

	padDataLen = (MultipleSize + 1) * 256 - lenthOfData;

	writefile("E:/Program/anaResults/ER2_Result/origEcg.txt", inputt);

	for (j = 0; j < lenthOfData; ++j)
	{
		realInput.push_back(inputt[j]);
	}

	// 数据补零
	if (0 != ReduntLength)
	{
		if (padDataLen < 64)
		{
			flag = 1;
				
			for (j = lenthOfData-1; j >= lenthOfData - padDataLen; j--)
			{
				realInput.push_back(inputt[j]);
			}

			for (j = 256 * (MultipleSize+1)-128; j < 256 * (MultipleSize + 1); j++)
			{
				realInput.push_back(realInput[j]);
			}
		}
		else
		{
			for (j = lenthOfData - 1; j >= lenthOfData - padDataLen; j--)
			{
				realInput.push_back(inputt[j]);
			}
		}
	}

	if (0 == ReduntLength)
	{
		for (i = 0; i < 256 * MultipleSize; ++i)
		{
			streamSwtQua.GetEcgData(realInput[i], outputPoints);

			for (j = 0; j < outputPoints.size(); ++j)
			{
				allSig.push_back(outputPoints[j]);
			}
		}

		for (i = 256 * MultipleSize - 128; i < 256 * MultipleSize; ++i)
		{
			streamSwtQua.GetEcgData(inputt[i], outputPoints);
		}

		for (j = 0; j < 64; j++)
		{
			allSig.push_back(outputPoints[j]);
		}
	}
	else
	{
		for (i = 0; i < realInput.size(); i++)
		{
			streamSwtQua.GetEcgData(realInput[i], outputPoints);

			for (j = 0; j < outputPoints.size(); ++j)
			{
				allSig.push_back(outputPoints[j]);
			}
		}

		if (ReduntLength < 192)
		{
			for (i = 0; i < 192 - ReduntLength; i++)
			{
				allSig.pop_back();
			}
		}

		if (1 == flag)
		{
			for (i = 0; i < 64+padDataLen; i++)
			{
				allSig.pop_back();
			}
		}
	}

	writefile("E:/Program/anaResults/ER2_Result/origDwtEcg.txt", allSig);

	//writefile("F:/size.txt", outputsize);
	std::cout << "Hello, World!" << std::endl;
	return 0;
}
