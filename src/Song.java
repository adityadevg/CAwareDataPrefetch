
import java.util.prefs.Preferences;

public class Song {

    Preferences prefs = Preferences.userNodeForPackage(CloudUtil.class);
    private String indexer;
    private String title;
    private String copyright;
    private String genre;
    private String lyrics;
    private String artist;
    private String duration;
    private String roles;
    private String ownerID;

    public Song() {
    }

    public Song(String indexer, String title, String copyright, String genre, String lyrics, String artist, String duration, String roles) {
        this.indexer = indexer;
        this.title = title;
        this.copyright = copyright;
        this.genre = genre;
        this.lyrics = lyrics;
        this.artist = artist;
        this.duration = duration;
        this.roles = roles;
    }

    public String getIndexer() {
        return indexer;
    }

    public void setIndex(String indexer) {
        this.indexer = indexer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getOwner() {
        return prefs.get("USER_NAME", "");
    }

    public void setOwner(String owner) {
        this.ownerID = prefs.get("USER_NAME", "");
    }

    public boolean display() {
        boolean result = false;
        DbConnect mydb = new DbConnect();
        mydb.indexer = getIndexer();
        mydb.title = getTitle();
        mydb.copyright = getCopyright();
        mydb.genre = getGenre();
        mydb.lyrics = getLyrics();
        mydb.artist = getArtist();
        mydb.duration = getDuration();
        mydb.roles = getRoles();
        mydb.createTable();
        result = mydb.insertTable();
        return result;
    }
}
