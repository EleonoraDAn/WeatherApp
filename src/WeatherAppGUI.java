import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI() {
            // imposta la gui con un titolo
            super("Weather App");

            // configura la gui per terminare il programma una volta chiuso
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            // imposta la grandezza della gui in pixels
            setSize(450, 650);

            // carica la gui al centro dello schermo
            setLocationRelativeTo(null);

            // crea il layout manager null per pozionare manualmente i componenti senza la gui
            setLayout(null);

            // preventiva qualsiasi resize della gui
            setResizable(false);

            addGuiComponents();
        }
        private void addGuiComponents() {
        // aggiunge la barra di ricerca
        JTextField searchTextField = new JTextField();
        // imposta la posizione e la grandezza del componente
        searchTextField.setBounds(15, 15, 351, 45);

        // cambiare il font e lo stile e la grandezza
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);


        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // testo della temperatura
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        // centrare il testo
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);

        add(temperatureText);

        // weather condition desc
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage =  new JLabel(loadImage("/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // bottone di ricerca
            JButton searchButton = new JButton(loadImage("/assets/search.png"));

            // cambia il cursore quando andiamo sul bottone
            searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            searchButton.setBounds(375, 13, 47, 45);
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //get location from user
                    String userInput = searchTextField.getText();

                    // validare l'input - rimuove lo spazio per non avere un testo vuoto
                    if(userInput.replaceAll("\\s", "").length() <= 0){
                        return;
                    }
                    // retrieve weather data
                    weatherData = WeatherApp.getWeatherData(userInput);
                    // update gui

                    // update weather image
                    String weatherCondition = (String) weatherData.get("weather_condition");
                    switch (weatherCondition){
                        case "Clear":
                            weatherConditionImage.setIcon(loadImage("/assets/clear.png"));
                            break;
                        case "Cloudy":
                            weatherConditionImage.setIcon(loadImage("/assets/cloudy.png"));
                            break;
                        case "Rain":
                            weatherConditionImage.setIcon(loadImage("/assets/rain.png"));
                            break;
                        case "Snow":
                            weatherConditionImage.setIcon(loadImage("/assets/snow.png"));
                            break;
                    }

                    // update temperature text
                    double temperature = (Double) weatherData.get("temperature");
                    temperatureText.setText(temperature + " C");

                    //update weather condition text
                    weatherConditionDesc.setText(weatherCondition);

                    // update humidity
                    long humidity = (long) weatherData.get("humidity");
                    humidityText.setText("<html><b>Humidity</b> "+ humidity + "%</html>");

                    //update windspeed
                    double windspeed = (double) weatherData.get("windspeed");
                    windspeedText.setText("<html><b>Windspeed </b>" + windspeed + "km/h</html>");
                }
            });
            add(searchButton);
        }
        // usato per creare immagini per i componenti
        private ImageIcon loadImage(String resourcePath) {
        try{
            // legge l'immagine dal path dato come parametro
            BufferedImage image = ImageIO.read(AppLauncher.class.getResourceAsStream(resourcePath));
            // ritorna l'immagine a icona così che i componenti possono renderizzarla
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
        }
}