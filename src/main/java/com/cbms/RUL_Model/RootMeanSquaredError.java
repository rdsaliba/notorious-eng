package com.cbms.RUL_Model;

public class RootMeanSquaredError {
    /**
     This function calculates the Root mean Squared error for an Artificial Inteligence model.
     The parameters of the model are the predicted ruls and the real ruls.
     The functions returns the root mean squared error of the mdoel.
     @author Talal
     */
    public static double calculate(double[] predictedRul, double[] realRul){
        double[] error=new double[predictedRul.length];
        double mse;
        double rmse;
        for(int i=0;i<predictedRul.length;i++){
            double difference= predictedRul[i]-realRul[i];
            error[i]= Math.pow(difference, 2);
        }
        double total = 0;
        for(int i=0; i<error.length; i++){
            total = total + error[i];
        }
        mse=total/error.length;
        rmse=Math.sqrt(mse);
        return rmse;
    }
}
