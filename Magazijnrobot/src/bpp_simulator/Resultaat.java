package bpp_simulator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import bpp_simulator.algoritmes.Algoritme;

public class Resultaat extends JFrame {

    private ArrayList<Bin> Bins = new ArrayList<>();
    private JButton jbSave;
    private Algoritme Algoritme;
    private int Volume, VolumeBoxes = 0;
    private TekenPanel drawPanel;
    public Resultaat(ArrayList<Bin> Bins, Algoritme Algoritme, int Volume, int BoxSize) {
        this.Bins = Bins;
        this.Algoritme = Algoritme;
        this.Volume = Volume;
        this.VolumeBoxes = VolumeBoxes;
//		setSize(1300, 803);
        setTitle("Resultaat");
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        drawPanel = new TekenPanel(this, Bins, Algoritme, Volume, BoxSize);
        JScrollPane scrollFrame = new JScrollPane(drawPanel);

        if (Bins.size() < 30) {
            scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        } else {
            scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
        scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        drawPanel.setAutoscrolls(true);
        scrollFrame.setPreferredSize(new Dimension(1200, 800));

        this.add(scrollFrame);
        pack();
        setResizable(false);
    }

    public void SaveScreen(String Location) {
        // http://stackoverflow.com/questions/5655908/export-jpanel-graphics-to-png-or-gif-or-jpg
        BufferedImage bi = new BufferedImage(drawPanel.getSize().width, drawPanel.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        drawPanel.paint(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(Location));
        } catch (Exception er) {
        }
    }

    public ArrayList<Bin> getBins() {
        return Bins;
    }

    public Algoritme getAlgoritme() {
        return Algoritme;
    }

}
