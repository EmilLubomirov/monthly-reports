import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Main {

    private static String dataPath;
    private static String reportPath;
    private static final String resultPath = "./src/main/java/report-result.csv";

    public static void main(String[] args) throws IOException {

        readFromConsole();

        try {

            String result = getReportData();

            System.out.println("Writing into file...");
            writeIntoFile(result);
            System.out.println("Done.");

        } catch (ParseException e) {
            System.out.println("CAN'T PARSE FILE DATA TO JSON!");
        }

        catch (IOException e){
            System.out.println("CAN'T FIND THE PATH SPECIFIED!");
        }

        catch (ClassCastException e){
            System.out.println(e.getMessage());
        }
    }

    private static void readFromConsole() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter data file path");
        System.out.println("Example: C:\\Users\\me\\files\\data.json");

        dataPath = br.readLine();

        System.out.println("Enter report definition file path");
        System.out.println("Example: C:\\Users\\me\\files\\report-definition.json");

        reportPath = br.readLine();
    }

    private static String getReportData() throws IOException, ParseException, ClassCastException {

        JSONParser parser = new JSONParser();

        StringBuilder sb = new StringBuilder("Name, Score")
                .append(System.lineSeparator());

        JSONArray data = (JSONArray) parser.parse(new FileReader(dataPath));
        JSONObject object = (JSONObject) parser.parse(new FileReader(reportPath));

        final long periodLimit = (long) object.get("periodLimit");
        final boolean useExperienceMultiplier = (boolean) object.get("useExprienceMultiplier");
        final long percent = (long) object.get("topPerformersThreshold");

        for (Object obj : data) {

            JSONObject dataObj = (JSONObject) obj;
            long objLimit = (long) dataObj.get("salesPeriod");

            if (objLimit > periodLimit){
                continue;
            }

            double score = getScore(dataObj, useExperienceMultiplier);


            // NOT SURE ABOUT FOLLOWING CONDITION
            if (score >= percent) {

                sb.append(dataObj.get("name"))
                        .append(", ")
                        .append(score)
                        .append(System.lineSeparator());
            }
        }

        return sb.toString().trim();
    }

    private static double getScore(JSONObject obj, boolean useExperienceMultiplier) {

        final long totalSales = (long) obj.get("totalSales");
        final long salesPeriod = (long) obj.get("salesPeriod");
        final double experienceMultiplier = (double) obj.get("experienceMultiplier");

        if (useExperienceMultiplier){
            return totalSales / (salesPeriod * 1.0) * experienceMultiplier;
        }

        return totalSales / (salesPeriod * 1.0);
    }

    private static void writeIntoFile(String result) throws IOException {

        FileWriter writer = new FileWriter(resultPath);
        writer.write(result);

        writer.flush();
    }
}
