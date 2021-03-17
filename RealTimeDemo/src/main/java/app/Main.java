package app;

import app.item.Asset;
import local.AssetDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static final int LIMIT = 10;
    private static int counter;
    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        MeasurementDAO measurementDAO = new MeasurementDAO();
        AssetDAOImpl assetDAOs = new AssetDAOImpl();
        List<Asset> assets = assetDAOs.getAllLiveAssets().subList(0, 5);
        for (Asset a : assets) {
            a.setAssetInfo(measurementDAO.getMeasurementsFromID(a.getId(), LIMIT));
            measurementDAO.deleteMeasurementsFromID(a.getId(), LIMIT);
            logger.info("Measurements delete for asset : {}", a.getId());
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (counter >= LIMIT)
                    timer.cancel();
                logger.info("New Measurements for time max - {} - Start", (counter + 1));
                for (Asset a : assets) {
                    measurementDAO.insertMeasurement(a, counter);
                    assetDAOs.setAssetUpdate(a.getId());
                }
                logger.info("New Measurements for time max - {} - end", (counter + 1) );
                counter++;
            }
        }, 0, 1000);

    }
}
