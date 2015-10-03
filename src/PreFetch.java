/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Jaishreeganesh
 */
public class PreFetch {
    private String songIndexer;
    private boolean isCache;

    public PreFetch(String songIndexer) {
        this.songIndexer = songIndexer;
    }

    public String getSongIndexer() {
        return songIndexer;
    }

    public void setSongIndexer(String songIndexer) {
        this.songIndexer = songIndexer;
    }

    public boolean isIsCache() {
        return isCache;
    }

    public void setIsCache(boolean isCache) {
        this.isCache = isCache;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.songIndexer != null ? this.songIndexer.hashCode() : 0);
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
        final PreFetch other = (PreFetch) obj;
        if ((this.songIndexer == null) ? (other.songIndexer != null) : !this.songIndexer.equals(other.songIndexer)) {
            return false;
        }
        return true;
    }
        
}
