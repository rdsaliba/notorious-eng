package com.cbms.preprocessing;


import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PrePro3_Data_Visualization extends PrePro2_Fill_RUL_Col
{
    public static void main(String[] args) throws Exception
    {
        //This needs to be refactored for JavaFX (Weka's data visualization GUI uses SWING instead of JavaFX)
        //https://weka.sourceforge.io/doc.dev/weka/gui/visualize/VisualizePanel.html

        DataSource src = new DataSource("Dataset/Converted/train_FD001_withRUL.arff");
        Instances data = src.getDataSet();

        VisualizePanel panel = new VisualizePanel();

        PlotData2D graph = new PlotData2D(data);
        String relationName = data.relationName();
        graph.setPlotName(relationName);
        panel.setMasterPlot(graph);

        JFrame frame = new JFrame("Title: " + relationName);
        frame.setSize(1080, 720);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }
}
