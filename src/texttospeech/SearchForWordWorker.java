package texttospeech;


import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shubham
 */
public class SearchForWordWorker extends SwingWorker<Integer, String>
        {
            private final JTextArea textArea;
            private final String searchString;
            private final JComponent component;
            private static void failIfInterrupted() throws InterruptedException { 
            if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while searching files");
                }
            }
            public SearchForWordWorker(JTextArea textArea, String textField,JComponent component)
            {
                this.textArea=textArea;
                this.searchString=textField;
                this.component = component;
            }
            protected Integer doInBackground() throws Exception
            {
                //highlight(textArea, searchString);
                int matches=0;
            Highlighter hilite = textArea.getHighlighter();
            hilite.removeAllHighlights();
            //removeHighlights(textComp);
            try{
                //hilite = textComp.getHighlighter();
                SearchForWordWorker.failIfInterrupted();
                javax.swing.text.Document doc = textArea.getDocument();
                String text = doc.getText(0,doc.getLength());
                int size= text.length();
                int pos = 0;
                for(matches=0;(pos=text.toUpperCase().indexOf(searchString.toUpperCase(),pos))>=0;matches++)
                {
                    SearchForWordWorker.failIfInterrupted();
                    hilite.addHighlight(pos, pos+searchString.length(), myHighlightPainter);
                    pos +=searchString.length();
                    setProgress((pos)*100 / size);
                }
                if(hilite.getHighlights().length == 0)
                {
                    JOptionPane.showMessageDialog(component,"No such phrase found!","Nothing Found",JOptionPane.ERROR_MESSAGE);
                }
                return matches;
            }
            catch(Exception e)
            {
                System.out.println("error in highlighting"+e);
                
            }
            return matches;
            }
            DefaultHighlighter.DefaultHighlightPainter myHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
           /* public void highlight(JTextArea textComp, String pattern )
            {
            int matches;
            Highlighter hilite = textComp.getHighlighter();
            hilite.removeAllHighlights();
            //removeHighlights(textComp);
            try{
                //hilite = textComp.getHighlighter();
                javax.swing.text.Document doc = textComp.getDocument();
                String text = doc.getText(0,doc.getLength());
                int pos = 0;
                for(matches=0;(pos=text.toUpperCase().indexOf(pattern.toUpperCase(),pos))>=0;matches++)
                {
                    SearchForWordWorker.failIfInterrupted();
                    hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                    pos +=pattern.length();
                }
                if(hilite.getHighlights().length == 0)
                {
                    JOptionPane.showMessageDialog(component,"No such phrase found!","Nothing Found",JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(Exception e)
            {
                System.out.println("error in highlighting"+e);
                
            }
        }*/
        }
