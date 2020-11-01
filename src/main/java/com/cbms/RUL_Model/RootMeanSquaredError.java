package com.cbms.RUL_Model;

public class RootMeanSquaredError {
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
