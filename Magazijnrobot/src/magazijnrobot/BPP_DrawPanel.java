package magazijnrobot;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import magazijnrobot.*;

public class BPP_DrawPanel extends JPanel {
    
    LiveView result;
    private int x = 50, y = 90, yProduct = 90, volume = 0, volumeDozen = 0, BinSize = 0;
    ArrayList<Bin> bins = new ArrayList<>();

    public BPP_DrawPanel(LiveView result, ArrayList<Bin> Bins, int Volume, int BinSize) {
        this.result = result;
        this.bins = Bins;
        this.volume = Volume;
        this.volumeDozen = BinSize * Bins.size();
        this.BinSize = BinSize;
        int prefsize = (Bins.size() / 10) * (BinSize * 10) + (Bins.size() / 10 * 50);
        this.setIgnoreRepaint(true);
        this.setPreferredSize(new java.awt.Dimension(1200, prefsize + 100));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        x = 50;
        y = 90;
        yProduct = 90;
        g.drawString("Aantal dozen: " + bins.size(), x, 40);
        g.drawString("Totaal volume producten: " + volume + "     Totaal volume dozen: " + volumeDozen, x, 60);
        
        for (Bin bin : bins) {
            g.setColor(Color.BLACK);
            g.drawString("Grootte: " + bin.getCurrentSize(), x, y - 10);
            g.drawRect(x, y, 50, bin.getMaxSize() * 10);
            yProduct = y;
            for (Product product : bin.getProducts()) {
                double hue = Math.random();
                int rgb;
                rgb = Color.HSBtoRGB((float) hue, 0.8f, 0.8f);
                Color color = new Color(rgb);
                g.setColor(color);
                g.fillRect(x, yProduct, 50, product.getVolume() * 10);
                yProduct += product.getVolume() * 10;
                g.setColor(Color.black);
                g.drawString(Integer.toString(product.getVolume()), x + 55, yProduct);
            }
            x += 100;
            if (x > 1000) {
                y += (BinSize * 10) + 50;
                x = 50;
            }
        }
        
    }
    
}