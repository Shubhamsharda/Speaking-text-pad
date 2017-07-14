package texttospeech;

import javax.swing.*;
import javax.swing.SwingWorker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Iterable;
import java.util.Collections;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import com.itextpdf.text.*;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import static com.sun.org.apache.regexp.internal.RETest.test;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.NumericShaper.Range;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker.StateValue;
import static javax.swing.SwingWorker.StateValue.DONE;
import texttospeech.TextToSpeech;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import static jdk.nashorn.internal.objects.NativeRegExp.test;
import org.apache.log4j.BasicConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shubham
 */
public class Front extends javax.swing.JFrame {
    PlayerModelImpl playerModelImpl;
    private int initialVolume = 10;
    public int lastIndex;
    FontChooser fontDialog;
    JDialog foregroundDialog=null;
    JColorChooser fcolorChooser=null;
    JColorChooser bcolorChooser=null;
    JDialog backgroundDialog=null;
    SearchForWordWorker searchWorker;
    String wnhome = "C:\\Program Files (x86)\\WordNet\\2.1";
   

    /**
     * Creates new form Front
     */
    
    Synthesizer synthesizer=null;
    public Front() {
        initComponents();
        //initSE();
        jProgressBar2.setMinimum(0);
        jMenuItem3.setEnabled(false);
        jProgressBar2.setMaximum(100);
        jProgressBar2.setVisible(false);
        jButton8.setVisible(false);
        playerModelImpl = new PlayerModelImpl();
        jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        playerModelImpl.createSynthesizers(); 
        synthesizerComboBox.setModel((ComboBoxModel)playerModelImpl.getSynthesizerList());
        voiceComboBox.setModel((ComboBoxModel)playerModelImpl.getVoiceList());
        //synthesizerComboBox = playerModelImpl.createComboBox((ComboBoxModel)playerModelImpl.getSynthesizerList(), "Synthesizer", "FreeTTS Synthesizer");
        //voiceComboBox = playerModelImpl.createComboBox((ComboBoxModel) playerModelImpl.getVoiceList(),"Voice","Voice");
        playerModelImpl.setVolume(initialVolume);
        int speakingRate = (int) playerModelImpl.getSpeakingRate();
        if (speakingRate == -1) {
	    speakingRate = 0;
	}
        
        

    }
   /* class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
    {
        public MyHighlightPainter(Color color)
        {
            super(color);
        }   
        
    }
    
    Highlighter.HighlightPainter myHighlightPainter =  new MyHighlightPainter(Color.red);
    */
    
       /* public void removeHighlights(JTextArea textComp)
        {
            Highlighter hilite = textComp.getHighlighter();
            //Highlighter.Highlight[] hilites = hilite.getHighlights();
            for(int i=0;i<hilites.length; i++){
               if(hilites[i].getPainter() instanceof MyHighlightPainter )
               {
                   hilite.removeHighlight(hilites[i]);
               } 
            }
            hilite.removeAllHighlights();
        }*/
        
        
  /*public void initSE()
  {
      String voiceName ="kevin16";    
    try    
    {    
        SynthesizerModeDesc desc = new SynthesizerModeDesc(null,"general",  Locale.US,null,null);    
        synthesizer =  Central.createSynthesizer(desc);    
        synthesizer.allocate();    
        synthesizer.resume();     
        desc = (SynthesizerModeDesc)  synthesizer.getEngineModeDesc();     
        Voice[] voices = desc.getVoices();      
        Voice voice = null;
        for (int i = 0; i < voices.length; i++)    
        {    
            if (voices[i].getName().equals(voiceName))    
            {    
                voice = voices[i];    
                break;     
            }     
        }   
        System.out.println("after selecting voice");
        synthesizer.getSynthesizerProperties().setVoice(voice);    
        //System.out.print("Speaking : "+speaktext);    
        //synthesizer.speakPlainText(speaktext, null);   
        
        
        //synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);    
        //synthesizer.deallocate();    
    }    
    catch (Exception e)   
    {    
        String message = " missing speech.properties in " + System.getProperty("user.home") + "\n";    
        System.out.println(""+e);    
        System.out.println(message);    
    }    
  }
    */
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jOptionPane1 = new javax.swing.JOptionPane();
        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        FindDialog = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        replaceLabel = new javax.swing.JLabel();
        findWhat = new javax.swing.JTextField();
        replaceWith = new javax.swing.JTextField();
        findNextButton = new javax.swing.JButton();
        replaceButton = new javax.swing.JButton();
        replaceAllButton = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        matchCase = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        up = new javax.swing.JRadioButton();
        down = new javax.swing.JRadioButton();
        jFileChooser2 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        volumeSlider = new javax.swing.JSlider();
        speedSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pitchSlider = new javax.swing.JSlider();
        rangeSlider = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        synthesizerComboBox = new javax.swing.JComboBox<>();
        voiceComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        statusBar = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        formatBackground = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();

        jDialog1.setMinimumSize(new java.awt.Dimension(200, 100));
        jDialog1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                jDialog1ComponentHidden(evt);
            }
        });

        jLabel3.setText("Please wait....");
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jProgressBar1.setMinimumSize(new java.awt.Dimension(284, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel5.setMinimumSize(new java.awt.Dimension(200, 200));

        jLabel8.setText("Find What");

        replaceLabel.setText("Replace With");

        findWhat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                findWhatFocusLost(evt);
            }
        });
        findWhat.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                findWhatInputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        findWhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findWhatActionPerformed(evt);
            }
        });

        findNextButton.setText("Find Next");
        findNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findNextButtonActionPerformed(evt);
            }
        });

        replaceButton.setText("Replace");
        replaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceButtonActionPerformed(evt);
            }
        });

        replaceAllButton.setText("Replace All");
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllButtonActionPerformed(evt);
            }
        });

        jButton11.setText("Cancel");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        matchCase.setText("Match case");

        jLabel10.setText("Direction:");

        up.setText("up");

        down.setText("down");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(up)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(down)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(up)
                    .addComponent(down))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(replaceLabel)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(findWhat, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                            .addComponent(replaceWith))
                        .addGap(40, 40, 40))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(matchCase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(replaceAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addComponent(replaceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(findNextButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(45, 45, 45))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(findWhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findNextButton))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(replaceLabel)
                            .addComponent(replaceWith, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(replaceButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(replaceAllButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11)
                    .addComponent(matchCase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout FindDialogLayout = new javax.swing.GroupLayout(FindDialog.getContentPane());
        FindDialog.getContentPane().setLayout(FindDialogLayout);
        FindDialogLayout.setHorizontalGroup(
            FindDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        FindDialogLayout.setVerticalGroup(
            FindDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FindDialogLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(0, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Text to Speech Player");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextArea1CaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setMnemonic('p');
        jButton1.setText("Play");
        jButton1.setToolTipText("Systhesize speech and play it for text in text area.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setMnemonic('a');
        jButton2.setText("pause");
        jButton2.setToolTipText("Pause speech");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setMnemonic('r');
        jButton3.setText("Resume");
        jButton3.setToolTipText("Resume speech after pausing..");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Stop");
        jButton4.setToolTipText("Stop speech synthesis");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Find meaning");
        jButton5.setToolTipText("Use WordNet dictionary for the selected text in text area.");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Search");
        jButton6.setToolTipText("Search for words or phrase in the text area.");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextField1.setToolTipText("Enter word or phrase for searching");

        volumeSlider.setMajorTickSpacing(5);
        volumeSlider.setMaximum(10);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setToolTipText("Set Volume");
        volumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeSliderStateChanged(evt);
            }
        });

        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMaximum(400);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        speedSlider.setPaintLabels(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setToolTipText("words/min");
        speedSlider.setValue(0);
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speedSliderStateChanged(evt);
            }
        });

        jLabel1.setText("Volume");

        jLabel2.setText("Speed");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18))
        );

        pitchSlider.setMajorTickSpacing(50);
        pitchSlider.setMaximum(200);
        pitchSlider.setMinimum(50);
        pitchSlider.setMinorTickSpacing(25);
        pitchSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        pitchSlider.setPaintLabels(true);
        pitchSlider.setPaintTicks(true);
        pitchSlider.setToolTipText("Pitch/Hz");
        pitchSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                pitchSliderStateChanged(evt);
            }
        });

        rangeSlider.setMajorTickSpacing(10);
        rangeSlider.setMaximum(50);
        rangeSlider.setMinorTickSpacing(5);
        rangeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        rangeSlider.setPaintLabels(true);
        rangeSlider.setPaintTicks(true);
        rangeSlider.setToolTipText("Range Control");
        rangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rangeSliderStateChanged(evt);
            }
        });

        jLabel4.setText("Pitch");

        jLabel5.setText("Range");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pitchSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rangeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pitchSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rangeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(19, 19, 19))
        );

        jButton7.setText("Cancel");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        synthesizerComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        synthesizerComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                synthesizerComboBoxActionPerformed(evt);
            }
        });

        voiceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        voiceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voiceComboBoxActionPerformed(evt);
            }
        });

        jLabel6.setText("Synthesizer:");

        jLabel7.setText("voice:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(voiceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(synthesizerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(synthesizerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(voiceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71))
        );

        statusBar.setText("||       Ln 0, Col 0");

        jButton8.setText("Cancel ");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("New");
        jMenuItem1.setToolTipText("create new text file");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Open");
        jMenuItem2.setToolTipText("Open files from your directory");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenu3.setText("Save As");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Text File");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        jRadioButtonMenuItem2.setSelected(true);
        jRadioButtonMenuItem2.setText("Pdf File");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem2);

        jRadioButtonMenuItem3.setSelected(true);
        jRadioButtonMenuItem3.setText("Doc File");
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem3);

        jRadioButtonMenuItem4.setSelected(true);
        jRadioButtonMenuItem4.setText("Docx File");
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem4);

        jMenu1.add(jMenu3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Exit");
        jMenuItem4.setToolTipText("exit this application");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Undo");
        jMenu2.add(jMenuItem3);
        jMenu2.add(jSeparator1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Cut");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Copy");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText("Paste");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMenuItem8.setText("Delete");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);
        jMenu2.add(jSeparator2);

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setText("Find");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem10.setText("FindNext");
        jMenu2.add(jMenuItem10);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setText("Replace");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuItem12.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem12.setText("Go to..");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);
        jMenu2.add(jSeparator3);

        jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem13.setText("Select All");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem14.setText("Time/Date");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem14);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Format");

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("Word Wrap");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jCheckBoxMenuItem1);

        jMenuItem15.setText("Font...");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem15);
        jMenu4.add(jSeparator4);

        formatBackground.setText("Set Text color");
        formatBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatBackgroundActionPerformed(evt);
            }
        });
        jMenu4.add(formatBackground);

        jMenuItem17.setText("Set Pad color");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem17);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Dictionary");
        jMenu5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu5ActionPerformed(evt);
            }
        });

        jMenuItem16.setText("Set up dictionary");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem16);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(statusBar))
                    .addComponent(jScrollPane1))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton5)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton7)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(statusBar)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
TextToSpeech obj=new TextToSpeech();
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Highlighter hilite = jTextArea1.getHighlighter();
        DefaultHighlighter.DefaultHighlightPainter myHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
        if(!(jTextArea1.getSelectedText() == null))
        {
             //String[] sentences = jTextArea1.getSelectedText().split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s");
            /*String[] sentences = jTextArea1.getSelectedText().split("\\.");
             for(String s : sentences)
            {
                playerModelImpl.speak(s);
            }*/
            //playerModelImpl.speak(jTextArea1.getSelectedText());
            System.out.println("isStopped() :  " + playerModelImpl.isStopped());
            System.out.println("Outputting selected text");
            playerModelImpl.setStopped(false);
            //DefaultHighlighter.DefaultHighlightPainter myHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
           // BreakIterator bi = BreakIterator.getSentenceInstance(Locale.ENGLISH);
            //bi.setText(jTextArea1.getSelectedText());
           // int start=0;
            //int end = 0;
            SpeakingTextWordWorker speakingTextWordWorker = new SpeakingTextWordWorker(playerModelImpl, jTextArea1,true);
            
            //while ((end = bi.next()) != BreakIterator.DONE) 
            //{
                //hilite.removeAllHighlights();
                /* try
                {
                //hilite.removeAllHighlights();
                hilite.addHighlight(jTextArea1.getSelectionStart()+start,jTextArea1.getSelectionStart()+end, myHighlightPainter);
                } catch (BadLocationException ex) 
                {
                Logger.getLogger(Front.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                //System.out.println("start before speaking: " +start);
                //System.out.println("end before speaking : " +end);
                //speakingTextWordWorker.setText(jTextArea1.getSelectedText().substring(start, end));
                    /*speakingTextWordWorker.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
              public void propertyChange(final PropertyChangeEvent event) {
               switch (event.getPropertyName()) {
              case "state":
                  switch ((StateValue) event.getNewValue()) {
                    case DONE:
                              hilite.removeAllHighlights();
                              int beginIndex=jTextArea1.getSelectedText().lastIndexOf('.');
                              int lastIndex = jTextArea1.getSelectedText().length();
                              System.out.println("BeginIndex: " + beginIndex+ " lastIndex : "+lastIndex );

                              if(jTextArea1.getSelectedText().endsWith(".") || jTextArea1.getSelectedText().endsWith(". ")||jTextArea1.getSelectedText().endsWith("\n")||jTextArea1.getSelectedText().endsWith(" "))
                              {
                                System.out.println("inside if");
                                String sub = jTextArea1.getSelectedText().substring(0,jTextArea1.getSelectedText().length()-4);
                                int newBeginIndex = sub.lastIndexOf('.');
                                int newEndIndex = sub.length();
                                System.out.println("neBeginIndex: " + newBeginIndex+ " newEndIndex : "+newEndIndex );
                                try {
                                      hilite.addHighlight(newBeginIndex+1, newEndIndex,myHighlightPainter );
                                } catch (BadLocationException ex) {
                                      Logger.getLogger(Front.class.getName()).log(Level.SEVERE, null, ex);
                                }
                             }
                              else
                             {
                                System.out.println("Inside else");
                   try {
                       hilite.addHighlight(beginIndex+4, lastIndex,myHighlightPainter );
                   } catch (BadLocationException ex) {
                       Logger.getLogger(Front.class.getName()).log(Level.SEVERE, null, ex);
                   }
                             }
                     break;
                    }
                break;
              }
      }
    });*/
                speakingTextWordWorker.execute();
                System.out.println("Created and executed speakingtext swingworker");
 
        }else
        {
            //String[] sentences = jTextArea1.getText().split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)"); //regex to split paras into sentences.
            //String[] sentences = jTextArea1.getText().split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)(\\s|[A-Z].*)");
            /*hilite.removeAllHighlights();
            String[] sentences = jTextArea1.getText().split("\\.");
            System.out.println("Text not selected, playing whole text");
            for(String s : sentences)
            {
                
                try {
                    playerModelImpl.speak(s);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Front.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
            System.out.println("isStopped() :  " + playerModelImpl.isStopped());
            System.out.println("Outputting all text");
            playerModelImpl.setStopped(false);
            SpeakingTextWordWorker speakingTextWordWorker = new SpeakingTextWordWorker(playerModelImpl, jTextArea1,false);
            speakingTextWordWorker.execute();
            System.out.println("Created and executed speakingtext swingworker");
            
        }
        
         
          //obj.speak(jTextArea1.getText(), synthesizer);
          
    }//GEN-LAST:event_jButton1ActionPerformed
   int pause = 0;
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
       //obj.Pause(synthesizer);
       playerModelImpl.pause();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
       // obj.Resume(synthesizer);
       playerModelImpl.resume();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        playerModelImpl.stop();
        Highlighter hilite = jTextArea1.getHighlighter();
        hilite.removeAllHighlights();
        //obj.Stop(synthesizer);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        
		try
		{
			if(synthesizer != null)
				synthesizer.deallocate();
			System.out.println("Speech engine shutdown."); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("ERROR! closing speech synthesis engine." + e); 
		
		}
                System.exit(0);
	
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        jTextArea1.setText("");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        //Front.this.repaint();
        FileFilter ft = new FileNameExtensionFilter("Text Files", "txt");
        FileFilter ft2 = new FileNameExtensionFilter("PDF Files", "pdf");
        jFileChooser1.addChoosableFileFilter(ft);
        jFileChooser1.addChoosableFileFilter(ft2);
     
        if(jFileChooser1.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File fSelected = jFileChooser1.getSelectedFile();
            String filename = fSelected.getName();                              //Trying to get the type of file.
            int dotIndex = filename.lastIndexOf('.');
            String type = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
            if(type.equals("txt"))
            {    
            try
            {
                FileReader in = new FileReader(fSelected);
                jTextArea1.read(in,null);
                in.close();
            } catch(IOException ioe)
            {
                System.out.println("Error in reading file "+ioe);
            }
            }
            else if(type.equals("pdf"))
            {
                try{ 
                PDDocument document = PDDocument.load(fSelected);
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                 jTextArea1.setText(text);
                 document.close();
                }catch(Exception e)
                {
                    System.out.println("pdf not opening "+e);
                }
            }
            else if(type.equals("doc"))
            {
                try{
                 FileInputStream fis = new FileInputStream(fSelected);
                 HWPFDocument document = new HWPFDocument(fis);
                 WordExtractor extractor = new WordExtractor(document);
                 jTextArea1.setText(extractor.getText());
                 extractor.close();
                }catch(Exception e)
                {
                    
                    System.out.println("Error while opening doc/x file "+e);
                }
            }
            else if(type.equals("docx"))
            {
                try{
                FileInputStream fis = new FileInputStream(fSelected);
                 XWPFDocument document = new XWPFDocument(fis);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                 jTextArea1.setText(extractor.getText());
                 extractor.close();
                }catch(Exception e)
                {
                    System.out.println("Error during reading docx document "+e);
                }
            }
        }
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        // TODO add your handling code here:
          FileFilter ft = new FileNameExtensionFilter("Text Files", "txt");
        jFileChooser1.addChoosableFileFilter(ft);
        if(jFileChooser1.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File fSelected = jFileChooser1.getSelectedFile();
            try
            {
                FileWriter out = new FileWriter(fSelected);
                jTextArea1.write(out);
                out.close();
            } catch(IOException ioe)
            {
                System.out.println("Error in saving file "+ioe);
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
      
         if(jFileChooser1.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File fSelected = jFileChooser1.getSelectedFile();
            try
            {
                //Blank Document
                 XWPFDocument document= new XWPFDocument(); 
                //Write the Document in file system
                 FileOutputStream out = new FileOutputStream(fSelected);
        
                //create Paragraph
                 XWPFParagraph paragraph = document.createParagraph();
                 paragraph.setSpacingAfterLines(1);
                 //paragraph.setSpacingBetween(pause);
                 XWPFRun run=paragraph.createRun();
                 run.setText(jTextArea1.getText());
                 document.write(out);
                 out.close();
            } catch(IOException ioe)
            {
                System.out.println("Error in saving docx file "+ioe);
            }
            catch(Exception ioe)
            {
                System.out.println("Error in saving docx file "+ioe);
            }
            
        }
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
       
       if(jFileChooser1.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File fSelected = jFileChooser1.getSelectedFile();
            try
            {
                try{
                      PrintWriter writer = new PrintWriter("C:\\Users\\Shubham\\Desktop\\java project Docs\\tem.txt", "UTF-8");
                      writer.println(jTextArea1.getText());
                      writer.close();
                    } catch (IOException e) {
                              System.out.println("Error while creating temp file for doc saving "+e);
                    }
                File f= new File("C:\\Users\\Shubham\\Desktop\\java project Docs\\tem.doc");
                FileInputStream in = new FileInputStream(f);
                POIFSFileSystem fs = new POIFSFileSystem(in);
                HWPFDocument document= new HWPFDocument(fs);
                org.apache.poi.hwpf.usermodel.Range range = document.getRange();
                CharacterRun run = range.insertAfter(jTextArea1.getText());
                document.write(fSelected);
            } catch(IOException ioe)
            {
                System.out.println("Error in saving doc file "+ioe);
            }
        }
       
       
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser1.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File fSelected = jFileChooser1.getSelectedFile();
            try
            {
                /*StringBuilder b = new StringBuilder();                        //Trying to remove unsupported characters by using contains function
                String text = jTextArea1.getText();                           //of WinAnsiEncoding class and saving the modifid text in b.
                for (int i = 0; i < text.length(); i++) {
            if (WinAnsiEncoding.INSTANCE.contains(text.charAt(i))) {
                b.append(text.charAt(i));
                if(text.charAt(i) == '.')
                {
                    b.append('\n');
                }

            }
            }
               // System.out.println("value of b is: " + b.toString());
                String text = jTextArea1.getText();
                String[] s = new String[50];
                for(int j=0;j<50;j++)
                {
                for (int i = 0; i < text.length(); i++){
                    if(text.charAt(i) == '.')
                    {
                        
                    }
                 }
                }
                PDDocument doc = new PDDocument();
                PDPage page = new PDPage();
                doc.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(doc, page);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(15,670);
                contentStream.setRenderingMode(RenderingMode.FILL);
                contentStream.showText(b.toString()); 
                contentStream.endText();
                System.out.println("Content added");
                //Closing the content stream
                contentStream.close();
                doc.save(fSelected);
                doc.close();*/
             Document pdfDoc = new Document(PageSize.A4);
             PdfWriter.getInstance(pdfDoc, new FileOutputStream(fSelected)).setPdfVersion(PdfWriter.VERSION_1_7);
             pdfDoc.open();
             BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
             Font myfont = new Font(courier);
             myfont.setStyle(Font.NORMAL);
             myfont.setSize(11);
             pdfDoc.add(new Paragraph("\n"));
             String[] lines = jTextArea1.getText().split(System.getProperty("line.separator"));
             int size = lines.length;
             for(int i=0; i < size ; i++ )
             {
                 Paragraph para = new Paragraph(lines[i]+"\n", myfont);
                 para.setAlignment(Element.ALIGN_JUSTIFIED);
                 pdfDoc.add(para);
                 
             }
             pdfDoc.close();
            } catch(IOException ioe)
            {
                System.out.println("Error in pdf saving file "+ioe);
            }
            catch(Exception e)
            {
                System.out.println("Error while saving pdf file "+e);
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
      // construct the URL to the Wordnet dictionary directory
      try{
      //wnhome = "C:\\Program Files (x86)\\WordNet\\2.1";
      String path = wnhome + File.separator + "dict";
      URL url=null;
      try{
       url = new URL("file",null, path);
      }catch(MalformedURLException e)
      {
          System.out.println("Error wile creating url "+e);
      }
      // construct the dictionary object and open it
      IDictionary dict = new Dictionary ( url ) ;
        try {
            dict.open();
        } catch (IOException ex) {
            Logger.getLogger(Front.class.getName()).log(Level.SEVERE, null, ex);
        }
        String result="";
        System.out.println(jTextArea1.getSelectedText());
        if(!(jTextArea1.getSelectedText() == null)){
            System.out.println("Text is selected. Text not empty");   //for debugging purpose.
        try{
        WordnetStemmer stemmer = new WordnetStemmer(dict);
        java.util.List<String> stems = stemmer.findStems(jTextArea1.getSelectedText(),null);
        //dictionarypage pg = new dictionaryPage();
        
        //Set<IWord> Stemwords = new HashSet<>();
        
        for(int i=0;i< stems.size(); i++)
        {
           Set<String> lexicon = new HashSet<>();
           for(POS p : POS.values())
           {
               
                IIndexWord idxWord = dict.getIndexWord(stems.get(i),p);
                IWordID wordID = null;
                IWord word = null;
                if(idxWord != null)
                {
                   wordID = idxWord.getWordIDs().get(0);
                   word = dict.getWord(wordID);
                   String Glossary = word.getSynset().getGloss();
                   Glossary = Glossary.replaceAll("\\;", "\\;\n");
                   result = result +'\n'+ '\n'+"WORD : " + word.getLemma() + "( " + p.name() + " )" + '\n' + "GLOSSARY : " + Glossary + '\n';
                   //Stemwords.add(word);
                   ISynset synset = word.getSynset();
                   for(IWord w : synset.getWords())
                   {
                      lexicon.add(w.getLemma());
                   }
                   
          
                
           
           
                    result= result + "SYNONYMS of "+ word.getLemma() + "( " + p.name() + " ) are : ";
                    for (String s : lexicon) 
                    result = result + s + " - "; 
                }
            
           
           }//result = result + "Id = " + wordID +'\n' +"Word = " + word.getLemma() + '\n' + "Glossary = " + word.getSynset().getGloss() + '\n';
        }
        
       
        }catch(Exception e)
        {
            System.out.println("Error found while processing word in dictionary"+e);
            e.printStackTrace();
        }
        if(result.equals("")){ JOptionPane.showMessageDialog(this,"No such word found","Dictionary",JOptionPane.INFORMATION_MESSAGE); }
        else
        JOptionPane.showMessageDialog(this,result,"Dictionary",JOptionPane.INFORMATION_MESSAGE);
        } else
        {
            System.out.println("No selected text");
            JOptionPane.showMessageDialog(this,"No Word Selected \nSelect a word to find its meaning. \n Double clicka word for selection","Dictionary",JOptionPane.INFORMATION_MESSAGE);
        }
        
      }catch(Exception e)
      {
          System.out.println("Exception occured "+ e);
          e.printStackTrace();
          JOptionPane.showMessageDialog(this,"Dictionsry may not be properly set, Error","Dictionary",JOptionPane.ERROR_MESSAGE);
      }

    }//GEN-LAST:event_jButton5ActionPerformed
    
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
          
         
         if((jTextField1.getText() == null))
         {
           JOptionPane.showMessageDialog(this,"Enter words to search first!","Error",JOptionPane.ERROR_MESSAGE);
           Highlighter hilite = jTextArea1.getHighlighter();
           hilite.removeAllHighlights();
         }
         else if(jTextField1.getText().equals(""))
           {
               JOptionPane.showMessageDialog(this,"Enter words to search first!","Error",JOptionPane.ERROR_MESSAGE);
               Highlighter hilite = jTextArea1.getHighlighter();
               hilite.removeAllHighlights();
           }
         else{
             
         /* javax.swing.SwingWorker<Void,Void> mySwingWorker;
             mySwingWorker = new javax.swing.SwingWorker<Void , Void>() {
                 @Override
                 protected Void doInBackground() throws Exception {
                     highlight(jTextArea1,jTextField1.getText());
                     //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                     return null;
                 }
             };
          mySwingWorker.addPropertyChangeListener(new PropertyChangeListener() {

         @Override
         public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("state")) {
               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                  jDialog1.dispose();
               }
            }
         }
         });
          mySwingWorker.execute();
          jProgressBar1.setIndeterminate(true);
         // jProgressBar1
          jDialog1.setTitle("Processing");
          jDialog1.setLocation(200, 200);
          jDialog1.setVisible(true);
          
         
         //System.out.println("Inside if");
         //jDialog1.setVisible(false);
         }
         
         
           
         */
         jProgressBar2.setVisible(true);
         jButton8.setVisible(true);
         searchWorker = new SearchForWordWorker(jTextArea1,jTextField1.getText(),this.jButton6);
         searchWorker.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
              public void propertyChange(final PropertyChangeEvent event) {
               switch (event.getPropertyName()) {
              case "progress":
               jProgressBar2.setIndeterminate(false);
               jProgressBar2.setValue((Integer) event.getNewValue());
               break;
              case "state":
               switch ((StateValue) event.getNewValue()) {
                case DONE:
                jProgressBar2.setVisible(false);
                jButton8.setVisible(false);
                //jProgressBar2.putValue(Action.NAME, "Search");
                 try {
                 final int count = searchWorker.get();
                 JOptionPane.showMessageDialog(jButton6, "Found: " + count + " words", "Search Words",
                  JOptionPane.INFORMATION_MESSAGE);
            } catch (final CancellationException e) {
              JOptionPane.showMessageDialog(jButton6, "The search process was cancelled", "Search Words",
                  JOptionPane.WARNING_MESSAGE);
            } catch (final Exception e) {
              JOptionPane.showMessageDialog(jButton6, "The search process failed", "Search Words",
                  JOptionPane.ERROR_MESSAGE);
            }

            //searchWorker = null;
               
            break;
          case STARTED:
          case PENDING:
            //searchCancelAction.putValue(Action.NAME, "Cancel");
            jProgressBar2.setVisible(true);
            jProgressBar2.setIndeterminate(true);
            break;
          }
          break;
        }
      }
    });
    searchWorker.execute();
         }   
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jDialog1ComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jDialog1ComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_jDialog1ComponentHidden

    private void synthesizerComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_synthesizerComboBoxActionPerformed
      int selectedIndex = synthesizerComboBox.getSelectedIndex();
      playerModelImpl.setSynthesizer(selectedIndex);
    }//GEN-LAST:event_synthesizerComboBoxActionPerformed

    private void voiceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voiceComboBoxActionPerformed
      Cursor oldCursor = getCursor();
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      playerModelImpl.setVoice(voiceComboBox.getSelectedIndex());
      setCursor(oldCursor);
      updateSliders();

      
    }//GEN-LAST:event_voiceComboBoxActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        playerModelImpl.cancel();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void volumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumeSliderStateChanged
      if(playerModelImpl.setVolume((float) volumeSlider.getValue()) == false)
      {
          volumeSlider.setValue((int) playerModelImpl.getVolume());
          
      }  
    }//GEN-LAST:event_volumeSliderStateChanged

    private void speedSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_speedSliderStateChanged
       if(playerModelImpl.setSpeakingRate((float) speedSlider.getValue()) == false)
       {
           speedSlider.setValue((int) playerModelImpl.getSpeakingRate());
       }
    }//GEN-LAST:event_speedSliderStateChanged

    private void pitchSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_pitchSliderStateChanged
         if(playerModelImpl.setPitch((float) pitchSlider.getValue()) == false)
         {
             pitchSlider.setValue((int) playerModelImpl.getPitch());
         }
    }//GEN-LAST:event_pitchSliderStateChanged

    private void rangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rangeSliderStateChanged
       if(playerModelImpl.setRange((float) rangeSlider.getValue()) == false)
       {
           rangeSlider.setValue((int) playerModelImpl.getRange());
       }
    }//GEN-LAST:event_rangeSliderStateChanged

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jTextArea1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextArea1CaretUpdate
      {  
          int lineNumber=0, column=0, pos=0;  
          try  
          {  
               pos = jTextArea1.getCaretPosition();  
               lineNumber = jTextArea1.getLineOfOffset(pos);  
               column = pos - jTextArea1.getLineStartOffset(lineNumber);  
          }catch(Exception e){ System.out.println("Error Finding caret position" + e);}  
          if(jTextArea1.getText().length()==0){lineNumber=0; column=0;}  
          statusBar.setText("||       Ln "+(lineNumber+1)+", Col "+(column+1));  
       }
    }//GEN-LAST:event_jTextArea1CaretUpdate

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        jTextArea1.paste();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jTextArea1.cut();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        jTextArea1.copy();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        jTextArea1.replaceSelection("");
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void findWhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findWhatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_findWhatActionPerformed

    private void findNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findNextButtonActionPerformed
        // TODO add your handling code here:
        findNextWithSelection();
    }//GEN-LAST:event_findNextButtonActionPerformed

    private void findWhatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_findWhatFocusLost
       enableDisableButtons();
    }//GEN-LAST:event_findWhatFocusLost

    private void findWhatInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_findWhatInputMethodTextChanged
        // TODO add your handling code here:
        enableDisableButtons();
    }//GEN-LAST:event_findWhatInputMethodTextChanged

    private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceButtonActionPerformed
        // TODO add your handling code here:
        replaceNext();
    }//GEN-LAST:event_replaceButtonActionPerformed

    private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllButtonActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null,"Total replacements made= "+replaceAllNext());
    }//GEN-LAST:event_replaceAllButtonActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
    
        replaceButton.setVisible(false);
        replaceAllButton.setVisible(false);
        replaceWith.setVisible(false);
        replaceLabel.setVisible(false);

        FindDialog.setTitle("Find");
        FindDialog.setSize(385, 246);
        FindDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
       FindDialog.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
       replaceButton.setVisible(true);
       replaceAllButton.setVisible(true);
        replaceWith.setVisible(true);
       replaceLabel.setVisible(true);
       FindDialog.setTitle("Replace");
       FindDialog.setVisible(true);
       FindDialog.setSize(385, 246);

    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
      
        
     int lineNumber=0;
     try
     {
        lineNumber=jTextArea1.getLineOfOffset(jTextArea1.getCaretPosition())+1;
        String tempStr=JOptionPane.showInputDialog(this,"Enter Line Number:",""+lineNumber);
        if(tempStr==null)
	   {return;}
        lineNumber=Integer.parseInt(tempStr);
        jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(lineNumber-1));
     }catch(Exception e){ e.printStackTrace();}
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        
        jTextArea1.selectAll();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
       jTextArea1.insert(new Date().toString(),jTextArea1.getSelectionStart());
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        JCheckBoxMenuItem temp=(JCheckBoxMenuItem)evt.getSource();
        jTextArea1.setLineWrap(temp.isSelected());
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
     showBackgroundColorDialog();        
        
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void formatBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatBackgroundActionPerformed
        // TODO add your handling code here:
        showForegroundColorDialog();
    }//GEN-LAST:event_formatBackgroundActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
      if(fontDialog==null)
	fontDialog=new FontChooser(jTextArea1.getFont());

        if(fontDialog.showDialog(this,"Choose a font"))
	jTextArea1.setFont(fontDialog.createFont());
        
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        searchWorker.cancel(true);
        Highlighter hilite = jTextArea1.getHighlighter();
        hilite.removeAllHighlights();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
      if(jFileChooser2.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            File fSelected = jFileChooser2.getSelectedFile();
            String filename = fSelected.getAbsolutePath();  
            System.out.println(filename); 
            wnhome = filename;
            
        }
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenu5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu5ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jMenu5ActionPerformed
    /*public String ResultCalc(String txt)
    {
        
    }*/
    /**
     * @param args the command line arguments
     */
    private void updateSliders() {
	int volume = (int) playerModelImpl.getVolume();
	if (volume > -1) {
	    volumeSlider.setValue(volume);
	}
	int rate = (int) playerModelImpl.getSpeakingRate();
	if (rate > -1) {
	    speedSlider.setValue(rate);
	}
	int pitch = (int) playerModelImpl.getPitch();
	if (pitch > -1) {
	    pitchSlider.setValue(pitch);
	}
	int range = (int) playerModelImpl.getRange();
	if (range > -1) {
	    rangeSlider.setValue(range);
	}
    }
void enableDisableButtons()
{
if(findWhat.getText().length()==0)
{
findNextButton.setEnabled(false);
replaceButton.setEnabled(false);
replaceAllButton.setEnabled(false);
}
else
{
findNextButton.setEnabled(true);
replaceButton.setEnabled(true);
replaceAllButton.setEnabled(true);
}
}

int findNext()
{

String s1=jTextArea1.getText();
String s2=findWhat.getText();

lastIndex=jTextArea1.getCaretPosition();

int selStart=jTextArea1.getSelectionStart();
int selEnd=jTextArea1.getSelectionEnd();

if(up.isSelected())
{
if(selStart!=selEnd)
	lastIndex=selEnd-s2.length()-1;
/*****Notepad doesnt use the else part, but it should be, instead of using caretPosition.***
else
	lastIndex=lastIndex-s2.length();
******/

if(!matchCase.isSelected())
	lastIndex=s1.toUpperCase().lastIndexOf(s2.toUpperCase(),lastIndex);
else
	lastIndex=s1.lastIndexOf(s2,lastIndex);	
}
else
{
if(selStart!=selEnd)
	lastIndex=selStart+1;
if(!matchCase.isSelected())
	lastIndex=s1.toUpperCase().indexOf(s2.toUpperCase(),lastIndex);
else
	lastIndex=s1.indexOf(s2,lastIndex);	
}

return lastIndex;
}

public void findNextWithSelection()
{
int idx=findNext();
if(idx!=-1)
{
jTextArea1.setSelectionStart(idx);
jTextArea1.setSelectionEnd(idx+findWhat.getText().length());
}
else
	JOptionPane.showMessageDialog(this,
	"Cannot find"+" \""+findWhat.getText()+"\"",
	"Find",JOptionPane.INFORMATION_MESSAGE);
}

void replaceNext()
{
// if nothing is selectd
if(jTextArea1.getSelectionStart()==jTextArea1.getSelectionEnd()) 
	{findNextWithSelection();return;}

String searchText=findWhat.getText();
String temp=jTextArea1.getSelectedText();	//get selected text

//check if the selected text matches the search text then do replacement

if(
	(matchCase.isSelected() && temp.equals(searchText))
				||
	(!matchCase.isSelected() && temp.equalsIgnoreCase(searchText))
  )
	jTextArea1.replaceSelection(replaceWith.getText());

findNextWithSelection();
}

int replaceAllNext()
{
if(up.isSelected())
	jTextArea1.setCaretPosition(jTextArea1.getText().length()-1);
else
	jTextArea1.setCaretPosition(0);

int idx=0;
int counter=0;
do
{
idx=findNext();
if(idx==-1) break;
counter++;
jTextArea1.replaceRange(replaceWith.getText(),idx,idx+findWhat.getText().length());
}while(idx!=-1);

return counter;
}

void goTo()
{
int lineNumber=0;
try
{
lineNumber=jTextArea1.getLineOfOffset(jTextArea1.getCaretPosition())+1;
String tempStr=JOptionPane.showInputDialog(this,"Enter Line Number:",""+lineNumber);
if(tempStr==null)
	{return;}
lineNumber=Integer.parseInt(tempStr);
jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(lineNumber-1));
}catch(Exception e){}
}

void showForegroundColorDialog()
{
if(fcolorChooser==null)
	fcolorChooser=new JColorChooser();
if(foregroundDialog==null)
	foregroundDialog=JColorChooser.createDialog
		(this,
		formatBackground.getName(),
		false,
		fcolorChooser,
		new ActionListener()
		{public void actionPerformed(ActionEvent evvv){
			jTextArea1.setForeground(fcolorChooser.getColor());}},
		null);		

foregroundDialog.setVisible(true);
}


void showBackgroundColorDialog()
{
if(bcolorChooser==null)
	bcolorChooser=new JColorChooser();
if(backgroundDialog==null)
	backgroundDialog=JColorChooser.createDialog
		(this,
		formatBackground.getText(),
		false,
		bcolorChooser,
		new ActionListener()
		{public void actionPerformed(ActionEvent evvv){
			jTextArea1.setBackground(bcolorChooser.getColor());}},
		null);		

backgroundDialog.setVisible(true);
}


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        BasicConfigurator.configure();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Front.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Front().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog FindDialog;
    private javax.swing.JRadioButton down;
    private javax.swing.JButton findNextButton;
    private javax.swing.JTextField findWhat;
    private javax.swing.JMenuItem formatBackground;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JCheckBox matchCase;
    private javax.swing.JSlider pitchSlider;
    private javax.swing.JSlider rangeSlider;
    private javax.swing.JButton replaceAllButton;
    private javax.swing.JButton replaceButton;
    private javax.swing.JLabel replaceLabel;
    private javax.swing.JTextField replaceWith;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JLabel statusBar;
    private javax.swing.JComboBox<String> synthesizerComboBox;
    private javax.swing.JRadioButton up;
    private javax.swing.JComboBox<String> voiceComboBox;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables
}
