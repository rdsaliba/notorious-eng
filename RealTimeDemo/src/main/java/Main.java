import app.item.Asset;
import local.AssetDAOImpl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public final static int LIMIT = 10;
    public static int counter = 0;

    public static void main(String[] args) {
        MeasurementDAO measurementDAO = new MeasurementDAO();
        AssetDAOImpl assetDAOs = new AssetDAOImpl();
        List<Asset> assets = assetDAOs.getAllLiveAssets().subList(0, 5);
        for (Asset a : assets) {
            a.setAssetInfo(measurementDAO.getMeasurementsFromID(a.getId(), LIMIT));
            measurementDAO.deleteMeasurementsFromID(a.getId(), LIMIT);
            System.out.println("Measurements delete for asset : " + a.getId());
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (counter >= LIMIT)
                    timer.cancel();
                System.out.println("New Measurements for time max - " + (counter + 1) + " - Start");
                for (Asset a : assets) {
                    measurementDAO.insertMeasurement(a, counter);
                    assetDAOs.setAssetUpdate(a.getId());
                }
                System.out.println("New Measurements for time max - " + (counter + 1) + " - end");
                counter++;
            }
        }, 0, 1000);

    }
}
