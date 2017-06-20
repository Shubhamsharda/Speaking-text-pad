/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texttospeech;

/**
 *
 * @author Shubham
 */
import javax.speech.*;    
import java.util.*;    
import javax.speech.synthesis.*;    

public class TextToSpeech    
{    
String speaktext; 
public void dospeak(String speak,String  voicename)    
{    
    speaktext=speak;    
    String voiceName =voicename;    
    try    
    {    
        SynthesizerModeDesc desc = new SynthesizerModeDesc(null,"general",  Locale.US,null,null);    
        Synthesizer synthesizer =  Central.createSynthesizer(desc);    
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
        System.out.print("Speaking : "+speaktext);    
        synthesizer.speakPlainText(speaktext, null);   
        
        
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);    
        //synthesizer.deallocate();    
    }    
    catch (Exception e)   
    {    
        String message = " missing speech.properties in " + System.getProperty("user.home") + "\n";    
        System.out.println(""+e);    
        System.out.println(message);    
    }    
}
public void speak(String text, Synthesizer synthesizer)
{
    System.out.print("Speaking : "+text);    
    synthesizer.speakPlainText(text, null);
}
public void Pause(Synthesizer synthesizer)
{
    try{
    synthesizer.pause();
    }catch(Exception e)
    {
        System.out.println("Error in pausing "+e);
    }
}
public void Resume(Synthesizer synthesizer)
{
    try{
        synthesizer.resume();
    }catch(Exception e)
    {
        System.out.println("Erro while resuming "+e);
    }
}
public void Stop(Synthesizer synthesizer)
{
  try{
        synthesizer.cancelAll();
    }catch(Exception e)
    {
        System.out.println("Erro while stopping "+e);
    }  
}

/*public static void main(String[] args)    
{    
    TextToSpeech obj=new TextToSpeech(); obj.dospeak("this is very good place lol","kevin16");    
}*/ 

}
