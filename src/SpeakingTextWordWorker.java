
import java.awt.Color;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.synthesis.Synthesizer;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shubham
 */
public class SpeakingTextWordWorker extends SwingWorker<Integer, String> {
    private PlayerModelImpl playerModelImpl;
    private String text;
    JTextArea jTextArea1;
    DefaultHighlighter.DefaultHighlightPainter myHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
    public SpeakingTextWordWorker(PlayerModelImpl playerModelImpl, JTextArea jTextArea1)
    {
        this.playerModelImpl = playerModelImpl;
        this.jTextArea1 =  jTextArea1;
        text = jTextArea1.getSelectedText();
    }
    @Override
    protected Integer doInBackground() throws BadLocationException
    {
        BreakIterator bi = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        bi.setText(text);
        int start=0;
        int end = 0;
        Highlighter hilite = jTextArea1.getHighlighter();
        int oldstart=0;
        while ((end = bi.next()) != BreakIterator.DONE) 
        {
            if(!playerModelImpl.isStopped())
            {
            System.out.println("start and end before speaking : " +start + "  " +end );
            try 
            {
                hilite.removeAllHighlights();
                hilite.addHighlight(jTextArea1.getSelectionStart()+oldstart,jTextArea1.getSelectionStart()+start, myHighlightPainter); 
                System.out.println("Highlighted: oldstart :" + oldstart + " start : "+ start);
            } catch (BadLocationException ex) 
            {
                Logger.getLogger(SpeakingTextWordWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            try 
            {
                playerModelImpl.speak(text.substring(start, end));
            } catch (InterruptedException ex) 
            {
             Logger.getLogger(SpeakingTextWordWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("start and end after speaking: " +start + "  " + end);
            oldstart = start;
            start = end;
            } else
            {
                System.out.println("breaked from while");
                break;
            }
            System.out.println("looping inside while");
        }
        hilite.removeAllHighlights();
        hilite.addHighlight(jTextArea1.getSelectionStart()+oldstart,jTextArea1.getSelectionStart()+start, myHighlightPainter);
        System.out.println("Last Highlisght start : " + oldstart + " end : " + start );
        System.out.println("You have reached here" );
        return null;
    }
    @Override
    protected void process(final List<String> chucks)
    {
        System.out.println("in the process() function");
        
    }
}
