
import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shubham
 */
/**
 * Implements the text-to-speech data model of the Player application, using
 * JSAPI. It should work with any JSAPI implementation.
 */

public class PlayerModelImpl {
    
    private Synthesizer synthesizer;
    private DefaultComboBoxModel synthesizerList;
    private DefaultComboBoxModel voiceList;
    private float volume = -1;
    //private static boolean debug = false;
    private Set loadedSynthesizers;
    private boolean paused = false;
    private boolean stopped = false;
    //private boolean playingFile = false;


    
    public PlayerModelImpl() {
	synthesizerList = new DefaultComboBoxModel();
	voiceList = new DefaultComboBoxModel();
	loadedSynthesizers = new HashSet();
    }
    
    /**
     * Creates a FreeTTS synthesizer.
     */
    public void createSynthesizers() {
	try {
	    EngineList list = Central.availableSynthesizers(null); 
	    Enumeration e = list.elements();

	    while (e.hasMoreElements()) 
            {
		MySynthesizerModeDesc myModeDesc = new MySynthesizerModeDesc((SynthesizerModeDesc) e.nextElement(), this);
		System.out.println(myModeDesc.getEngineName() + " " +myModeDesc.getLocale() + " " +
			   myModeDesc.getModeName() + " " +
			   myModeDesc.getRunning());
		synthesizerList.addElement(myModeDesc);
	    }
	    
	    if (synthesizerList.getSize() > 0) {
		setSynthesizer(1);
	    } else {
		System.err.println("No synthesizer found");
	    }
            if (synthesizer == null) {
                System.err.println("PlayerModelImpl: Can't find synthesizer");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Returns true if the player is paused.
     *
     * @return true if the player is paused, false otherwise
     */
    public synchronized boolean isPaused() {
	return paused;
    }
    
    public void speak(String text)
{
    System.out.print("Speaking : "+text);    
    synthesizer.speakPlainText(text, null);
}

    /**
     * Pauses the player.
     */
    public synchronized void pause() {
	paused = true;
	synthesizer.pause();
    }
        

    /**
     * Resumes the player.
     */
    public synchronized void resume() {
	paused = false;
	try {
	    synthesizer.resume();
	} catch (AudioException ae) {
	    ae.printStackTrace();
	}	
    }
            

    /**
     * Stops the player if it is playing.
     */
    public synchronized void stop() {
	
	    stopped = true;
	
	synthesizer.cancelAll();
    }


    /**
     * Cancels the currently playing item.
     */
    public void cancel() {
	synthesizer.cancel();
    }
    /**
     * Close this playable
     */
    public void close() {
	for (Iterator i = loadedSynthesizers.iterator(); i.hasNext();) {
	    Synthesizer synth = (Synthesizer) i.next();
	    try {
		synth.deallocate();
	    } catch (EngineException ee) {
		System.out.println("Trouble closing the synthesizer: " + ee);
	    }
	}
    }
    /**
     * Returns true if the Player is currently being stopped.
     *
     * @return true if the Player is currently being stopped; false otherwise
     */    
    private synchronized boolean isStopped() {
	return stopped;
    }
    
    /**
     * Sets the Synthesizer at the given index to use
     *
     * @param index index of the synthesizer in the list
     */
    public void setSynthesizer(int index) {
	MySynthesizerModeDesc myModeDesc = (MySynthesizerModeDesc)
	    synthesizerList.getElementAt(index);
	if (myModeDesc != null) {
	    synthesizer = myModeDesc.getSynthesizer();
	    if (synthesizer == null) {
		synthesizer = myModeDesc.createSynthesizer();
		if (synthesizer == null) {
		    System.out.println("still null");
		} else {
		    System.out.println("created");
		}
	    } else {
		System.out.println("not null");
	    }
	    if (myModeDesc.isSynthesizerLoaded()) {
		setVoiceList(myModeDesc);
	    } else {
		myModeDesc.loadSynthesizer();
	    }

	    loadedSynthesizers.add(synthesizer);
            synthesizerList.setSelectedItem(myModeDesc);
	}
    
    }
    /**
     * Sets the Voice at the given to use.
     *
     * @param index the index of the voice
     */
    public void setVoice(int index) {
	try {
	    Voice voice = (Voice) voiceList.getElementAt(index);
	    if (voice != null) {
		float oldVolume = getVolume();
		float oldSpeakingRate = getSpeakingRate();
		synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
		synthesizer.getSynthesizerProperties().setVoice(voice);
		setVolume(oldVolume);
		setSpeakingRate(oldSpeakingRate);
                voiceList.setSelectedItem(voice);
	    }
	} catch (PropertyVetoException pve) {
	    pve.printStackTrace();
	} catch (InterruptedException ie) {
	    ie.printStackTrace();
	}
    }
    
    /**
     * Returns the volume, in the range of 0 to 10.
     *
     * @return the volume, or -1 if unknown, or an error occurred
     */
    public float getVolume() {
	try {
	    float adjustedVolume =
		synthesizer.getSynthesizerProperties().getVolume();
	    if (adjustedVolume < 0.5) {
		volume = 0;
	    } else {
		volume = (float) ((adjustedVolume - 0.5) * 20);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} 
	return volume;
    }

    /**
     * Sets the volume, in the range of 0 to 10.
     *
     * @param volume the new volume
     *
     * @return true if new volume is set; false otherwise
     */
    public boolean setVolume(float volume) {
	try {
	    float adjustedVolume = (float) (volume/20 + 0.5);
	    if (synthesizer != null) {
		synthesizer.getSynthesizerProperties().setVolume
		    (adjustedVolume);
		this.volume = volume;
		return true;
	    } else {
		this.volume = volume;
		return false;
	    }
	} catch (PropertyVetoException pve) {
	    try {
		synthesizer.getSynthesizerProperties().setVolume(this.volume);
	    } catch (PropertyVetoException pe) {
		pe.printStackTrace();
	    }
	    return false;
	}
    }

    /**
     * Returns the speaking rate.
     *
     * @return the speaking rate, or -1 if unknown or an error occurred
     */
    public float getSpeakingRate() {
	if (synthesizer != null) {
	    return synthesizer.getSynthesizerProperties().getSpeakingRate();
	} else {
	    return -1;
	}
    }

    /**
     * Sets the speaking rate in terms of words per minute.
     *
     * @param wordsPerMin the new speaking rate
     *
     * @return the speaking rate, or -1 if unknown or an error occurred
     */
    public boolean setSpeakingRate(float wordsPerMin) {
	float oldSpeed = getSpeakingRate();
	SynthesizerProperties properties =
	    synthesizer.getSynthesizerProperties();
	try {
	    properties.setSpeakingRate(wordsPerMin);
	    return true;
	} catch (PropertyVetoException pve) {
	    try {
		properties.setSpeakingRate(oldSpeed);
	    } catch (PropertyVetoException pe) {
		pe.printStackTrace();
	    }
	    return false;
	}
    }	
    /**
     * Returns the baseline pitch for the current synthesis voice.
     *
     * @return the baseline pitch for the current synthesis voice
     */
    public float getPitch() {
	return synthesizer.getSynthesizerProperties().getPitch();
    }	

    /**
     * Sets the baseline pitch for the current synthesis voice.
     *
     * @param pitch the baseline pitch
     *
     * @return true if new pitch is set; false otherwise
     */
    public boolean setPitch(float pitch) {
	float oldPitch = getPitch();
	try {
	    synthesizer.getSynthesizerProperties().setPitch(pitch);
	    return true;
	} catch (PropertyVetoException pve) {
	    try {
		synthesizer.getSynthesizerProperties().setPitch(oldPitch);
	    } catch (PropertyVetoException pe) {
		pe.printStackTrace();
	    }
	    return false;
	}
    }
    

    /**
     * Returns the pitch range for the current synthesis voice.
     *
     * @return the pitch range for the current synthesis voice
     */
    public float getRange() {
	return synthesizer.getSynthesizerProperties().getPitchRange();
    }
    

    /**
     * Sets the pitch range for the current synthesis voice.
     *
     * @param range the pitch range
     *
     * @return true if new range is set; false otherwise
     */
    public boolean setRange(float range) {
	float oldRange = getRange();
	try {
	    synthesizer.getSynthesizerProperties().setPitchRange(range);
	    return true;
	} catch (PropertyVetoException pve) {
	    try {
		synthesizer.getSynthesizerProperties().setPitchRange(oldRange);
	    } catch (PropertyVetoException pe) {
		pe.printStackTrace();
	    }
	    return false;
	}
    }
         

    /**
     * Sets the list of voices using the given Synthesizer mode description.
     *
     * @param modeDesc the synthesizer mode description
     */
    public void setVoiceList(SynthesizerModeDesc modeDesc) {
	Voice[] voices = modeDesc.getVoices();
	voiceList.removeAllElements();
	for (int i = 0; i < voices.length; i++) {
	    voiceList.addElement(new MyVoice(voices[i].getName(),
					     voices[i].getGender(),
					     voices[i].getAge(),
					     voices[i].getStyle()));
	}
    }

    /**
     * Returns the list of voices.
     *
     * @return the list of voices
     */
    public ListModel getVoiceList() {
	return voiceList;
    }


    /**
     * Returns the list synthesizers
     *
     * @return the synthesizer list
     */
    public ListModel getSynthesizerList() {
	return synthesizerList;
    }
   /* public JComboBox createComboBox( ComboBoxModel model,String toolTipText,
				    String prototypeDisplayValue) {
	JComboBox comboBox = new JComboBox(model);
	comboBox.setToolTipText(toolTipText);
	comboBox.setPrototypeDisplayValue(prototypeDisplayValue);
	comboBox.setEditable(false);
	return comboBox;
    }
  */
  


/**
 * A Voice that implements the <code>toString()</code> method so that
 * it returns the name of the person who owns this Voice.
 */
class MyVoice extends Voice {


    /**
     * Constructor provided with voice name, gender, age and style.
     *
     * @param name the name of the person who owns this Voice
     * @param gender the gender of the person
     * @param age the age of the person
     * @param style the style of the person
     */
    public MyVoice(String name, int gender, int age, String style) {
	super(name, gender, age, style);
    }


    /**
     * Returns the name of the person who owns this Voice.
     *
     * @param String the name of the person
     */
    public String toString() {
	return getName();
    }
}    
}
class MySynthesizerModeDesc extends SynthesizerModeDesc
{
    private Synthesizer synthesizer = null;
    private boolean synthesizerLoaded = false;
    private PlayerModelImpl playerModelImpl = null;

    public MySynthesizerModeDesc(SynthesizerModeDesc modeDesc,PlayerModelImpl playerModelImpl) 
    {
	super(modeDesc.getEngineName(), modeDesc.getModeName(),
	      modeDesc.getLocale(), modeDesc.getRunning(), 
	      modeDesc.getVoices());
    }
    
    /**
     * Returns true if the synthesizer is already loaded.
     *
     * @return true if the synthesizer is already loaded
     */
    public synchronized boolean isSynthesizerLoaded() 
    {
	if (synthesizer == null)
        {
	    return false;
	}
	return ((synthesizer.getEngineState() & Engine.ALLOCATED) != 0);
    }

    public synchronized Synthesizer getSynthesizer() 
    {
	System.out.println("MyModeDesc.getSynthesizer(): " + getEngineName());
	return synthesizer;
    }
    public Synthesizer createSynthesizer() {
	try {
	    System.out.println("Creating " + getEngineName() + "...");
	    synthesizer = Central.createSynthesizer(this);
	    
	    if (synthesizer == null) 
            {
		System.out.println("Central created null synthesizer");
	    } else 
            {
		synthesizer.allocate();
		synthesizer.resume();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} 
	return synthesizer;
    }
    public Synthesizer loadSynthesizer() 
    {
	try 
        {
	    if (!synthesizerLoaded) 
            {
		System.out.println("Loading " + getEngineName() + "...");
		synthesizerLoaded = true;
		SynthesizerLoader loader = new SynthesizerLoader(synthesizer,this);
		loader.start();
	    }
	} catch (Exception e) 
        {
	    e.printStackTrace();
	}
	return synthesizer;
    }
    
    public String toString()
    {
	return getEngineName();
    }
    public PlayerModelImpl getPlayerModelImpl() {
	return playerModelImpl;
    }





    
}

/**
 * A Thread that loads the Synthesizer.
 */
class SynthesizerLoader extends Thread {
    private Synthesizer synthesizer;
    private MySynthesizerModeDesc modeDesc;
    private PlayerModelImpl playerModelImpl;

   
    
    /**
     * Constructs a SynthesizerLoaded which loads the given Synthesizer.
     *
     * @param synthesizer the Synthesizer to load
     * @param modeDesc the MySynthesizerModeDesc from which we can retrieve
     *    the PlayerModel
     */
    public SynthesizerLoader(Synthesizer synthesizer,
			     MySynthesizerModeDesc modeDesc) {
	this.synthesizer = synthesizer;
	this.modeDesc = modeDesc;
        this.playerModelImpl = modeDesc.getPlayerModelImpl();

    }
    

    /**
     * Implements the <code>run()</code> method of the Thread class.
     */
    public void run() {
	try {
	    System.out.println("allocating...");
	    synthesizer.allocate();
	    System.out.println("...allocated");
	    synthesizer.resume();
	    System.out.println("...resume");
	    playerModelImpl.setVoiceList(modeDesc);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

