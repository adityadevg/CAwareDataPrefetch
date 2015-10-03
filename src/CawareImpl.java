/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jaishreeganesh
 */
public class CawareImpl {
    static List<String> cacheList = new ArrayList<String>();
    static List<String> serverList = new ArrayList<String>();
    private static Map<String, CachePreFetch> cacheMap = new HashMap<String, CachePreFetch>();
    private static Map<String, ServerPreFetch> serverMap = new HashMap<String, ServerPreFetch>();
    private static long networkLoad = 10000;
    public static long initTime = 0;
    public static long endTime = 0;
    
    public static List<String> songSequence = new ArrayList<String>();
    public static List<Long> responseTime = new ArrayList<Long>();
    public static List<Long> latencyFactor = new ArrayList<Long>();


    public static void main(String[] args) {
        cacheCawareData();
    }
    public static void cacheCawareData() {

        try {
            File fXmlFile = new File("C:\\Users\\adityadev\\Desktop\\lru.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

          //  System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("song");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String index = eElement.getElementsByTagName("index").item(0).getTextContent();
                    String load = eElement.getElementsByTagName("load").item(0).getTextContent();
                    String disk = eElement.getElementsByTagName("disk").item(0).getTextContent();
                    String sample = eElement.getElementsByTagName("sample").item(0).getTextContent();
                    songSequence.add(index);
                    performExecution(index, Long.parseLong(load)/200, Long.parseLong(disk)/200, Long.parseLong(sample)/200);
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(LruImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Map.Entry<String, CachePreFetch> entry : cacheMap.entrySet()) {
            String string = entry.getKey();
            CachePreFetch cachePreFetch = entry.getValue();
            System.out.println("cache : "+cachePreFetch.getSongIndexer()+ " - "+cachePreFetch.getMeanTime());
        }

        for (Map.Entry<String, ServerPreFetch> entry : serverMap.entrySet()) {
            String string = entry.getKey();
            ServerPreFetch serverPreFetch = entry.getValue();
            System.out.println("server : "+serverPreFetch.getSongIndexer()+ " - " +serverPreFetch.getMeanTime());
        }
        CacheStatistics1.createAndShowGui(songSequence, responseTime, latencyFactor);
    }

    public static void performExecution(String songIndexer, long networkload, long diskspeed, long sample) {
        System.out.println("Entered");
        initTime = System.currentTimeMillis();
        long cacheMeantime = 0;
        long serverMeantime = 0;
        boolean cFlag = false;
        boolean sFlag = false;

        if (cacheMap.containsKey(songIndexer)) {
            CachePreFetch c = cacheMap.get(songIndexer);
            cacheMeantime = c.getMeanTime();
        }
        if (serverMap.containsKey(songIndexer)) {
            ServerPreFetch s = serverMap.get(songIndexer);
            serverMeantime = s.getMeanTime();
        }


        if (cacheMap.containsKey(songIndexer)) {
            System.out.println("IF 1");
            CachePreFetch c1 = new CachePreFetch(songIndexer);
            ServerPreFetch s1 = new ServerPreFetch(songIndexer);
            c1.setSample(sample);
            s1.setSample(sample);
            
            if (cacheMap.size() > 4) {
                if (cacheMeantime < serverMeantime) {
                    String ind = cacheList.get(0);
                    cacheMap.remove(ind);
                    cacheList.remove(ind);
                    cacheList.add(songIndexer);
                    cacheMap.put(songIndexer, c1);
                    cFlag = true;
                } else {
                    cacheMap.remove(songIndexer);
                    cacheList.remove(songIndexer);
                    serverList.add(songIndexer);
                    serverMap.put(songIndexer, s1);
                    sFlag = true;
                }
            } else {
                if (cacheMeantime > serverMeantime) {
                    cacheMap.remove(songIndexer);
                    cacheList.remove(songIndexer);
                    serverList.add(songIndexer);
                    serverMap.put(songIndexer, s1);
                    sFlag = true;
                } else {
                    cacheMap.put(songIndexer, c1);
                    cacheList.add(songIndexer);
                    cFlag = true;
                }
            }
        } else {
            System.out.println("ELSE 1");
            Connection conn = DatabaseUtil.readDataBase();
            PreparedStatement preparedStatement = null;

            ResultSet rs = null;
            try {
                String sql = "Select indexer, title, genre, artist, lyrics from song where indexer = ?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, songIndexer);
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    if (!serverList.contains(songIndexer)) {
                        
                        cacheList.add(songIndexer);
                        CachePreFetch cpf = new CachePreFetch(songIndexer);
                        cacheMap.put(songIndexer, cpf);
                        ServerPreFetch spf = new ServerPreFetch(songIndexer);
                        serverMap.put(songIndexer, spf);
                        serverList.add(songIndexer);
                        cFlag = true;
                        sFlag = true;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(CawareImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (sFlag) {
            endTime = System.currentTimeMillis() + networkLoad;
            ServerPreFetch serverPreFetch = serverMap.get(songIndexer);
            serverPreFetch.setLastAccessHandleTime(endTime - initTime + networkload);
            serverPreFetch.setSample(calcServerSample(serverPreFetch));
            serverPreFetch.setTotalTime(calcServerTotalTime(serverPreFetch) + networkload);
            serverPreFetch.setMeanTime(calcServerMeanTime(serverPreFetch));
            serverMap.put(songIndexer, serverPreFetch);
            latencyFactor.add(networkLoad);
            responseTime.add(serverPreFetch.getMeanTime());
        } 
        if (cFlag) {
            endTime = System.currentTimeMillis() + diskspeed;
            CachePreFetch cachePreFetch = cacheMap.get(songIndexer);
            cachePreFetch.setLastAccessHandleTime(endTime - initTime + diskspeed);
            cachePreFetch.setSample(calcCacheSample(cachePreFetch));
            cachePreFetch.setTotalTime(calcCacheTotalTime(cachePreFetch) + diskspeed);
            cachePreFetch.setMeanTime(calcCacheMeanTime(cachePreFetch));
            cacheMap.put(songIndexer, cachePreFetch);
            latencyFactor.add(diskspeed);
            responseTime.add(cachePreFetch.getMeanTime());
        }
        sFlag = false;
        cFlag = false;
    }

    public static long calcCacheTotalTime(CachePreFetch cachePreFetch) {

        return ((7 * cachePreFetch.getTotalTime() + 128 * cachePreFetch.getLastAccessHandleTime()) / 8);
    }

    public static long calcServerTotalTime(ServerPreFetch serverPreFetch) {
        //ServerPreFetch serverPreFetch = new ServerPreFetch(song);
        return (7 * serverPreFetch.getTotalTime() + 128 * serverPreFetch.getLastAccessHandleTime() / 8);
    }

    public static long calcCacheSample(CachePreFetch cachePreFetch) {

        return (7 * cachePreFetch.getSample() + 256) / 8;
    }

    public static long calcServerSample(ServerPreFetch serverPreFetch) {
        return (7 * serverPreFetch.getSample() + 256) / 8;
    }

    public static long calcCacheMeanTime(CachePreFetch cachePreFetch) {
        cachePreFetch.setMeanTime((cachePreFetch.getTotalTime() + 128) / cachePreFetch.getSample());
        return cachePreFetch.getMeanTime();
    }

    public static long calcServerMeanTime(ServerPreFetch serverPreFetch) {
        return (serverPreFetch.getTotalTime() + 128) / serverPreFetch.getSample();
    }
}
