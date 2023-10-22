/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package donutnv.familytree;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import static org.neo4j.driver.Values.parameters;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;

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
    Object parent = graph.getDefaultParent();
    Object root = null;

    /**
     * Creates new form ChampsTree
     */
    public FamilyTree() {

        super("Hello, World!");
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        layout.setUseBoundingBox(false);
        layout.setEdgeRouting(false);
        layout.setLevelDistance(30);
        layout.setNodeDistance(10);

        Object parent = graph.getDefaultParent();
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
            String cypherQuery = "match (root:Information)-[r:Has_Relation]->()"
                    + "where not (()-[:Has_Relation]->(root)) and (r.relation= 'Cha con' or r.relation= 'Mẹ con')"
                    + "return root";
            Result result = session.run(cypherQuery);

            Record record = result.next();
            Node rootG = record.get("root").asNode();
            String rootName = rootG.get("name").asString();
            int rootId = rootG.get("id").asInt();
            graph.getModel().beginUpdate();
            try {
                root = graph.insertVertex(parent, rootId + "", rootName, 0, 0, 100, 50);
                Map<Object, Node> childMap = loadAndDisplayGenerations(parent, root, rootName, "Cha con");
                loadAndDisplayGrandchildren(parent, childMap, "Cha con");
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

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public Map<Object, Node> loadAndDisplayGenerations(Object parent, Object root, String rootName, String relationType) {
        Map<Object, Node> childMap = new HashMap<>();
        String cypherQuery = "MATCH (root:Information{name:$rootName})-[r:Has_Relation{relation:$relationType}]->(nextgen:Information) RETURN nextgen";
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
            Result result = session.run(cypherQuery, parameters("rootName", rootName, "relationType", relationType));
            while (result.hasNext()) {
                Record record = result.next();
                Node nextgenNode = record.get("nextgen").asNode();
                String nextgenName = nextgenNode.get("name").asString();
                int nextgenId = nextgenNode.get("id").asInt();

                graph.getModel().beginUpdate();
                try {
                    // Tạo đỉnh cho đời con
                    Object nextgenVertex = graph.insertVertex(parent, Integer.toString(nextgenId), nextgenName, 0, 0, 100, 50);

                    // Lưu thông tin của đời con vào childMap
                    childMap.put(nextgenVertex, nextgenNode);

                    // Tạo cạnh kết nối từ nút gốc đến đời con
                    graph.insertEdge(parent, null, "", root, nextgenVertex);
                } finally {
                    graph.getModel().endUpdate();
                }
            }
        }
        return childMap;
    }

    public void loadAndDisplayGrandchildren(Object parent, Map<Object, Node> childMap, String relationType) {
        for (Map.Entry<Object, Node> entry : childMap.entrySet()) {
            Object childVertex = entry.getKey();
            Node childNode = entry.getValue();
            String childName = childNode.get("name").asString();

            String cypherQuery = "MATCH (child:Information{name:$childName})-[r:Has_Relation{relation:$relationType}]->(grandchild:Information) RETURN grandchild";
            try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Phat121002@")); Session session = driver.session()) {
                Result result = session.run(cypherQuery, parameters("childName", childName, "relationType", relationType));
                while (result.hasNext()) {
                    Record record = result.next();
                    Node grandchildNode = record.get("grandchild").asNode();
                    String grandchildName = grandchildNode.get("name").asString();
                    int grandchildId = grandchildNode.get("id").asInt();

                    graph.getModel().beginUpdate();
                    try {
                        // Tạo đỉnh cho nút cháu
                        Object grandchildVertex = graph.insertVertex(parent, Integer.toString(grandchildId), grandchildName, 0, 0, 100, 50);

                        // Tạo cạnh kết nối từ nút con đến nút cháu
                        graph.insertEdge(parent, null, "", childVertex, grandchildVertex);
                    } finally {
                        graph.getModel().endUpdate();
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
                frame.setSize(900, 320);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);

            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
