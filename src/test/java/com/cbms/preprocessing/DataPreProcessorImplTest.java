package com.cbms.preprocessing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.cbms.AppConstants.SYSTEM_NAME;

public class DataPreProcessorImplTest
{
    Instances originalDataset;
    Instances reducedDataset;
    Instances minimallyReducedDataset;
    ArrayList<Integer> removedIndex;

    DataPreProcessorImpl dataPreProcessorImpl;

    private static DataSource src;

    static
    {
        try
        {
            src = new DataSource("Dataset/Converted/train_FD001.arff");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Instances data;

    static
    {
        try
        {
            data = src.getDataSet();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static DataSource testSrc;

    static
    {
        try
        {
            testSrc = new DataSource("Dataset/Converted/test_FD001.arff");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Instances testData;

    static
    {
        try
        {
            testData = testSrc.getDataSet();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static int classIndex = data.numAttributes() - 1;

    @Before
    public void setUp() throws Exception
    {
        //MockitoAnnotations.initMocks(this);
        dataPreProcessorImpl = new DataPreProcessorImpl(originalDataset);
        data.setClassIndex(classIndex);
        data.renameAttribute(0, SYSTEM_NAME);
    }

    @Test
    public void testAddRUL() throws Exception
    {

        double[] max = dataPreProcessorImpl.getMaxCycles(data, 100);
        Instances result = dataPreProcessorImpl.addRUL(data, max);

        assertEquals("Class values of an instance should be numeric after '?' are replaced with RUL.",
                     (data.lastInstance().classAttribute().equals("?")), result.lastInstance().classAttribute().equals(0));
    }

    @Test
    public void testGetMaxCycles() throws Exception
    {
        double[] result = dataPreProcessorImpl.getMaxCycles(data, 100);
        assertEquals("First max cycle should be 192.", 192, result[0], 0);
    }

    @Test
    public void testProcessFullReduction()
    {
        Instances fullReduction = dataPreProcessorImpl.getReducedDataset();
        assertSame(reducedDataset, fullReduction);
    }

    @Test
    public void testProcessMinimalReduction() throws Exception
    {
        Instances minimalReduction = dataPreProcessorImpl.getMinimallyReducedDataset();
        assertSame(minimallyReducedDataset, minimalReduction);
    }

    @Test
    public void testAddRULCol() throws Exception
    {
        Instances result = dataPreProcessorImpl.addRULCol(data);
        assertEquals("After adding the RUL column to original file, the class attribute should be called RUL."
                ,data.classAttribute().name().equals("Sensor_21"), result.classAttribute().name().equals("RUL"));
    }

    @Test
    public void testRemoveAttributes() throws Exception
    {
        Instances result = dataPreProcessorImpl.removeAttributes(data, testData);
        int trainTotal = data.numAttributes();
        int testTotal = result.numAttributes();
        assertTrue("Testing set should have less attributes than training set after removal.", trainTotal > testTotal);
    }

    @Test
    public void testGetRemovedIndexList() throws Exception
    {
        ArrayList<Integer> removedIndex = new ArrayList<>();
        Remove remove                   = new Remove();
        int[] indicesToDelete = removedIndex.stream().mapToInt(i -> i).toArray();   //convert Integer list to int array
        remove.setAttributeIndicesArray(indicesToDelete);
        remove.setInputFormat(data);

        assertTrue("Data should have less attributes than originalDataset (27) after applying Remove filter."
                   , data.numAttributes() < 27);
    }

    @Test
    public void testGetReducedDataset()
    {
        assertEquals("The reduced datasets should match.", reducedDataset, dataPreProcessorImpl.getReducedDataset());
    }

    @Test
    public void testGetMinimallyReducedDataset()
    {
        assertEquals("The minimally reduced datasets should match.", minimallyReducedDataset, dataPreProcessorImpl.getMinimallyReducedDataset());
    }

    //setContains is private method
//    @Test
//    public void testSetContains()
//    {
//        //assertFalse(dataPreProcessorImpl.setContains(data, data.classAttribute()));
//        assertEquals(true, dataPreProcessorImpl.setContains(data, data.classAttribute()));
//    }
}