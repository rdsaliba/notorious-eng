/**
 * TODO this class will query the DB and return what we need, not yet implemented
 *
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.source.local;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LocalDataSource {
    public static Instances loadTrainingData(String path) throws Exception {
        {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);
            Instances data   = source.getDataSet();

            if(data.classIndex() == -1)
            {                            //27 - 1 = 26
                data.setClassIndex(data.numAttributes() - 1);       //numAttributes not index 0 (so total attributes - 1 to make last attr the class)
            }

            return data;
        }
    }

    public static File convertToArff(File txtFile,String name) throws IOException
    {
        //Make .arff file for the .txt file passed in
        File arffFile = new File("Dataset/Converted/"+name+".arff");

        //Write attribute headers
        try(PrintWriter write = new PrintWriter(arffFile))
        {
            String attributes = "@relation 'train_FD01'\n" +
                    "\n" +
                    "@attribute Engine_Num numeric\n" +
                    "@attribute Time_Cycle numeric\n" +
                    "@attribute OpSetting_1 numeric\n" +
                    "@attribute OpSetting_2 numeric\n" +
                    "@attribute OpSetting_3 numeric\n" +
                    "@attribute Sensor_1 numeric\n" +
                    "@attribute Sensor_2 numeric\n" +
                    "@attribute Sensor_3 numeric\n" +
                    "@attribute Sensor_4 numeric\n" +
                    "@attribute Sensor_5 numeric\n" +
                    "@attribute Sensor_6 numeric\n" +
                    "@attribute Sensor_7 numeric\n" +
                    "@attribute Sensor_8 numeric\n" +
                    "@attribute Sensor_9 numeric\n" +
                    "@attribute Sensor_10 numeric\n" +
                    "@attribute Sensor_11 numeric\n" +
                    "@attribute Sensor_12 numeric\n" +
                    "@attribute Sensor_13 numeric\n" +
                    "@attribute Sensor_14 numeric\n" +
                    "@attribute Sensor_15 numeric\n" +
                    "@attribute Sensor_16 numeric\n" +
                    "@attribute Sensor_17 numeric\n" +
                    "@attribute Sensor_18 numeric\n" +
                    "@attribute Sensor_19 numeric\n" +
                    "@attribute Sensor_20 numeric\n" +
                    "@attribute Sensor_21 numeric\n" +
                    //                       "@attribute RUL numeric\n" +
                    "\n" +
                    "@data\n";
            write.print(attributes);
        }

        //Read original txt file, modify and write it to the new .arff file
        try(Scanner fileScanner = new Scanner(txtFile);
            FileWriter write = new FileWriter(arffFile, true)
        )
        {
            while(fileScanner.hasNextLine())
            {
                String nextLine = fileScanner.nextLine();
                Scanner lineScanner = new Scanner(nextLine);

                while(lineScanner.hasNext())
                {
                    write.append(lineScanner.next() + ", ");
                }
                write.append("?\n");
                lineScanner.close();
            }
        }

        return arffFile;
    }
}
