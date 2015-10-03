/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jaishreeganesh
 */
public class LruImpl {

    private static List<CacheSong> dataList = new ArrayList<CacheSong>();
    public static List<String> songSequence = new ArrayList<String>();
    public static List<Long> responseTime = new ArrayList<Long>();
    public static List<Long> latencyFactor = new ArrayList<Long>();
    public static long executionFactor = 10;
    //private long networkLoad = 10000;

    public static void main(String[] args) {
        start();
    }
    
    public static void start() {
            try {
            File fXmlFile = new File("C:\\Users\\adityadev\\Desktop\\lru.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

//            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("song");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

  //              System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String index = eElement.getElementsByTagName("index").item(0).getTextContent();
                    String load = eElement.getElementsByTagName("load").item(0).getTextContent();
                    String disk = eElement.getElementsByTagName("disk").item(0).getTextContent();
                    songSequence.add(index);
                    
                    getData(index, Long.parseLong(load)/200, Long.parseLong(disk)/200);
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        CacheStatistics1.createAndShowGui(songSequence, responseTime, latencyFactor);
    }

    private static long getData(String songIndexer, long networkLoad, long diskSpeed) {
        long latency = 0;
        System.out.println("Song name : " +songIndexer);
        boolean songFound = false;
        long initTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        CacheSong song = new CacheSong(songIndexer);
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.contains(song)) {
                System.out.println("song found ..");
                songFound = true;
                latency = diskSpeed;
                latencyFactor.add(latency);
                endTime = System.currentTimeMillis() + diskSpeed + executionFactor;
                break;
            }
        }
        
        
        if (!songFound) {
            try {
                System.out.println("Getting data from Database");
                ResultSet resultSet = getSongFromDB(songIndexer);
                while (resultSet.next()) {
                    CacheSong song1 = new CacheSong(resultSet.getString("indexer"));
                    if (song.equals(song1)) {
                        System.out.println("Song found from database");
                        if(dataList.size() > 4) {
                            dataList.remove(0);
                        }
                        dataList.add(song);
                        endTime = System.currentTimeMillis() + networkLoad + diskSpeed + executionFactor;
                        latency = networkLoad + diskSpeed;
                        //System.out.println("networkLoad + diskSpeed = " +(networkLoad + diskSpeed));
                        latencyFactor.add(latency);
                        break;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("" + latency);
        System.out.println("response time : " +(endTime - initTime));
        responseTime.add(endTime - initTime);
        
        return endTime - initTime;
    }

    public static ResultSet getSongFromDB(String indexer) {
        Connection conn = DatabaseUtil.readDataBase();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            String sql = "Select indexer, title, genre, artist, lyrics from song where indexer = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, indexer);
            rs = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
}
