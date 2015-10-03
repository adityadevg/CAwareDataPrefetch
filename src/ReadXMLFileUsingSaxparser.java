
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadXMLFileUsingSaxparser extends DefaultHandler {

    private Song song;
    private String temp;
    private ArrayList<Song> songList = new ArrayList<Song>();
    Preferences prefs = Preferences.userNodeForPackage(CloudUtil.class);

    /**
     * The main method sets things up for parsing
     */
    public static void uploadFile(String filePath) {
        try {
            //Create a "parser factory" for creating SAX parsers
            SAXParserFactory spfac = SAXParserFactory.newInstance();

            //Now use the parser factory to create a SAXParser object
            SAXParser sp = spfac.newSAXParser();

            //Create an instance of this class; it defines all the handler methods
            ReadXMLFileUsingSaxparser handler = new ReadXMLFileUsingSaxparser();

            //Finally, tell the parser to parse the input and notify the handler
            sp.parse(filePath, handler);

            handler.readList();
        } catch (IOException ex) {
            Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /*
     * When the parser encounters plain text (not XML elements),
     * it calls(this method, which accumulates them in a string buffer
     */

    public void characters(char[] buffer, int start, int length) {
        temp = new String(buffer, start, length);
    }
    /*
     * Every time the parser encounters the beginning of a new element,
     * it calls this method, which resets the string buffer
     */

    public void startElement(String uri, String localName,
            String qName, Attributes attributes) throws SAXException {
        temp = "";
        if (qName.equalsIgnoreCase("Song")) {
            song = new Song();
        }
    }
    /*
     * When the parser encounters the end of an element, it calls this method
     */

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("Song")) {
            // add it to the list
            songList.add(song);
        } else if (qName.equalsIgnoreCase("Index")) {
            song.setIndex(temp);
        } else if (qName.equalsIgnoreCase("Title")) {
            song.setTitle(temp);
        } else if (qName.equalsIgnoreCase("Copyright")) {
            song.setCopyright(temp);
        } else if (qName.equalsIgnoreCase("Genre")) {
            song.setGenre(temp);
        } else if (qName.equalsIgnoreCase("Lyrics")) {
            song.setLyrics(temp);
        } else if (qName.equalsIgnoreCase("Artist")) {
            song.setArtist(temp);
        } else if (qName.equalsIgnoreCase("Duration")) {
            song.setDuration(temp);
        } else if (qName.equalsIgnoreCase("Roles")) {
            song.setRoles(temp);
        }
    }

    private void readList() {

        System.out.println("No of songs in list '" + songList.size() + "'.");
        Iterator<Song> it = songList.iterator();
        while (it.hasNext()) {
            try {
                boolean flag = false;
                Song objSong = it.next();
                String owner = prefs.get("USER_NAME", "");
                String index = objSong.getIndexer();
                objSong = new Encryption().EncryptData(objSong);
                flag = objSong.display();
                if (flag) {
                    new Encryption().CommutativeClientEncrypt(owner, index);
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchProviderException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidAlgorithmParameterException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ReadXMLFileUsingSaxparser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
