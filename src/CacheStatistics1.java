/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class CacheStatistics1 extends JPanel {

    private static final int MAX_SCORE = 30;
    private static final int PREF_W = 800;
    private static final int PREF_H = 650;
    private static final int BORDER_GAP = 30;
    private static final Color GRAPH_COLOR = Color.red;
    private static final Color GRAPH_POINT_COLOR = new Color(0, 0, 0, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private static final int GRAPH_POINT_WIDTH = 12;
    private static final int Y_HATCH_CNT = 10;
    List<String> songSequence;
    List<Long> responseTime;
    List<Long> latencyFactor;
    private static final Color GRAPH_COLOR_1 = Color.green;
    private static final Color GRAPH_POINT_COLOR_1 = new Color(200, 200, 200, 180);
    private static final Stroke GRAPH_STROKE_1 = new BasicStroke(2f);
    

    public CacheStatistics1(List<String> songSequence, List<Long> responseTime, List<Long> latencyFactor) {
        this.songSequence = songSequence;
        this.responseTime = responseTime;
        this.latencyFactor = latencyFactor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (songSequence.size() - 1);
        double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);

        //List<Point> graphPoints = new ArrayList<Point>();
        List<Point> graphLatencyPoints = new ArrayList<Point>();
        List<Point> graphResponsePoints = new ArrayList<Point>();
        for (int i = 0; i < songSequence.size(); i++) {
            int x1 = (int) (i * xScale + BORDER_GAP);
            int y1 = (int) ((MAX_SCORE - latencyFactor.get(i)) * yScale + BORDER_GAP);
            int y2 = (int) ((MAX_SCORE - responseTime.get(i)) * yScale + BORDER_GAP);
            graphLatencyPoints.add(new Point(x1, y1));
            graphResponsePoints.add(new Point(x1, y2));
        }

        // create x and y axes 
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

        // create hatch marks for y axis. 
        for (int i = 0; i < Y_HATCH_CNT; i++) {
            int x0 = BORDER_GAP;
            int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
            int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
            //g2.drawString(Integer.toString(BORDER_GAP), x1, y1);
        }

        // and for x axis
        for (int i = 0; i < songSequence.size() - 1; i++) {
            //System.out.println("" + getWidth());
            int x0 = (i) * (getWidth() - BORDER_GAP * 2) / (songSequence.size() - 1) + BORDER_GAP;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP;
            int y1 = y0 - GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString(songSequence.get(i), x0, y0);
        }
        // show latency time
        Stroke oldStroke = g2.getStroke();
        g2.setColor(GRAPH_COLOR);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphLatencyPoints.size() - 1; i++) {
            int x1 = graphLatencyPoints.get(i).x;
            int y1 = graphLatencyPoints.get(i).y;
            int x2 = graphLatencyPoints.get(i + 1).x;
            int y2 = graphLatencyPoints.get(i + 1).y;
            System.out.println(x1+"-"+x2+"-"+y1+"-"+y2);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(GRAPH_POINT_COLOR);
        for (int i = 0; i < graphLatencyPoints.size(); i++) {
            int x = graphLatencyPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
            int y = graphLatencyPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
            g2.drawString(Long.toString(latencyFactor.get(i)), x, y);
        }
        
        
        // show re tsponseime
        Stroke oldStroke1 = g2.getStroke();
        g2.setColor(GRAPH_COLOR_1);
        g2.setStroke(GRAPH_STROKE_1);
        for (int i = 0; i < graphResponsePoints.size() - 1; i++) {
            int x1 = graphResponsePoints.get(i).x;
            int y1 = graphResponsePoints.get(i).y;
            int x2 = graphResponsePoints.get(i + 1).x;
            int y2 = graphResponsePoints.get(i + 1).y;
            System.out.println(x1+"-"+x2+"-"+y1+"-"+y2);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke1);
        g2.setColor(GRAPH_POINT_COLOR_1);
        for (int i = 0; i < graphResponsePoints.size(); i++) {
            int x = graphResponsePoints.get(i).x - GRAPH_POINT_WIDTH / 2;
            int y = graphResponsePoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
            g2.drawString(Long.toString(responseTime.get(i)), x, y);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    public static void createAndShowGui(List<String> songSequence, List<Long> responseTime, List<Long> latencyFactor) {
        System.out.println("Entered Graph !!");
        List<Integer> scores = new ArrayList<Integer>();
        Random random = new Random();
        int maxDataPoints = 16;
        int maxScore = 30;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add(random.nextInt(maxScore));
        }
        CacheStatistics1 mainPanel = new CacheStatistics1(songSequence, responseTime, latencyFactor);
        CacheStatistics1 mainPanel1 = new CacheStatistics1(songSequence, responseTime, latencyFactor);

        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}