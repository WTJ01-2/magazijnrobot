package bpp_simulator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import bpp_simulator.algoritmes.Algoritme;
import bpp_simulator.algoritmes.Bestfit;
import bpp_simulator.algoritmes.Bruteforce;
import bpp_simulator.algoritmes.EigenAlgoritme;
import bpp_simulator.algoritmes.Firstfit;
import bpp_simulator.algoritmes.Nextfit;


public class Simulatie extends javax.swing.JFrame implements MouseListener, ActionListener, Runnable {

    private ArrayList<Product> ArrayProducts = new ArrayList<>();
    private final ArrayList<Resultaat> ArrayResults = new ArrayList<>();
    private final int BoxSize, vol;
    private StringBuilder endResult = new StringBuilder();
    private Thread t;
    private final Algoritme Algoritmes;
    private Bruteforce BruteForceAlgoritme;
    private Nextfit NextFitAlgoritme;
    private Firstfit FirstFitAlgoritme;
    private Bestfit BestFitAlgoritme;
    private EigenAlgoritme EigenAlgoritme;
    private Resultaat BruteForceResult, NextFitResult, FirstFitResult, BestFitResult, EigenFitResult;
    private final SelectieScherm selectieScherm;

    public Simulatie(SelectieScherm selectieScherm, ArrayList<Product> ArrayProducts, int BoxSize, boolean[] Algorithms) {
        initComponents();
        this.selectieScherm = selectieScherm;
        this.ArrayProducts = ArrayProducts;
        this.BoxSize = BoxSize;
        Algoritmes = new Algoritme();
        if (Algorithms[0]) {
            jlNextFitStatus.setText("In wachtrij");
            NextFitAlgoritme = new Nextfit();
            Algoritmes.addAlgoritme(NextFitAlgoritme);
        }
        if (Algorithms[1]) {
            jlFirstFitStatus.setText("In wachtrij");
            FirstFitAlgoritme = new Firstfit();
            Algoritmes.addAlgoritme(FirstFitAlgoritme);
        }
        if (Algorithms[2]) {
            jlBestFitStatus.setText("In wachtrij");
            BestFitAlgoritme = new Bestfit();
            Algoritmes.addAlgoritme(BestFitAlgoritme);
        }
        if (Algorithms[3]) {
            jlEigenFitStatus.setText("In wachtrij");
            EigenAlgoritme = new EigenAlgoritme();
            Algoritmes.addAlgoritme(EigenAlgoritme);
        }
        if (Algorithms[4]) {
            jlBruteforceStatus.setText("In wachtrij");
            BruteForceAlgoritme = new Bruteforce(ArrayProducts, BoxSize);
            Algoritmes.addAlgoritme(BruteForceAlgoritme);
        }
        jbCancel.addActionListener(this);
        jbSave.addActionListener(this);
        jbContinue.addActionListener(this);
        vol = getVolume();
        jlTotalVolumeProducts.setText(Integer.toString(vol));
        setVisible(true);
        StartThread();
        endResult.append("Naam Algoritme");
        endResult.append(';');
        endResult.append("Aantal dozen");
        endResult.append(';');
        endResult.append("Tijd (ms)");
        endResult.append('\n');
        endResult.append('\n');
    }

    private void StartThread() {
        if (t == null) {
            t = new Thread(this, "StartSimulatie");
            t.start();
        }
    }

    // Save results with folder location chooser
    private void SaveResults() {
        PrintWriter pwFileWriter = null;
        JFileChooser jfChooser;
        jfChooser = new JFileChooser();
        jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfChooser.setDialogTitle("Selecteer map");
        if (jfChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String locatie = jfChooser.getCurrentDirectory().getAbsolutePath() + "\\" + jfChooser.getSelectedFile().getName();
            try {
                pwFileWriter = new PrintWriter(new File(locatie + "\\Resultaat.csv"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Er ging iets mis tijdens het opslaan", "Foutmelding",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            pwFileWriter.write(endResult.toString());
            pwFileWriter.close();
            if (BruteForceResult != null) {
                BruteForceResult.SaveScreen(locatie + "\\Bruteforce.png");
            }
            if (NextFitResult != null) {
                NextFitResult.SaveScreen(locatie + "\\Nextfit.png");
            }
            if (BestFitResult != null) {
                BestFitResult.SaveScreen(locatie + "\\Bestfit.png");
            }
            if (FirstFitResult != null) {
                FirstFitResult.SaveScreen(locatie + "\\Firstfit.png");
            }
            if (EigenFitResult != null) {
                EigenFitResult.SaveScreen(locatie + "\\EigenFit.png");
            }
            try {
                Desktop.getDesktop().open(new File(locatie));
            } catch (IOException ex) {

            }
        }
    }

    // Append the results for the CSV writer
    private void AppendResultaat(String naam, int grootte, long tijdsduur) {
        endResult.append(naam);
        endResult.append(';');
        endResult.append(grootte);
        endResult.append(';');
        endResult.append(tijdsduur);
        endResult.append('\n');
    }

    // Append the results for the CSV writer
    private void AppendResultaat(String tekst, String naam, String result) {
        endResult.append(tekst);
        endResult.append(';');
        endResult.append(naam);
        endResult.append(';');
        endResult.append(result);
        endResult.append('\n');
    }

    // Function to make a hyperlink of a label
    private void MakeHyperlink(javax.swing.JLabel label) {
        label.setText("Bekijk resultaat");
        label.setForeground(Color.blue);
        Font font = label.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        label.setFont(font.deriveFont(attributes));
        label.addMouseListener(this);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Function to start simulation
    private void StartSimulation() {
        ArrayList<Bin> bins = new ArrayList<>();
        long nu, tijdsduur;
        for (Algoritme Algoritme1 : Algoritmes.getAlgoritmes()) {
            nu = Instant.now().toEpochMilli();
            jpProgress.setIndeterminate(true);
            if (Algoritme1 instanceof Nextfit) {
                Algoritme1.setName("Nextfit");
                jlNextFitStatus.setText("Uitvoeren...");
                jlCurrentSimulation.setText(Algoritme1.getName());
                bins = (NextFitAlgoritme.start(ArrayProducts, BoxSize));
                jlNextFitStatus.setText("Voltooid");
                Algoritme1.setBins(bins.size());
                ArrayResults.add(NextFitResult = new Resultaat(bins, Algoritme1, vol, BoxSize));
                MakeHyperlink(jlNextFitStatus);
            }
            if (Algoritme1 instanceof Firstfit) {
                Algoritme1.setName("Firstfit");
                jlFirstFitStatus.setText("Uitvoeren...");
                jlCurrentSimulation.setText(Algoritme1.getName());
                bins = (FirstFitAlgoritme.start(ArrayProducts, BoxSize));
                jlFirstFitStatus.setText("Voltooid");
                Algoritme1.setBins(bins.size());
                ArrayResults.add(FirstFitResult = new Resultaat(bins, Algoritme1, vol, BoxSize));
                MakeHyperlink(jlFirstFitStatus);
            }
            if (Algoritme1 instanceof Bestfit) {
                Algoritme1.setName("Bestfit");
                jlBestFitStatus.setText("Uitvoeren...");
                jlCurrentSimulation.setText(Algoritme1.getName());
                bins = (BestFitAlgoritme.start(ArrayProducts, BoxSize));
                jlBestFitStatus.setText("Voltooid");
                Algoritme1.setBins(bins.size());
                ArrayResults.add(BestFitResult = new Resultaat(bins, Algoritme1, vol, BoxSize));
                MakeHyperlink(jlBestFitStatus);
            }
            if (Algoritme1 instanceof EigenAlgoritme) {
                Algoritme1.setName("Eigenfit");
                nu = Instant.now().toEpochMilli();
                jlEigenFitStatus.setText("Uitvoeren...");
                jlCurrentSimulation.setText(Algoritme1.getName());
                bins = (EigenAlgoritme.start(ArrayProducts, BoxSize));
                jlEigenFitStatus.setText("Voltooid");
                Algoritme1.setBins(bins.size());
                ArrayResults.add(EigenFitResult = new Resultaat(bins, Algoritme1, vol, BoxSize));
                MakeHyperlink(jlEigenFitStatus);
            }
            if (Algoritme1 instanceof Bruteforce) {
                Algoritme1.setName("Bruteforce");
                nu = Instant.now().toEpochMilli();
                jlBruteforceStatus.setText("Uitvoeren...");
                jlCurrentSimulation.setText(Algoritme1.getName());
                bins = (BruteForceAlgoritme.start());
                jlBruteforceStatus.setText("Voltooid");
                MakeHyperlink(jlBruteforceStatus);
                Algoritme1.setBins(bins.size());
                ArrayResults.add(BruteForceResult = new Resultaat(bins, Algoritme1, vol, BoxSize));
            }
            tijdsduur = Instant.now().toEpochMilli() - nu;
            AppendResultaat(Algoritme1.getName(), bins.size(), tijdsduur);
            Algoritme1.setEndTime(tijdsduur);
        }
        jpProgress.setIndeterminate(false);
        jpProgress.setValue(100);
        setTitle("Bin Packing Problem Simulation - Voltooid");
        jlCurrentSimulation.setText("Voltooid");
        jbCancel.setEnabled(false);
        jbSave.setEnabled(true);
        jbContinue.setEnabled(true);
        CalculateAlgorithms();
    }

    // Get total volume of products
    private int getVolume() {
        int v = 0;
        v = ArrayProducts.stream().map((product) -> product.getLength()).reduce(v, Integer::sum);
        return v;
    }

    // Function to calculate algorithms
    private void CalculateAlgorithms() {
        long fastestTime = -1, eft = -1;
        int bins = -1, fbins = -1;
        String fastestAlg = "", EfficientAlg = "";
        for (Resultaat result : ArrayResults) {
            if (fastestTime == -1 && bins == -1) {
                fastestTime = result.getAlgoritme().getEndTime();
                bins = result.getBins().size();
            }
            if (fastestTime >= result.getAlgoritme().getEndTime()) {
                fastestTime = result.getAlgoritme().getEndTime();
                fastestAlg = result.getAlgoritme().getName();
                fbins = result.getAlgoritme().getBins();
            }
            if (bins >= result.getBins().size()) {
                bins = result.getBins().size();
                EfficientAlg = result.getAlgoritme().getName();
                eft = result.getAlgoritme().getEndTime();
            }
        }
        jlFastestAlgorithm.setText(fastestAlg + " (" + fastestTime + "ms)");
        endResult.append("\nSnelste algoritme:\n");
        AppendResultaat(fastestAlg, Integer.toString(fbins), "Tijd: " + Long.toString(fastestTime) + "ms");
        endResult.append("\nEfficientste algoritme:\n");
        jlEfficientAlgorithm.setText(EfficientAlg + " (" + bins + " dozen)");
        AppendResultaat(EfficientAlg, bins, eft);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jpProgress = new javax.swing.JProgressBar();
        jbCancel = new javax.swing.JButton();
        jpPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jlFastestAlgorithm = new javax.swing.JLabel();
        jlEfficientAlgorithm = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jlTotalVolumeProducts = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlBruteforceStatus = new javax.swing.JLabel();
        jlNextFitStatus = new javax.swing.JLabel();
        jlFirstFitStatus = new javax.swing.JLabel();
        jlBestFitStatus = new javax.swing.JLabel();
        jlCurrentSimulation = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlEigenFitStatus = new javax.swing.JLabel();
        jbSave = new javax.swing.JButton();
        jbContinue = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bin Packing Problem Simulation - Bezig");
        setResizable(false);

        jLabel5.setText("Brute force");

        jLabel1.setText("Next fit");

        jLabel2.setText("First fit");

        jLabel3.setText("Best fit");

        jpProgress.setForeground(new java.awt.Color(0, 204, 0));
        jpProgress.setValue(50);

        jbCancel.setText("Annuleren");

        jpPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Resultaten:");

        jLabel8.setText("Snelste algoritme:");

        jLabel9.setText("Efficiëntste algoritme:");

        jlFastestAlgorithm.setText("Wordt berekend...");

        jlEfficientAlgorithm.setText("Wordt berekend...");

        jLabel10.setText("Totaal volume producten:");

        jlTotalVolumeProducts.setText("0");

        javax.swing.GroupLayout jpPanelLayout = new javax.swing.GroupLayout(jpPanel);
        jpPanel.setLayout(jpPanelLayout);
        jpPanelLayout.setHorizontalGroup(
            jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlEfficientAlgorithm, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .addComponent(jlFastestAlgorithm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlTotalVolumeProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpPanelLayout.setVerticalGroup(
            jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jlFastestAlgorithm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jlEfficientAlgorithm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jlTotalVolumeProducts))
                .addContainerGap(186, Short.MAX_VALUE))
        );

        jLabel6.setText("Simulatie met algoritme: ");

        jlBruteforceStatus.setText("Wordt niet uitgevoerd");

        jlNextFitStatus.setText("Wordt niet uitgevoerd");

        jlFirstFitStatus.setText("Wordt niet uitgevoerd");

        jlBestFitStatus.setText("Wordt niet uitgevoerd");

        jlCurrentSimulation.setText("x");

        jLabel4.setText("Eigen fit");

        jlEigenFitStatus.setText("Wordt niet uitgevoerd");

        jbSave.setText("Resultaten opslaan");
        jbSave.setEnabled(false);

        jbContinue.setText("Doorgaan");
        jbContinue.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jpProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbContinue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlEigenFitStatus)
                                    .addComponent(jlBruteforceStatus)
                                    .addComponent(jlNextFitStatus)
                                    .addComponent(jlFirstFitStatus)
                                    .addComponent(jlBestFitStatus)))
                            .addComponent(jbSave, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlCurrentSimulation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jlCurrentSimulation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jlNextFitStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jlFirstFitStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jlBestFitStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jlEigenFitStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jlBruteforceStatus))
                        .addGap(153, 153, 153)
                        .addComponent(jbSave))
                    .addComponent(jpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbCancel)
                        .addComponent(jbContinue))
                    .addComponent(jpProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbContinue;
    private javax.swing.JButton jbSave;
    private javax.swing.JLabel jlBestFitStatus;
    private javax.swing.JLabel jlBruteforceStatus;
    private javax.swing.JLabel jlCurrentSimulation;
    private javax.swing.JLabel jlEfficientAlgorithm;
    private javax.swing.JLabel jlEigenFitStatus;
    private javax.swing.JLabel jlFastestAlgorithm;
    private javax.swing.JLabel jlFirstFitStatus;
    private javax.swing.JLabel jlNextFitStatus;
    private javax.swing.JLabel jlTotalVolumeProducts;
    private javax.swing.JPanel jpPanel;
    private javax.swing.JProgressBar jpProgress;
    // End of variables declaration//GEN-END:variables

    // Required overrides
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == jlBruteforceStatus) {
            BruteForceResult.setVisible(true);
        } else if (e.getSource() == jlBestFitStatus) {
            BestFitResult.setVisible(true);
        } else if (e.getSource() == jlNextFitStatus) {
            NextFitResult.setVisible(true);
        } else if (e.getSource() == jlEigenFitStatus) {
            EigenFitResult.setVisible(true);
        } else if (e.getSource() == jlFirstFitStatus) {
            FirstFitResult.setVisible(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void run() {
        StartSimulation();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbCancel) {
            t.stop();
            jpProgress.setForeground(Color.red);
            jpProgress.setValue(100);
            jpProgress.setIndeterminate(false);
            jlCurrentSimulation.setText("Geannuleerd");
            jbSave.setEnabled(true);
            jbContinue.setEnabled(true);
            jbCancel.setEnabled(false);
            setTitle("Bin Packing Problem Simulation - Geannuleerd");
        } else if (e.getSource() == jbSave) {
            SaveResults();
        } else if (e.getSource() == jbContinue) {
            this.setVisible(false);
            ArrayResults.removeAll(ArrayResults);
            BruteForceResult = null;
            NextFitResult = null;
            BestFitResult = null;
            EigenFitResult = null;
            FirstFitResult = null;
            ArrayProducts = null;
            endResult = null;
            selectieScherm.setVisible(true);
            dispose();
        }
    }
}
