/**
 * Created by Nicholas on 5/5/2017.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SentiWordNet {

    private String pathToSWN = "C:\\Users\\Nicholas\\Desktop\\SentiWordNet_3.0.0\\home\\swn\\www\\admin\\dump\\SentiWordNet_3.0.0.txt";
    private HashMap<String, Double> _dict;

    public SentiWordNet() {

        _dict = new HashMap<String, Double>();
        HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
        try {
            BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
            String line = "";
            while ((line = csv.readLine()) != null) {
                String[] data = line.split("\t");
                Double score = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
                String[] words = data[4].split(" ");
                for (String w : words) {
                    String[] w_n = w.split("#");
                    w_n[0] += "#" + data[0];
                    int index = Integer.parseInt(w_n[1]) - 1;
                    if (_temp.containsKey(w_n[0])) {
                        Vector<Double> v = _temp.get(w_n[0]);
                        if (index > v.size())
                            for (int i = v.size(); i < index; i++)
                                v.add(0.0);
                        v.add(index, score);
                        _temp.put(w_n[0], v);
                    } else {
                        Vector<Double> v = new Vector<Double>();
                        for (int i = 0; i < index; i++)
                            v.add(0.0);
                        v.add(index, score);
                        _temp.put(w_n[0], v);
                    }
                }
            }
            Set<String> temp = _temp.keySet();
            for (Iterator<String> iterator = temp.iterator(); iterator.hasNext(); ) {
                String word = (String) iterator.next();
                Vector<Double> v = _temp.get(word);
                double score = 0.0;
                double sum = 0.0;
                for (int i = 0; i < v.size(); i++)
                    score += ((double) 1 / (double) (i + 1)) * v.get(i);
                for (int i = 1; i <= v.size(); i++)
                    sum += (double) 1 / (double) i;
                score /= sum;
                _dict.put(word, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Double extract(String word) {
        Double total = new Double(0);
        if (_dict.get(word + "#n") != null)
            total = _dict.get(word + "#n") + total;
        if (_dict.get(word + "#a") != null)
            total = _dict.get(word + "#a") + total;
        if (_dict.get(word + "#r") != null)
            total = _dict.get(word + "#r") + total;
        if (_dict.get(word + "#v") != null)
            total = _dict.get(word + "#v") + total;
        return total;
    }

    public String classifyPickUpLine(String line) {
        String[] words = line.split("\\s+");
        double totalScore = 0, averageScore;
        for (String word : words) {
            word = word.replaceAll("([^a-zA-Z\\s])", "");
            if (extract(word) == null)
                continue;
            totalScore += extract(word);
        }
        averageScore = totalScore;

        if (averageScore >= 0.75)
            return "very positive";
        else if (averageScore > 0.25 && averageScore < 0.5)
            return "positive";
        else if (averageScore >= 0.5)
            return "positive";
        else if (averageScore < 0 && averageScore >= -0.25)
            return "negative";
        else if (averageScore < -0.25 && averageScore >= -0.5)
            return "negative";
        else if (averageScore <= -0.75)
            return "very negative";
        return "neutral";
    }

    public static void main(String[] args) throws IOException {
        SentiWordNet _sw = new SentiWordNet();
        String opinion = "";
        try (BufferedReader pickUp = new BufferedReader(new FileReader("C:\\Users\\Nicholas\\Desktop\\Pick-up Lines 2"))) {
            String line = "";
            while((line = pickUp.readLine()) != null) {
                opinion = _sw.classifyPickUpLine(line);
                System.out.println(line);
                System.out.println("Classification:\t" + opinion);
            }
        } catch (Exception e) {
            System.out.println("File not found.");
            System.exit(0);
        }
    }
}