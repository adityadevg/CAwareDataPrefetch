/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Jaishreeganesh
 */
public class CacheSong {
    private String songindexer;
    private String title;
    private String copyright;
    private String genre;
    private String lyrics;
    private String artist;
    private String duration;
    private String role;

    public CacheSong(String songindexer) {
        this.songindexer = songindexer;
    }

    public CacheSong(String songindexer, String title, String copyright, String genre, String lyrics, String artist, String duration, String role) {
        this.songindexer = songindexer;
        this.title = title;
        this.copyright = copyright;
        this.genre = genre;
        this.lyrics = lyrics;
        this.artist = artist;
        this.duration = duration;
        this.role = role;
    }

    public String getSongindexer() {
        return songindexer;
    }

    public void setSongindexer(String songindexer) {
        this.songindexer = songindexer;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.songindexer != null ? this.songindexer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CacheSong other = (CacheSong) obj;
        if ((this.songindexer == null) ? (other.songindexer != null) : !this.songindexer.equals(other.songindexer)) {
            return false;
        }
        return true;
    }
    
    
    
}
