/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package donutnv.familytree.DataBase;
import org.neo4j.driver.*;

/**
 *
 * @author COHOTECH
 */
public class ConnectDatbase {
    public static final String URL = "bolt://localhost:7687";
    public static final String USER = null;
    public static final String PASSWORD = null;
    public static Driver createDriver() {
        return GraphDatabase.driver(URL, AuthTokens.basic(USER, PASSWORD));
    }
}
