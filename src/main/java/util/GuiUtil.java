package util;

import exceptions.NodeNotFoundException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * A helper class, which provides helper functions
 */
public class GuiUtil {

    // static class
    private GuiUtil(){}

    /**
     * Searches recursively the requested node
     * @param parent the parent node, which should be searched for the requested node
     * @param id the requested node's id
     * @return the requested node
     * @throws NodeNotFoundException will be thrown, if the requested node is not in the parent node-tree
     */
    public static Node searchNodeById(Parent parent, String id) throws NodeNotFoundException {
        for(Node node: parent.getChildrenUnmodifiable()){
            if(node.getId() != null && node.getId().equals(id)) return node;
            else if(node instanceof Parent) {
                // Only if all children nodes threw an exception: throw the NodeNotFoundException
                try { return searchNodeById((Parent)node, id); }
                catch(NodeNotFoundException ignored){}
            }
        }
        throw new NodeNotFoundException("Node with id " + id + " in parent " + parent + " not found");
    }
}
