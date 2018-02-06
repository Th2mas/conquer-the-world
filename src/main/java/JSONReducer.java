import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JSONReducer {
    public static void main(String[] args) {



        try (BufferedReader reader = new BufferedReader(new InputStreamReader(JSONReducer.class.getResource("./json/countries.json").openStream()))){
            String line;
            while((line = reader.readLine()) != null){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
