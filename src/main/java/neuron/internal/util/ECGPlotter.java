package neuron.internal.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ECGPlotter {
    private final String csvFilePath;

    public ECGPlotter(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public void saveChartAsPNG(String outputPath) {
        try {
            XYSeries ecgSeries = new XYSeries("ECG1_Filtered");
            XYSeries rPeaksSeries = new XYSeries("R Peaks");

            BufferedReader br = Files.newBufferedReader(Paths.get(csvFilePath));
            String header = br.readLine(); // Skip header

            int index = 0;
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 3) continue;

                double ecgValue = Double.parseDouble(values[1]);
                boolean isRPeak = Boolean.parseBoolean(values[2]);

                ecgSeries.add(index, ecgValue);
                if (isRPeak) {
                    rPeaksSeries.add(index, ecgValue);
                }

                index++;
            }

            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(ecgSeries);
            dataset.addSeries(rPeaksSeries);

            JFreeChart chart = ChartFactory.createXYLineChart(
                    "ECG Filtered with R-Peaks",
                    "Sample Index",
                    "ECG1_Filtered",
                    dataset
            );

            XYPlot plot = chart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

            // ECG line - blue line, no shape
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesShapesVisible(0, false);

            renderer.setSeriesPaint(1, Color.RED);
            renderer.setSeriesLinesVisible(1, false);
            renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-2, -2, 4, 4));
            renderer.setSeriesShapesVisible(1, true);

            plot.setRenderer(renderer);

            ChartUtils.saveChartAsPNG(new File(outputPath), chart, 1200, 600);
            System.out.println("Chart saved to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}