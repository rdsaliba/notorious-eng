package com.cbms.Stuff;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PrePro1_ConvertToArff
{
    public static void main(String[] args) throws IOException
    {
        //Feed the original .txt dataset
        File textFile = new File("Dataset/Test/test_FD001.txt");          //original Training_1 data

        File arffFile = convertToArff(textFile);

        System.out.println(((arffFile.exists() && arffFile.canRead())? "Conversion successful. Retrieve from: " +
                arffFile.getPath() : "No conversion took place."));
    }

    private static File convertToArff(File txtFile) throws IOException
    {
        //Make .arff file for the .txt file passed in
        File arffFile = new File("Dataset/Converted/test_FD001.arff");

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
