// display the gui
import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                // display app gui
               new WeatherAppGUI().setVisible(true);
                //System.out.println(WeatherApp.getLocationData("Tokyo"));

                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
