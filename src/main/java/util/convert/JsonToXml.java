package util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class transforms a given .json object into a xml persistence
 */
public class JsonToXml {

    /**
     * The actual program, which converts the .json file to a .xml file
     * @param args the necessary parameters for the program
     */
    public static void main(String... args) {

        // Assure, that there is a correct number of parameters in the console
        if(args.length<3) throw new IllegalArgumentException("You have to define arguments! Use the following format: JsonToXML jsonFile xmlFile tags");

        // Assure, that the first argument is a json file
        ConverterUtil.checkFormat(args[0],".json");

        // Assure, that the json file exists
        if(Files.notExists(Paths.get(args[0]))) throw new IllegalArgumentException(args[0] + " does not exist!");

        // Assure, that the second argument is a xml file
        ConverterUtil.checkFormat(args[1],".xml");

        // Use the other parameters as tags
        List<String> tagList = new ArrayList<>(Arrays.asList(args).subList(2, args.length));

        try {
            Stream<String> jsonStream = Files.lines(Paths.get(args[0]));
            StringBuilder jsonBuilder = new StringBuilder();
            jsonStream.forEach(jsonBuilder::append);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonBuilder.toString());

            List<JsonNode> list = getNodes(jsonNode, tagList);

            if(list.isEmpty()) throw new RuntimeException("Tags not found!");

            // Separate the list into their subparts
            List<List<JsonNode>> partsList = new ArrayList<>();
            for(int i=0; i<list.size(); i+=tagList.size()){
                List<JsonNode> subList = new ArrayList<>();
                for(int j=0; j<tagList.size(); j++) subList.add(list.get(i+j));
                partsList.add(subList);
            }

            // Remove duplicates
            Set<List<JsonNode>> set = new HashSet<>(partsList);
            partsList.clear();
            partsList.addAll(set);

            // Look for a primary key -> unique and a string
            Optional<Integer> primaryKeyIndex = Optional.empty();
            for(int i=0; i<tagList.size(); i++){

                // Check if the element at position "i" is a text node
                // Otherwise just continue
                if(!partsList.get(0).get(i).isTextual()) continue;

                // Create a temporary map, which counts the occurances of JsonNodes
                Map<JsonNode, Integer> map = new HashMap<>();

                // Count the occurances and sage them in the map
                for(List<JsonNode> p : partsList) map.merge(p.get(i), 1, (a, b) -> a + b);

                int counter = 0;
                // Check if the map contains every element only once
                for(Map.Entry<JsonNode, Integer> e : map.entrySet()) counter += e.getValue();
                if(counter == partsList.size()){
                    primaryKeyIndex = Optional.of(i);
                    break;
                }
            }

            // Check if primary key index exists
            if(!primaryKeyIndex.isPresent()) throw new RuntimeException("Primary Key not found");

            // Now you have the primary key index
            // Create the xml file, if it does not exist
            if(Files.notExists(Paths.get(args[1]))) {
                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                    // root element
                    // Get the root node (tag) for the xml file
                    String rootTag = ConverterUtil.fileName(args[1]);

                    Document doc = docBuilder.newDocument();
                    Element rootElement = doc.createElement(rootTag);
                    doc.appendChild(rootElement);

                    // simple elements (default name: "element")
                    for(List<JsonNode> l : partsList){
                        // Make a copy of the list and remove the primary key from the copied list
                        List<JsonNode> copy = new ArrayList<>(l);
                        copy.remove(primaryKeyIndex.get().intValue());

                        // Create the "element node" and add it to the root node
                        Element elem = doc.createElement("element");
                        rootElement.appendChild(elem);

                        // Add the primary key
                        JsonNode primary = l.get(primaryKeyIndex.get());
                        Attr prim = doc.createAttribute("primary");
                        prim.setValue(primary.asText());
                        elem.setAttributeNode(prim);

                        // Add all other variables
                        for(int i=0; i<copy.size(); i++){
                            Element nodeElem = doc.createElement("elem"+i);
                            nodeElem.appendChild(doc.createTextNode(copy.get(i).toString()));
                            elem.appendChild(nodeElem);
                        }
                    }

                    // Write the content into a xml file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File(args[1]));

                    // Create the actual xml file
                    transformer.transform(source, result);
                    System.out.println("File saved!");

                } catch (ParserConfigurationException | TransformerException e) {
                    System.err.println(e.getMessage());
                }
            }
            // TODO: Else just append the elements, if they not exist yet

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Returns a list with the given tags as keys and the found values
     * @param rootNode the root node to be traversed
     * @param tagList the tags to be searched for
     * @return the list in order, in which the elements were found
     */
    private static List<JsonNode> getNodes(JsonNode rootNode, List<String> tagList){
        Iterator<JsonNode> iter = rootNode.elements();

        List<JsonNode> tags = new ArrayList<>();
        while(iter.hasNext()){
            JsonNode next = iter.next();

            if(next.isContainerNode()){
                // find the tags in the container, if they are the next elements
                Iterator<Map.Entry<String, JsonNode>> entryIterator = next.fields();
                while(entryIterator.hasNext()){
                    Map.Entry<String, JsonNode> entry = entryIterator.next();

                    // check for the tags
                    for(String s : tagList) if(s.equals(entry.getKey())) tags.add(entry.getValue());
                }
            }
            tags.addAll(getNodes(next, tagList));
        }

        return tags;
    }
}
