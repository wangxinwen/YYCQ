package com.example.yycq.media;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import com.example.yycq.bean.LyricSentence;
import com.example.yycq.bean.LyricText;
import com.example.yycq.bean.LyricWord;


public class KaraokeMedia {

    private static KaraokeMedia karaokeMedia=new KaraokeMedia();
	
	private static List<Integer> sentencesTimes=null;
	private static MediaPlayer mp3Player = new MediaPlayer();
	private static int currentSentence_index ; 
	private static int currentWord_index;
	private static int showWord_index;
	private static List<LyricSentence> sentences;

	private KaraokeMedia(){}
	
	public static KaraokeMedia getInstance() {
		return karaokeMedia;
	}
	
	public static void playArticleMedia(LyricText lyricText,Context context) {
		
		karaokeMedia.prepareSentencesTimes(lyricText);
		
		currentSentence_index=0;
		currentWord_index=0;
		
		mMode = RUNNING;
		karaokeMedia.update();
		
		AssetManager asset = context.getAssets();
		FileDescriptor fd = null;
		try {
			fd = asset.openFd(lyricText.getSound_path()).getFileDescriptor();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		  
		if(fd == null){
			return;
		}
		
		try {
			mp3Player.setDataSource(fd);
			mp3Player.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}		

		mp3Player.start();
		
	}
	
	
	public static void seekToArticleMedia(int seekTotSentence_index) {
		
		currentSentence_index=seekTotSentence_index;
		mMode = PAUSE;
		
		int seekToTime = sentencesTimes.get(currentSentence_index);
		
		if(mp3Player.isPlaying()){
			mp3Player.seekTo(seekToTime);
		} else {		
			mp3Player.start();
			mp3Player.seekTo(seekToTime);
		}
		
		mMode = RUNNING;
		karaokeMedia.update();
	}
	
	public static boolean pauseArticleMedia() {
		
		if(mp3Player.isPlaying()){
			mp3Player.pause();
			mMode = PAUSE;
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean reStartoArticleMedia() {
		
		if(!mp3Player.isPlaying()){		
			mp3Player.start();
			mMode = RUNNING;
			karaokeMedia.update();
			
			return true;
		} else {
			return false;
		}
	}
	
	private void prepareSentencesTimes(LyricText lyricText){
		
		sentences = lyricText.getSentences();
		int Sentences_size = sentences.size();
		
		if(sentencesTimes!=null){
			sentencesTimes.clear();
		}
		
		sentencesTimes = new ArrayList<Integer>(Sentences_size);
		for(LyricSentence sentence : sentences){
			Integer time = sentence.getSentence_offset();
			sentencesTimes.add(time);
		}
	}
	
    private static int mMediaDelay = 10;
    
    private static int mMode;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int STOP = 3;
    
    private void update() {
        if (mMode == RUNNING) {
        	
            int now = mp3Player.getCurrentPosition();
            if(currentSentence_index < (sentencesTimes.size()))	{
	            if (now >= sentencesTimes.get(currentSentence_index)) {
	            	mOnSentenceChangeListener.OnSentenceChange(currentSentence_index);
	            	currentSentence_index++;	
	            	currentWord_index=0;
	            	showWord_index=0;
	            }	            
            }
            
            if(currentSentence_index <= (sentencesTimes.size()))	{
            	if(currentSentence_index-1>=0){
            		int sentenceIndex4word = currentSentence_index-1;
    	        	LyricSentence currentSentence = sentences.get(sentenceIndex4word);
    	        	List<LyricWord> words = currentSentence.getWords();
    	        	
    	        	if(currentWord_index < words.size()) {
    	        		LyricWord word = words.get(currentWord_index);
    	            	if (now >=currentSentence.getSentence_offset() + word.getWord_offset()) {
    	            		showWord_index = showWord_index + word.getWord().length();
    	            		mOnSentenceChangeListener.OnWordChange(showWord_index);	            		
    	            		currentWord_index++;
    	            	}
    	        	}
            	}
            }
            
            mRedrawHandler.sleep(mMediaDelay);
        }

    }
	
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	karaokeMedia.update();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    
	public void SetOnSentenceChangeListener(
			OnSentenceChangeListener l) {
		mOnSentenceChangeListener = l;
	}
	
	OnSentenceChangeListener mOnSentenceChangeListener = null;
	
	public interface OnSentenceChangeListener {
		void OnSentenceChange(int currentSentence_index);
		void OnWordChange(int showWord_index);
	}
	
	public static void stopArticleMedia() {
		mMode=STOP;
		if(mp3Player!=null) {
			mp3Player.stop();
			mp3Player.release();
			mp3Player=null;
		}		
	}
	
}
