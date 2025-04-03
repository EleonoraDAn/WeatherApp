import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// Questo è il backend dove i dati dall'API  verranno mostrati nell'App
public class WeatherApp {
    // associamo i dati del meteo con la località che ci viene data
    public static JSONObject getWeatherData(String locationName){
        // prendiamo le coordinate usando l'API della geolocalizzazione
        JSONArray locationData = getLocationData(locationName);

        // estrarre latitudine e longitudine
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // costruire l'url per la richiesta dell'API con le coordinate
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m";

        try{
            // chiama l'api e get risponde
            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to the API.");
                return null;
            }

            // immagazzinare i risultati
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNextLine()){
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            JSONParser parse =  new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parse.parse(String.valueOf(resultJson));
            // ottenere i dati dell'ora
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            // per conoscere i dati dell'ora attuale, abbiamo bisogno di avere l'indice della nostra ora attuale
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // costruire l'oggetto per i dati in json del meteo dei quali avremo accesso nel frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            return weatherData;


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // ricaviamo le coordinate dal nome della posizione
    public static JSONArray getLocationData(String locationName){

    // sostituiamo qualsiasi spazio nel nome della località con "+" per aderire al formato di richiesta dell'API
    locationName = locationName.replaceAll(" ", "+");

    //costruire l'url dell'API con il parametro della località
    String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
            locationName + "&count=10&language=en&format=json";

    try{
        // chiamare l'API e avere una rispsota
        HttpURLConnection conn = fetchApiResponse(urlString);

        // controllare lo stato di risposta. 200 = connessione effettuata
        if(conn.getResponseCode() != 200){
            System.out.println("Error: Could not connect to the API.");
            return null;
        }else{
            // stare the API results
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            // legge e immagazzina i risultati json nella string builder
            while(scanner.hasNextLine()){
                resultJson.append(scanner.nextLine());
            }
            // close scanner
            scanner.close();

            // close url connection
            conn.disconnect();

            // parse the JSON string into a JSON obj (facciamo il parsing per accedere ai dati in modo appropriato)
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // ottenere la lista dei dati della posizione che l'API ha ottenuto dal nome della posizione
            JSONArray locationData = (JSONArray) resultJsonObj.get("results");
            return locationData;

        }
    }catch(Exception e){
        e.printStackTrace();
    }
    return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // tentativo di creare una connessione
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // impostare un metodo di richiesta a "GET" perchè stiamo cercando di sapere i dati relativi alla posizione
            conn.setRequestMethod("GET");

            // connessione all'API
            conn.connect();
            return conn;
        }catch(IOException e){
           e.printStackTrace();
        }

        // non si crea nessuna connessione
        return null;
    }
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // iteriamo attraverso la time list e guarda quale coincide con il tempo corrente
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // return the index
                return i;
            }
        }
        return 0;
    }
    public static String getCurrentTime(){
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // formatta la data: "2025-04-03T00:00"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //formatta e stampa il tempo e la datat corrente
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    // convertire il weather_code in qualcosa di più leggibile
    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if(weatherCode == 0L){
            weatherCondition = "Clear";
        }else if(weatherCode <= 3L && weatherCode > 0L){
            weatherCondition = "Cloudy";
        }else if((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 90L)){
            weatherCondition = "Rain";
        }else if(weatherCode >= 71L && weatherCode <= 77L){
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
