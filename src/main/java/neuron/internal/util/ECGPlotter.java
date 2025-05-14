package neuron.internal.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ECGPlotter {

    private String csvFilePath;

    public ECGPlotter(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    private XYSeries readECGDataWithFixedSampling() {
        XYSeries series = new XYSeries("Filtered ECG (250Hz)");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;
            int sampleIndex = 0;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // skip header
                    continue;
                }

                String[] values = line.split(",");

                // Ambil nilai ECG Filtered
                double ecgFiltered = Double.parseDouble(values[1]);

                // Hitung waktu berdasarkan sample index (4 ms per sample karena 250Hz)
                double timeMs = sampleIndex * (1000.0 / 250.0);

                series.add(timeMs, ecgFiltered);

                sampleIndex++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return series;
    }


    private JFreeChart createECGChart(XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        return ChartFactory.createXYLineChart(
                "Filtered ECG Signal",
                "Time (ms)",
                "ECG1_Filtered",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    public void saveChartAsPNG(String outputFilePath) {
        XYSeries series = readECGDataWithFixedSampling();
        JFreeChart chart = createECGChart(series);

        try {
            File outputFile = new File(outputFilePath);
            ChartUtils.saveChartAsPNG(outputFile, chart, 1000, 600);
            System.out.println("Chart saved to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

