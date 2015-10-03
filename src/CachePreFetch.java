/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Jaishreeganesh
 */
public class CachePreFetch extends PreFetch {

    private long meanTime;
    private long sample;
    private long totalTime;
    private long lastAccessHandleTime;

    public CachePreFetch(String songIndexer) {
        super(songIndexer);
    }
    
    public long getMeanTime() {
        return meanTime;
    }

    public void setMeanTime(long meanTime) {
        this.meanTime = meanTime;
    }

    public long getSample() {
        return sample;
    }

    public void setSample(long sample) {
        this.sample = sample;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getLastAccessHandleTime() {
        return lastAccessHandleTime;
    }

    public void setLastAccessHandleTime(long lastAccessHandleTime) {
        this.lastAccessHandleTime = lastAccessHandleTime;
    }
    
    
}
