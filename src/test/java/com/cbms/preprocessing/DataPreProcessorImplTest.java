package com.cbms.preprocessing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class DataPreProcessorImplTest
{
    @Mock
    Instances originalDataset;
    @Mock
    Instances reducedDataset;
    @Mock
    Instances minimallyReducedDataset;
    @Mock
    ArrayList<Integer> removedIndex;
    @InjectMocks
    DataPreProcessorImpl dataPreProcessorImpl;

    @Before
    public void setUp()
    {
        //MockitoAnnotations.initMocks(this);
        dataPreProcessorImpl = new DataPreProcessorImpl();
    }

    @Test
    public void testAddRUL() throws Exception
    {
        Instances result = DataPreProcessorImpl.addRUL(null, new double[]{0d});
        Assert.assertEquals(null, result);
    }

//    @Test
//    public void testGetMaxCycles() throws Exception
//    {
//        double[] result = DataPreProcessorImpl.getMaxCycles(null, 0);
//        Assert.assertArrayEquals(new double[]{0d}, result);
//    }

    @Test
    public void testProcessFullReduction() throws Exception
    {
        dataPreProcessorImpl.processFullReduction();
    }

    @Test
    public void testProcessMinimalReduction() throws Exception
    {
        dataPreProcessorImpl.processMinimalReduction();
    }

    @Test
    public void testAddRULCol() throws Exception
    {
        Instances result = dataPreProcessorImpl.addRULCol(null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testRemoveAttributes() throws Exception
    {
        Instances result = dataPreProcessorImpl.removeAttributes(null, null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetRemovedIndexList() throws Exception
    {
        Remove result = dataPreProcessorImpl.getRemovedIndexList();
        Assert.assertEquals(null, result);
    }
}