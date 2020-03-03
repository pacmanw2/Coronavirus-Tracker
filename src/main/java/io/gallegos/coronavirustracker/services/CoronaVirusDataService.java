package io.gallegos.coronavirustracker.services;


import io.gallegos.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService{

    private List<LocationStats> allConfirmedStats = new ArrayList<>();
    private List<LocationStats> allRecoveredStats = new ArrayList<>();
    private List<LocationStats> allDeathStats = new ArrayList<>();

    public List<LocationStats> getAllRecoveredStats(){
        return allRecoveredStats;
    }

    public List<LocationStats> getAllDeathStats(){
        return allDeathStats;
    }

    public List<LocationStats> getAllConfirmedStats(){
        return allConfirmedStats;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 * ? * *")
    public void fetchVirusData() throws IOException, InterruptedException{
        //List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();

        String VIRUS_DATA_URL_CONFIRMED = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
        fetchVirusDataHelper(client, VIRUS_DATA_URL_CONFIRMED, "confirmed");

        String VIRUS_DATA_URL_RECOVERED = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";
        fetchVirusDataHelper(client, VIRUS_DATA_URL_RECOVERED, "recovered");

        String VIRUS_DATA_URL_DEATHS = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";
        fetchVirusDataHelper(client, VIRUS_DATA_URL_DEATHS, "death");
    }

    private void fetchVirusDataHelper(HttpClient client, String virusDataUrl, String statType) throws IOException, InterruptedException{
        List<LocationStats> newStats = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(virusDataUrl)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body());
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));

            // get the latest record column
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDeltaFromPrevDay(latestCases - prevDayCases);
            newStats.add(locationStat);
        }

        String confirmedStat = "confirmed";
        String recoveredStat = "recovered";
        String deathStat = "death";

        if (confirmedStat.equals(statType)) {
            this.allConfirmedStats = newStats;
        } else if (recoveredStat.equals(statType)) {
            this.allRecoveredStats = newStats;
        } else if (deathStat.equals(statType)) {
            this.allDeathStats = newStats;
        }
    }

}
