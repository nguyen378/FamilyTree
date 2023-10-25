/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package donutnv.familytree;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import donutnv.familytree.Controller.InformationController;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.swing.JFrame;
import javafx.scene.layout.Pane;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.jgrapht.graph.DefaultEdge;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import static org.neo4j.driver.Values.parameters;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Relationship;

/**
 *
 * @author COHOTECH
 */
class FoldableTree extends mxGraph {

    /**
     * Need to add some conditions that will get us the expand/collapse icon on
     * the vertex.
     */
    @Override
    public boolean isCellFoldable(Object cell, boolean collapse) {
        //I want to keep the original behavior for groups in case I use a group someday.
        boolean result = super.isCellFoldable(cell, collapse);
        if (!result) {
            //I also want cells with outgoing edges to be foldable...
            return this.getOutgoingEdges(cell).length > 0;
        }
        return result;
    }

    /**
     * Need to define how to fold cells for our DAG. In this case we want to
     * traverse the tree collecting all child vertices and then hide/show them
     * and their edges as needed.
     */
    @Override
    public Object[] foldCells(boolean collapse, boolean recurse, Object[] cells, boolean checkFoldable) {
        //super.foldCells does this so I will too...
        if (cells == null) {
            cells = getFoldableCells(getSelectionCells(), collapse);
        }

        this.getModel().beginUpdate();

        try {
            toggleSubtree(this, cells[0], !collapse);
            this.model.setCollapsed(cells[0], collapse);
            fireEvent(new mxEventObject(mxEvent.FOLD_CELLS, "cells", cells, "collapse", collapse, "recurse", recurse));
        } finally {
            this.getModel().endUpdate();
        }

        return cells;
    }

    // Updates the visible state of a given subtree taking into
    // account the collapsed state of the traversed branches
    private void toggleSubtree(mxGraph graph, Object cellSelected, boolean show) {
        List<Object> cellsAffected = new ArrayList<>();
        graph.traverse(cellSelected, true, new mxGraph.mxICellVisitor() {
            @Override
            public boolean visit(Object vertex, Object edge) {
                // We do not want to hide/show the vertex that was clicked by the user to do not
                // add it to the list of cells affected.
                if (vertex != cellSelected) {
                    cellsAffected.add(vertex);
                }

                // Do not stop recursing when vertex is the cell the user clicked. Need to keep
                // going because this may be an expand.
                // Do stop recursing when the vertex is already collapsed.
                return vertex == cellSelected || !graph.isCellCollapsed(vertex);
            }
        });

        graph.toggleCells(show, cellsAffected.toArray(), true/*includeEdges*/);
    }
}


public class FamilyTree extends javax.swing.JFrame {

    private static final long serialVersionUID = -2707712944901661771L;
    FoldableTree graph = new FoldableTree();
    Object root = null;
    private String username;
    private String sex;
    private String dateOfBirth;
    private String placeOfBirth;
    
    
    

    /**
     * Creates new form ChampsTree
     */
    public FamilyTree() {

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        // Set the background color of the graph component
        graphComponent.setBackground(new Color(0xEAE7D6));

        // Set the vertex style in the stylesheet
        mxStylesheet stylesheet = graph.getStylesheet();
        Map<String, Object> vertexStyle = stylesheet.getDefaultVertexStyle();
        vertexStyle.put(mxConstants.STYLE_FONTFAMILY, "Arial");
        vertexStyle.put(mxConstants.STYLE_FONTCOLOR, "#5D7B6F");
        vertexStyle.put(mxConstants.STYLE_FONTSTYLE, Font.PLAIN);

        // Create the compact tree layout
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        layout.setUseBoundingBox(false);
        layout.setEdgeRouting(false);
        layout.setLevelDistance(30);
        layout.setNodeDistance(10);

        // Add the graph component to the content pane
        getContentPane().add(graphComponent);

        Object parent = graph.getDefaultParent();
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
            String cypherQuery = "MATCH (root:Information)-[r:Has_Relation]->()\n"
                    + "WHERE NOT (()-[:Has_Relation]->(root)) AND (r.relation='Cha con' OR r.relation='Mẹ con')"
                    + "OPTIONAL MATCH (root)-[o:Has_Relation{relation:'Vợ chồng'}]-(spouse:Information)"
                    + "RETURN DISTINCT coalesce(root, {}) AS root, spouse,r,o";
            Result result = session.run(cypherQuery);

            Record record = result.next();
            Node rootG = record.get("root").asNode();
            String rootName = rootG.get("name").asString();
            int rootId = rootG.get("id").asInt();

            Node spouse = record.get("spouse").asNode();
            String spouseName = spouse.get("name").asString();
            int spouseId = spouse.get("id").asInt();
            Relationship relationship = record.get("o").asRelationship();
            String relationshipName = relationship.get("relation").asString();

            graph.getModel().beginUpdate();
            try {
                int xOffset = 0;
                root = graph.insertVertex(parent, rootId + "", rootName, 20, 20, 100, 50);
                int spouseXOffset = xOffset + 150;

                Object spouseCell = graph.insertVertex(parent, spouseId + "", spouseName, 120, 20, 100, 50);
                graph.insertEdge(parent, null, relationshipName, root, spouseCell, "sourcePort=0;targetPort=0;points=[[50, 25], [150, 25]]");

                xOffset = spouseXOffset;

                Map<Object, Node> childMap = loadAndDisplayGenerations(parent, root, rootName, "Cha con");
                Map<Object, Node> grandChild = loadAndDisplayGrandchildren(parent, childMap, "Cha con");
                loadAndDisplayGreatchildren(parent, grandChild, "Cha con");
                loadAndDisplayGreatchildren(parent, grandChild, "Mẹ con");
                layout.execute(parent);
            } finally {
                graph.getModel().endUpdate();
            }

        }

        graph.addListener(mxEvent.FOLD_CELLS, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
                layout.execute(graph.getDefaultParent());
            }
        });
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Kiểm tra cho sự click đơn
                if (e.getClickCount() == 1) {
                    // Lấy cell tại tọa độ chuột
                    Object cell = graphComponent.getCellAt(e.getX(), e.getY());

                    if (cell instanceof mxICell) {
                        mxICell mxCell = (mxICell) cell;

                        if (mxCell.isVertex()) {
                            // Lấy giá trị của node name từ cell
                            String nodeName = (String) mxCell.getValue();
                            // Truy vấn Neo4j và hiển thị thông tin
                            String info = queryNeo4j(nodeName);
                            
                        }
                    }
                }
            }
        });

        getContentPane().add(graphComponent);
    }

    public Map<Object, Node> loadAndDisplayGenerations(Object parent, Object root, String rootName, String relationType) {
        Map<Object, Node> childMap = new HashMap<>();
        String cypherQuery = "MATCH (root:Information{name:\"Nguyễn Ngọc Ðoàn \"})-[r:Has_Relation{relation:\"Cha con\"}]->(nextgen:Information)"
                + "OPTIONAL MATCH (nextgen)-[o:Has_Relation{relation:\"Vợ chồng\"}]-(spouse:Information)"
                + "RETURN nextgen, spouse,r,o";
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
            Result result = session.run(cypherQuery, parameters("rootName", rootName, "relationType", relationType));
            while (result.hasNext()) {
                int xOffset = 0;
                Record record = result.next();
                Node nextgenNode = record.get("nextgen").asNode();
                String nextgenName = nextgenNode.get("name").asString();
                int nextgenId = nextgenNode.get("id").asInt();
                Node spouse = record.get("spouse").asNode();
                String spouseName = spouse.get("name").asString();
                int spouseId = spouse.get("id").asInt();
                Relationship relationshipPa = record.get("r").asRelationship();
                String relationshipPaName = relationshipPa.get("relation").asString();
                Relationship relationshipSP = record.get("o").asRelationship();
                String relationshipSPName = relationshipSP.get("relation").asString();

                graph.getModel().beginUpdate();
                try {
                    // Tạo đỉnh cho đời con
                    Object nextgenVertex = graph.insertVertex(parent, Integer.toString(nextgenId), nextgenName, 0, 0, 100, 50);
                    Object spouseVertex = graph.insertVertex(parent, Integer.toString(spouseId), spouseName, xOffset, 60, 100, 50);

                    // Lưu thông tin của đời con vào childMap
                    childMap.put(nextgenVertex, nextgenNode);

                    // Tạo cạnh kết nối từ nút gốc đến đời con
                    graph.insertEdge(parent, "nextgenId ", relationshipSPName, nextgenVertex, spouseVertex);
                    graph.insertEdge(parent, null, relationshipPaName, root, nextgenVertex);
                    xOffset += 150;
                } finally {
                    graph.getModel().endUpdate();
                }
            }
        }
        return childMap;
    }

    public Map<Object, Node> loadAndDisplayGrandchildren(Object parent, Map<Object, Node> childMap, String relationType) {
        Map<Object, Node> grandChild = new HashMap<>();
        for (Map.Entry<Object, Node> entry : childMap.entrySet()) {
            Object childVertex = entry.getKey();
            Node childNode = entry.getValue();
            String childName = childNode.get("name").asString();
            String spouseName = null;
            int spouseId = 0;
            String relationshipPaName = null;
            String relationshipSPName = null;

            String cypherQuery = "MATCH (child:Information{name:$childName})-[r:Has_Relation{relation:$relationType}]->(grandchild:Information)"
                    + "OPTIONAL MATCH (grandchild)-[o:Has_Relation{relation:'Vợ chồng'}]-(spouse:Information)"
                    + "RETURN grandchild, spouse, r, o";
            try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
                Result result = session.run(cypherQuery, parameters("childName", childName, "relationType", relationType));
                while (result.hasNext()) {
                    int xOffset = 0;
                    Record record = result.next();
                    Node grandchildNode = record.get("grandchild").asNode();
                    String grandchildName = grandchildNode.get("name").asString();
                    int grandchildId = grandchildNode.get("id").asInt();
                    Node spouse = null;
                    Value spouseValue = record.get("spouse");
                    if (!spouseValue.isNull()) {
                        spouse = spouseValue.asNode();
                        spouseName = spouse.get("name").asString();
                        spouseId = spouse.get("id").asInt();
                    }
                    Relationship relationshipPa = null;
                    Value relationshipPaValue = record.get("r");
                    if (!relationshipPaValue.isNull()) {
                        relationshipPa = relationshipPaValue.asRelationship();
                        relationshipPaName = relationshipPa.get("relation").asString();
                    }
                    Relationship relationshipSP = null;
                    Value relationshipSPValue = record.get("o");
                    if (!relationshipSPValue.isNull()) {
                        relationshipSP = relationshipSPValue.asRelationship();
                        relationshipSPName = relationshipSP.get("relation").asString();
                    }

                    graph.getModel().beginUpdate();
                    try {
                        // Tạo đỉnh cho nút cháu
                        Object grandchildVertex = graph.insertVertex(parent, Integer.toString(grandchildId), grandchildName, 0, 0, 100, 50);
                        Object spouseVertex = graph.insertVertex(parent, Integer.toString(spouseId), spouseName, xOffset, 60, 100, 50);

                        // Tạo cạnh kết nối từ nút con đến nút cháu
                        graph.insertEdge(parent, null, relationshipPaName, childVertex, grandchildVertex);
                        graph.insertEdge(parent, null, relationshipSPName, grandchildVertex, spouseVertex);

                        // Lưu thông tin của nút cháu vào grandChildMap
                        grandChild.put(grandchildVertex, grandchildNode);

                        xOffset += 150;
                    } finally {
                        graph.getModel().endUpdate();
                    }
                }
            }
        }
        return grandChild;
    }

    public Map<Object, Node> loadAndDisplayGreatchildren(Object parent, Map<Object, Node> grandchilddMap, String relationType) {
        Map<Object, Node> greatChild = new HashMap<>();
        for (Map.Entry<Object, Node> entry : grandchilddMap.entrySet()) {
            Object childVertex = entry.getKey();
            Node childNode = entry.getValue();
            String childName = childNode.get("name").asString();
            String spouseName = null;
            int spouseId = 0;
            String relationshipPaName = null;
            String relationshipSPName = null;

            String cypherQuery = "MATCH (child:Information{name:$childName})-[r:Has_Relation{relation:$relationType}]->(grandchild:Information)"
                    + "OPTIONAL MATCH (grandchild)-[o:Has_Relation{relation:'Vợ chồng'}]-(spouse:Information)"
                    + "RETURN grandchild, spouse, r, o";
            try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
                Result result = session.run(cypherQuery, parameters("childName", childName, "relationType", relationType));
                while (result.hasNext()) {
                    int xOffset = 0;
                    Record record = result.next();
                    Node grandchildNode = record.get("grandchild").asNode();
                    String grandchildName = grandchildNode.get("name").asString();
                    int grandchildId = grandchildNode.get("id").asInt();
                    Node spouse = null;
                    Value spouseValue = record.get("spouse");
                    if (!spouseValue.isNull()) {
                        spouse = spouseValue.asNode();
                        spouseName = spouse.get("name").asString();
                        spouseId = spouse.get("id").asInt();
                    }
                    Relationship relationshipPa = null;
                    Value relationshipPaValue = record.get("r");
                    if (!relationshipPaValue.isNull()) {
                        relationshipPa = relationshipPaValue.asRelationship();
                        relationshipPaName = relationshipPa.get("relation").asString();
                    }
                    Relationship relationshipSP = null;
                    Value relationshipSPValue = record.get("o");
                    if (!relationshipSPValue.isNull()) {
                        relationshipSP = relationshipSPValue.asRelationship();
                        relationshipSPName = relationshipSP.get("relation").asString();
                    }

                    graph.getModel().beginUpdate();
                    try {
                        // Tạo đỉnh cho nút cháu
                        Object grandchildVertex = graph.insertVertex(parent, Integer.toString(grandchildId), grandchildName, 0, 0, 100, 50);
                        Object spouseVertex = graph.insertVertex(parent, Integer.toString(spouseId), spouseName, xOffset, 60, 100, 50);

                        // Tạo cạnh kết nối từ nút con đến nút cháu
                        graph.insertEdge(parent, null, relationshipPaName, childVertex, grandchildVertex);
                        graph.insertEdge(parent, null, relationshipSPName, grandchildVertex, spouseVertex);

                        // Lưu thông tin của nút cháu vào grandChildMap
                        greatChild.put(grandchildVertex, grandchildNode);

                        xOffset += 150;
                    } finally {
                        graph.getModel().endUpdate();
                    }
                }
            }
        }
        return greatChild;
    }

    private String queryNeo4j(String nodeName) {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@"))) {
            try (Session session = driver.session()) {
                String query = "MATCH (person:Information) WHERE person.name = $name "
                        + "RETURN person.name AS name, person.sex AS sex, "
                        + "person.dateOfBirth AS dateOfBirth, "
                        + "person.placeOfBirth AS placeOfBirth";
                Result result = session.run(query, Values.parameters("name", nodeName));

                if (result.hasNext()) {
                    Record record = result.next();
                    username = record.get("name").asString();
                    sex = record.get("sex").asString();
                    dateOfBirth = record.get("dateOfBirth").asLocalDate().toString();
                    placeOfBirth = record.get("placeOfBirth").asString();
                } else {
                    return "Person not found!";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while querying Neo4j.";
        }
        return null;
    }
//     private void initializeJavaFX(Pane jfxPanel) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/Information.fxml"));
//            Scene scene = new Scene(loader.load());
//
//            // Set the scene to the JFXPanel
//            jfxPanel.setScene(scene);
//
//            // Get the controller instance
//            InformationController controller = loader.getController();
//
//            // You can now call methods or access properties of the controller
//            controller.setInformation("John Doe", "Male", "1990-01-01", "City");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 255, 51));
        setFont(new java.awt.Font("Pristina", 0, 14)); // NOI18N
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FamilyTree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FamilyTree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FamilyTree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FamilyTree.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FamilyTree frame = new FamilyTree();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(2000, 600);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);

            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
