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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import texttospeech.TextToSpeech;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
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

    /**
     * Creates new form Front
     */
    Synthesizer synthesizer=null;
    public Front() {
        initComponents();
        initSE();
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
    DefaultHighlightPainter myHighlightPainter = new DefaultHighlightPainter(Color.red);
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
        public void highlight(JTextArea textComp, String pattern )
        {
            Highlighter hilite = textComp.getHighlighter();
            hilite.removeAllHighlights();
            //removeHighlights(textComp);
            try{
                //hilite = textComp.getHighlighter();
                javax.swing.text.Document doc = textComp.getDocument();
                String text = doc.getText(0,doc.getLength());
                int pos = 0;
                while((pos=text.toUpperCase().indexOf(pattern.toUpperCase(),pos))>=0)
                {
                    hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                    pos +=pattern.length();
                }
                if(hilite.getHighlights().length == 0)
                {
                    JOptionPane.showMessageDialog(this,"No such phrase found!","Nothing Found",JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(Exception e)
            {
                System.out.println("error in highlighting"+e);
                
            }
        }
  public void initSE()
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setMnemonic('p');
        jButton1.setText("Play");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setMnemonic('a');
        jButton2.setText("pause");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setMnemonic('r');
        jButton3.setText("Resume");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Stop");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Find meaning");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Search");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
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
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Open");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenu3.setText("jMenu3");

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

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Save As");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
TextToSpeech obj=new TextToSpeech();
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         
          obj.speak(jTextArea1.getText(), synthesizer);
        //obj.dospeak(jTextArea1.getText(),"kevin16");  
    }//GEN-LAST:event_jButton1ActionPerformed
   int pause = 0;
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
       obj.Pause(synthesizer);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        obj.Resume(synthesizer);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        obj.Stop(synthesizer);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
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
    }//GEN-LAST:event_jMenuItem3ActionPerformed

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
                 paragraph.setSpacingBetween(pause);
                 XWPFRun run=paragraph.createRun();
                 run.setText(jTextArea1.getText());
                 
			
                 document.write(out);
                 out.close();
            } catch(IOException ioe)
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
      String wnhome = "C:\\Program Files (x86)\\WordNet\\2.1";
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
        
      }catch(NullPointerException e)
      {
          System.out.println("Exception occured "+ e);
          e.printStackTrace();
      }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
         if((jTextField1.getText() == null))
         {
           JOptionPane.showMessageDialog(this,"Enter words to search first!","Error",JOptionPane.ERROR_MESSAGE);
           
         }
         else if(jTextField1.getText().equals(""))
           {
               JOptionPane.showMessageDialog(this,"Enter words to search first!","Error",JOptionPane.ERROR_MESSAGE);
               Highlighter hilite = jTextArea1.getHighlighter();
               hilite.removeAllHighlights();
           }
         else
         highlight(jTextArea1,jTextField1.getText());
         System.out.println("Inside if");
        
            
        
    }//GEN-LAST:event_jButton6ActionPerformed
    /*public String ResultCalc(String txt)
    {
        
    }*/
    /**
     * @param args the command line arguments
     */
 
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
