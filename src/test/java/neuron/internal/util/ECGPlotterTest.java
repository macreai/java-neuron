package neuron.internal.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ECGPlotterTest {

    private String findFirstCSVFile(String folderPath) {
        java.io.File folder = new java.io.File(folderPath);
        java.io.File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files != null && files.length > 0) {
            return files[0].getPath();
        }
        return null;
    }

    @Test
    public void pngECG() {
        String folderPath = "src/main/resources/output_ecg_filtered/";
        String csvFile = findFirstCSVFile(folderPath);
        assertNotNull(csvFile, "CSV not found in " + folderPath);

        String outputImage = "src/main/resources/ecg_filtered.png";
        ECGPlotter plotter = new ECGPlotter(csvFile);
        plotter.saveChartAsPNG(outputImage);
    }
}