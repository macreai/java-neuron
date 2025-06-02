package neuron.internal;

import neuron.internal.pojo.*;
import neuron.internal.signal.ButterworthFilter;
import neuron.internal.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ECGProcessor {

    public static List<Stats> processCSV(String csvPath, double lowCut, double highCut, int filterOrder, Duration windowDuration) {
        try {
            // Step 1: Read CSV
            List<ECGSample> rawSamples = readCsv(csvPath);

            // Step 2: Median Grouped by Time
            List<ECGSample> medianSamples = Util.computeMedianGroupedByTime(rawSamples);

            // Step 3: Calculate Sampling Rate
            List<String> timestamps = medianSamples.stream()
                    .map(s -> s.getTimestamp().toString())
                    .collect(Collectors.toList());
            double samplingRate = Util.calculateSamplingRateFromTimes(timestamps);

            // Step 4: Extract signal values for filtering
            List<Double> signalValues = medianSamples.stream()
                    .map(ECGSample::getValue)
                    .collect(Collectors.toList());

            // Step 5: Bandpass Filtering
            ButterworthFilter filter = new ButterworthFilter();
            List<Double> filteredSignal = filter.bandPassFilter(signalValues, samplingRate, lowCut, highCut, filterOrder);

            // Step 6: Reconstruct ECGSamples with filtered values
            List<ECGSample> filteredSamples = new ArrayList<>();
            for (int i = 0; i < filteredSignal.size(); i++) {
                filteredSamples.add(new ECGSample(medianSamples.get(i).getTimestamp(), filteredSignal.get(i)));
            }

            // Step 7: Mark RPeaks
            List<RPeakSample> rPeakSamples = Util.markRPeaks(filteredSamples);
            Util.writeRPeakSamplesToCSV(rPeakSamples, "src/main/resources/rpeaks_output.csv");

            // Step 8: Calculate RRIntervals
            List<RRInterval> rrIntervals = new Util().calculateRRIntervals(rPeakSamples);

            // Step 9: Calculate Stats in Sliding Window
            return Util.calculateRRStatisticsInWindow(rrIntervals, windowDuration);

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<ECGSample> readCsv(String csvPath) throws Exception {
        List<ECGSample> samples = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                Instant timestamp = Instant.parse(parts[0].trim() + "Z");
                double value = Double.parseDouble(parts[1].trim());
                samples.add(new ECGSample(timestamp, value));
            }
        }
        return samples;
    }
}

