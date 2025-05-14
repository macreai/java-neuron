package neuron.internal.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ECGPlotterTest {

    @Test
    public void pngECG() {
        String csvFile = "src/main/resources/output_ecg_filtered.csv/part-00000-64819ebe-7f96-4bea-b7ef-cb1a324f8fa3-c000.csv";
        String outputImage = "src/main/resources/ecg_filtered.png";
        ECGPlotter plotter = new ECGPlotter(csvFile);
        plotter.saveChartAsPNG(outputImage);
    }
}