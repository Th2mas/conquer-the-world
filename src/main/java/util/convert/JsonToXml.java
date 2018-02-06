package util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class transforms a given .json object into a xml persistence
 */
public class JsonToXml {

    public static void main(String... args) {

        // Assure, that there is a correct number of parameters in the console
        if(args.length<3) throw new IllegalArgumentException("You have to define arguments! Use the following format: JsonToXML jsonFile xmlFile tags");

        // Assure, that the first argument is a json file
        ConverterUtil.checkFormat(args[0],".json");

        // Assure, that the json file exists
        if(Files.notExists(Paths.get(args[0]))) throw new IllegalArgumentException(args[0] + " does not exist!");

        // Assure, that the second argument is a xml file
        ConverterUtil.checkFormat(args[1],".xml");

        // Get the root node of the xml file: should always be the same name, as the file name
        String rootNode = ConverterUtil.fileName(args[1]);

        // Use the other parameters as tags
        List<String> tagList = new ArrayList<>(Arrays.asList(args).subList(2, args.length));

        try {
            Stream<String> jsonStream = Files.lines(Paths.get(args[0]));
            StringBuilder jsonBuilder = new StringBuilder();
            jsonStream.forEach(jsonBuilder::append);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonBuilder.toString());
            // Create a temporary map for storing the countries with their coordinates
            Map<String, List<Point2D>> map = new HashMap<>();

            // TODO: Make me more generic for generic use?
            printNodes(jsonNode);


        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printNodes(JsonNode rootNode){
        if(rootNode.isContainerNode()) {
            rootNode.fields().forEachRemaining(e -> {
                System.out.println(e.getKey() + " " + e.getValue().getNodeType());
                printNodes(e.getValue());
            });
        } else if(rootNode.isArray()){

        }
        else System.out.println(rootNode.toString());

    }
}
