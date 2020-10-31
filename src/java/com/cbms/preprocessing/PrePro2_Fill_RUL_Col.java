package com.cbms.preprocessing;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;

public class PrePro2_Fill_RUL_Col
{
    public static void main(String[] args) throws Exception
    {
        Instances trainingData = loadTrainingData();
        Instance lastRow = trainingData.lastInstance();

        int totalEngines = (int) lastRow.value(0);
        double[] maxCycles = getMaxCycles(trainingData, totalEngines);

        //showMaxCycles(maxCycles);

        Instances trainingWithRUL = addRUL(trainingData, maxCycles);

        showRUL(trainingWithRUL);

        //Save and write the new data:
        ArffSaver save = new ArffSaver();
        save.setInstances(trainingWithRUL);

        //Save as arff
        save.setFile(new File("Dataset/Converted/train_FD001_withRUL.arff"));
        save.writeBatch();

    }

    //loads .arff training data
    public static Instances loadTrainingData() throws Exception
    {
        String path = ("Dataset/Converted/train_FD001.arff");
        DataSource source = new DataSource(path);
        Instances data   = source.getDataSet();

        if(data.classIndex() == -1)
        {                            //27 - 1 = 26
            data.setClassIndex(data.numAttributes() - 1);       //numAttributes not index 0 (so total attributes - 1 to make last attr the class)
        }

        return data;
    }

    //Returns max cycles of each engine in an Array (size = # of engine in the trainingData that is passed in)
    private static double[] getMaxCycles(Instances trainingData, int totalEngines)
    {
        Attribute engine = trainingData.attribute("Engine_Num");
        Attribute timeCycle = trainingData.attribute("Time_Cycle");

        double engineNum = 1;
        double[] maxCycles = new double[totalEngines];

        for (int i = 0; i < trainingData.numInstances(); i++)
        {
            Instance row = trainingData.instance(i);
            if(row.value(engine) -1 == engineNum)
            {
                Instance prevRow = trainingData.instance(i - 1);
                maxCycles[(int) engineNum - 1] = prevRow.value(timeCycle);
                engineNum++;
            }

            else if(engineNum == 100)
            {
                Instance lastRow = trainingData.lastInstance();
                maxCycles[(int) engineNum - 1] = lastRow.value(timeCycle);
            }
        }
        return maxCycles;
    }

    private static void showMaxCycles(double[] max)
    {
        int engineNum = 1;

        for(double e: max)
        {
            System.out.println("Max cycle of engine " + (engineNum++) + ": " + e);
        }
    }

    private static Instances addRUL(Instances trainingData, double[] maxCycles)
    {
        int engineNum = 1;
        Attribute engine = trainingData.attribute("Engine_Num");
        Instance row;

        int timeCycleIndex = 1;

        for(int i = 0; i < trainingData.numInstances(); i++)
        {
            row = trainingData.instance(i);

            if(row.value(engine) != engineNum)
            {
                engineNum++;
                timeCycleIndex = 1;
            }

            row.setClassValue((maxCycles[engineNum - 1] - timeCycleIndex++));
        }
        return trainingData;
    }

    private static void showRUL(Instances trainingDataWithRUL)
    {
        //System.out.println(trainingDataWithRUL.toSummaryString());        No missing RUL
        Instance row;

        for(int i = 0; i < trainingDataWithRUL.numInstances(); i++)
        {
            row = trainingDataWithRUL.instance(i);
            System.out.println("Engine " + (row.value(0)) + " & Time Cycle " + (row.value(1)) + ": RUL = " + row.classValue());
        }
    }

}


