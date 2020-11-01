package com.cbms.preprocessing;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.BestFirst;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.IOException;

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

        //showRUL(trainingWithRUL);

        //Save and write the new data for training with RUL:
        ArffSaver save = new ArffSaver();
        save.setInstances(trainingWithRUL);

        //Save as arff
        save.setFile(new File("Dataset/Converted/train_FD001_withRUL.arff"));
        save.writeBatch();

        Instances filteredData;
        filteredData = selectAttributes(trainingWithRUL);
        //filteredData = mergeInstances(trainingWithRUL,filteredData);

        //Save and write the new data for filtered data:
        save = new ArffSaver();
        save.setInstances(filteredData);

        //Save as arff
        save.setFile(new File("Dataset/Converted/train_FD001_filtered.arff"));
        save.writeBatch();

    }

    private static Instances mergeInstances(Instances trainingWithRUL, Instances filteredData) throws Exception {

        System.out.println(trainingWithRUL.classIndex());
        System.out.println(trainingWithRUL.numAttributes());
        String rulIndex = Integer.toString(trainingWithRUL.numAttributes());
        String[] options = {"-R", "1,2,"+rulIndex};

        Remove remove = new Remove();
        remove.setOptions(options);
        remove.setInvertSelection(true);
        remove.setInputFormat(trainingWithRUL);

        Instances newIris = Filter.useFilter(trainingWithRUL, remove);

       return newIris;
    }

    /**
     This method will filter the attributes and remove the ones that do not provide useful information
     To use this method you need to choose an evaluation method and a search method
     for now i choose the default CfsSubsetEval for the evaluator and BestFirst for the search method
     the Weka docs contains other types of evaluators and search methods we can try in the future
     @author Paul
     */
    public static Instances selectAttributes(Instances trainingData){
        AttributeSelection filter = new AttributeSelection();  // a filter needs an evaluator and a search method
        CfsSubsetEval eval = new CfsSubsetEval(); // the evaluator chosen is the CfsSubsetEval
        BestFirst search = new BestFirst(); // the search method used is BestFirst
        filter.setEvaluator(eval); // set the filter evaluator and search method
        filter.setSearch(search);
        Instances newData= trainingData;

        try {
            filter.setInputFormat(trainingData);
            newData = Filter.useFilter(trainingData, filter); // this is what takes the data and applies the filter to reduce it
        } catch (Exception e) {
            e.printStackTrace();
        }

        // print out the attributes that are kept
        System.out.println("After performing CfsSubset evaluator with BestFirst search method (on train_withRUL), " +
                           "the following attributes are kept:");

        for (int i = 0 ; i< newData.numAttributes() ; i++) {
            System.out.println(newData.attribute(i).name());
        }

        return newData;
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

    private static Instances addRUL(Instances trainingData, double[] maxCycles) throws Exception {

      /*  Add filter = new Add();
        filter.setAttributeIndex("last");
        filter.setAttributeName("@RUL");
        filter.setInputFormat(trainingData);
        trainingData = Filter.useFilter(trainingData, filter);*/
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


