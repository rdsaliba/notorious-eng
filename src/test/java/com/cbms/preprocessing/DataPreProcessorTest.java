//package com.cbms.preprocessing;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import weka.core.Instances;
//
//import java.util.Arrays;
//import java.util.Collection;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//import org.junit.runners.Parameterized.Parameters;
//
//@RunWith(Parameterized.class)
//public class DataPreProcessorTest
//{
//    //@Mock
//    Instances reducedDataset;
//    //@Mock
//    Instances minimallyReducedDataset;
//    //@Mock
//    Instances removedIndex;
//    //@InjectMocks
//    static Instances originalDataset;
//    DataPreProcessor dataPreProcessor;
//    DataPreProcessorImpl dataPreProcessorImpl;
//
//    public DataPreProcessorTest(DataPreProcessor dataPreProcessor, DataPreProcessorImpl dataPreProcessorImpl)
//    {
//        this.dataPreProcessor = dataPreProcessor;
//        this.dataPreProcessorImpl = dataPreProcessorImpl;
//    }
//
//    @Before
//    public void setUp() throws Exception
//    {
//        //MockitoAnnotations.initMocks(this);
//        //dataPreProcessor = new DataPreProcessorImpl(originalDataset);
//        //dataPreProcessorImpl = new DataPreProcessorImpl(originalDataset);
//    }
//
//    @Test
//    public void testProcessFullReduction() throws Exception
//    {
////        Instances fullReduction = dataPreProcessor.getReducedDataset();
////        assertSame(reducedDataset, fullReduction);
//        Instances fullReduction = dataPreProcessor.getReducedDataset();
//        assertEquals(reducedDataset, fullReduction);
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> instancesToTest() throws Exception
//    {
//        return (Collection<Object[]>) Arrays.asList(
//                new Object[]{new DataPreProcessorImpl(originalDataset)}
//                                                   );
//    }
//
//}
//