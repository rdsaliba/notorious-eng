package rul_models;

public class RootMeanSquaredError {
    /**
     This function calculates the Root mean Squared error for an Artificial Intelligence model.
     The parameters of the model are the predicted RUL and the real RUL.
     The functions returns the root mean squared error of the model.
     @author Talal
     */
    public static double calculate(double[] predictedRul, Double[] realRul){
        double[] error=new double[predictedRul.length];
        double mse;
        double rmse;
        for(int i=0;i<realRul.length;i++){
            double difference= predictedRul[i]-realRul[i];
            error[i]= Math.pow(difference, 2);
        }
        double total = 0;
        for (double v : error) {
            total = total + v;
        }
        mse=total/error.length;
        rmse=Math.sqrt(mse);
        return rmse;
    }
}
