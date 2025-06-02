package neuron.internal.util;

import neuron.internal.pojo.ECGSample;
import neuron.internal.pojo.RPeakSample;
import neuron.internal.pojo.RRInterval;
import neuron.internal.pojo.Stats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Util {

    public static List<ECGSample> computeMeanGroupedByTime(List<ECGSample> rawSamples) {
        List<ECGSample> validSamples = rawSamples.stream()
                .filter(sample -> sample.getTimestamp() != null)
                .collect(Collectors.toList());

        Map<Instant, List<Double>> grouped = new HashMap<>();

        for (ECGSample sample : validSamples) {
            grouped.computeIfAbsent(sample.getTimestamp(), k -> new ArrayList<>())
                    .add(sample.getValue());
        }

        List<ECGSample> result = new ArrayList<>();
        for (Map.Entry<Instant, List<Double>> entry : grouped.entrySet()) {
            Instant timestamp = entry.getKey();
            List<Double> values = entry.getValue();
            double mean = values.stream().mapToDouble(d -> d).average().orElse(0.0);
            result.add(new ECGSample(timestamp, mean));
        }

        result.sort(Comparator.comparing(ECGSample::getTimestamp));

        return result;
    }

    public static List<ECGSample> computeMedianGroupedByTime(List<ECGSample> rawSamples) {
        List<ECGSample> validSamples = rawSamples.stream()
                .filter(sample -> sample.getTimestamp() != null)
                .collect(Collectors.toList());

        Map<Instant, List<Double>> grouped = new HashMap<>();

        for (ECGSample sample : validSamples) {
            grouped.computeIfAbsent(sample.getTimestamp(), k -> new ArrayList<>())
                    .add(sample.getValue());
        }

        List<ECGSample> result = new ArrayList<>();
        for (Map.Entry<Instant, List<Double>> entry : grouped.entrySet()) {
            Instant timestamp = entry.getKey();
            List<Double> values = entry.getValue();
            double median = computeMedian(values);
            result.add(new ECGSample(timestamp, median));
        }

        result.sort(Comparator.comparing(ECGSample::getTimestamp));

        return result;
    }

    public static double calculateSamplingRateFromTimes(List<String> isoTimestamps) {
        if (isoTimestamps.size() < 2) {
            throw new IllegalArgumentException("The list must have at least two timestamps.");
        }

        Instant t1 = Instant.parse(isoTimestamps.get(0));
        Instant t2 = Instant.parse(isoTimestamps.get(1));

        long diffMillis = Math.abs(t2.toEpochMilli() - t1.toEpochMilli());
        double diffSeconds = diffMillis / 1000.0;

        if (diffSeconds == 0.0) {
            throw new ArithmeticException("Timestamp identical — sampling rate cannot be calculated.");
        }

        return 1.0 / diffSeconds;
    }

    public static List<RPeakSample> markRPeaks(List<ECGSample> samples) {
        List<RPeakSample> result = new ArrayList<>();
        double threshold = computeThreshold(samples.stream()
                .map(ECGSample::getValue)
                .collect(Collectors.toList()), 0.6);

        for (int i = 1; i < samples.size() - 1; i++) {
            ECGSample prev = samples.get(i - 1);
            ECGSample curr = samples.get(i);
            ECGSample next = samples.get(i + 1);

            boolean isRPeak = curr.getValue() > threshold &&
                    curr.getValue() > prev.getValue() &&
                    curr.getValue() > next.getValue();

            result.add(new RPeakSample(curr.getTimestamp(), curr.getValue(), isRPeak));
        }

        result.add(0, new RPeakSample(samples.get(0).getTimestamp(), samples.get(0).getValue(), false));
        result.add(new RPeakSample(samples.get(samples.size() - 1).getTimestamp(),
                samples.get(samples.size() - 1).getValue(), false));

        return result;
    }

    public static void writeRPeakSamplesToCSV(List<RPeakSample> rPeakSamples, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Header
            writer.write("Time,ECG,is_rpeak");
            writer.newLine();

            for (RPeakSample sample : rPeakSamples) {
                String line = String.format("%s,%.6f,%b",
                        sample.getTimestamp().toString(),
                        sample.getValue(),
                        sample.isRPeak());
                writer.write(line);
                writer.newLine();
            }

            System.out.println("RPeakSamples exported to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double computeThreshold(List<Double> filteredSignal, double multiplier) {
        if (filteredSignal == null || filteredSignal.isEmpty()) {
            throw new IllegalArgumentException("Filtered signal list is empty.");
        }

        double maxVal = Collections.max(filteredSignal);
        return maxVal * multiplier;
    }

    public List<RRInterval> calculateRRIntervals(List<RPeakSample> rPeakSamples) {
        List<RRInterval> rrIntervals = new ArrayList<>();

        List<RPeakSample> peaksOnly = rPeakSamples.stream()
                .filter(RPeakSample::isRPeak)
                .sorted(Comparator.comparing(ECGSample::getTimestamp))
                .collect(Collectors.toList());

        for (int i = 1; i < peaksOnly.size(); i++) {
            Instant prev = peaksOnly.get(i - 1).getTimestamp();
            Instant curr = peaksOnly.get(i).getTimestamp();

            long diffMillis = Duration.between(prev, curr).toMillis();
            rrIntervals.add(new RRInterval(curr, diffMillis));
        }

        return rrIntervals;
    }

    public static List<Stats> calculateRRStatisticsInWindow(List<RRInterval> rrIntervals, Duration windowDuration) {
        if (rrIntervals.isEmpty()) return Collections.emptyList();

        List<RRInterval> sortedIntervals = new ArrayList<>(rrIntervals);
        sortedIntervals.sort(Comparator.comparing(RRInterval::getTimestamp));

        List<Stats> statsList = new ArrayList<>();

        Instant windowStart = sortedIntervals.get(0).getTimestamp();
        Instant windowEnd = windowStart.plus(windowDuration);

        List<Double> currentWindowValues = new ArrayList<>();

        for (RRInterval rr : sortedIntervals) {
            Instant ts = rr.getTimestamp();

            while (ts.isAfter(windowEnd)) {
                if (!currentWindowValues.isEmpty()) {
                    statsList.add(computeStats(windowStart, windowEnd, currentWindowValues));
                }

                windowStart = windowEnd;
                windowEnd = windowStart.plus(windowDuration);
                currentWindowValues.clear();
            }

            currentWindowValues.add(rr.getRrIntervalMs());
        }

        if (!currentWindowValues.isEmpty()) {
            statsList.add(computeStats(windowStart, windowEnd, currentWindowValues));
        }

        return statsList;
    }

    private static Stats computeStats(Instant windowStart, Instant windowEnd, List<Double> values) {
        double min = values.stream().min(Double::compare).orElse(0.0);
        double max = values.stream().max(Double::compare).orElse(0.0);
        double mean = values.stream().mapToDouble(d -> d).average().orElse(0.0);
        double median = computeMedian(values);
        double std = computeStd(values, mean);

        return new Stats(windowStart, windowEnd, min, max, mean, median, std);
    }

//    private static Stats calculateStats(List<Double> values) {
//        double min = Collections.min(values);
//        double max = Collections.max(values);
//        double mean = values.stream().mapToDouble(d -> d).average().orElse(0.0);
//        double median = computeMedian(values);
//        double std = computeStd(values, mean);
//
//        return new Stats(min, max, mean, median, std);
//    }

    private static double computeMedian(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        sorted.sort(Double::compareTo);
        int n = sorted.size();
        if (n % 2 == 0) {
            return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
        } else {
            return sorted.get(n / 2);
        }
    }

    private static double computeStd(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }


}
