package com.example.yycq.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.yycq.bean.LyricSentence;
import com.example.yycq.bean.LyricText;
import com.example.yycq.bean.LyricWord;

public class LyricParse {
	
	private final static String CODING_TYPE="utf-8";
	
	public static LyricText getLyricTextNew(String lyricpath,Context context) {		
		LyricText lyricText = null; 	
		try {
			String lyricContent = getLyricContent(lyricpath,context);
			if ( lyricContent != null && lyricContent != "")
			{ 
				lyricText = new LyricText();
				lyricText = getLyricTextFromString(context,lyricContent);
			    String soundPath = lyricpath.replace(".txt", ".mp3");
			    lyricText.setSound_path(soundPath);
			}
		} catch (Exception e) {
		   e.printStackTrace();
		   return null;
	   }
	   return lyricText;
	}
	
	
	public static String getLyricContent(String lyricpath,Context context) throws XmlPullParserException, IOException{
		  InputStream inputStream = null ;
		  AssetManager asset = context.getAssets();

		  try {
			  inputStream = asset.open(lyricpath);
		  } catch (IOException e) {
		  }

		  String lyricContent="";
		  try {			
				InputStreamReader inStreamReader = new InputStreamReader(inputStream,CODING_TYPE);				
				BufferedReader br = new BufferedReader(inStreamReader);
		    	String data = null;
		    	while((data = br.readLine())!=null)
		    	{
		    		lyricContent = lyricContent + data;
		    	}
		    	
		    	inStreamReader.close();
		    	inputStream.close();
		    	br.close();

			} catch (FileNotFoundException e) {			
				e.printStackTrace();
			} catch (IOException e) {			
				e.printStackTrace();
			}
		    return lyricContent;
	}
	
	public static LyricText getLyricTextFromString(Context context,String lrcText) {
		
		LyricText lyricText = new LyricText();
		
   		 try {
   			 
		    String[] lrcList = lrcText.split("\\[");
		    List<LyricSentence> sentences = new ArrayList<LyricSentence>();
	    	int count = lrcList.length;
	    	int sentence_id=0;
	    	
	    	for (int i = 0 ; i < count; i ++) {
	    		
	    		if (lrcList[i].trim() != ""){
	    			
			    	String[] lrc_str = lrcList[i].split("\\]");
			    	
			    	if(lrc_str.length<2){
			    		
			    		String[] lrc_info = lrc_str[0].split(":");
			    		
			    		if("ti".equals(lrc_info[0])) {
			    			lyricText.setLyric_ti(lrc_info[1]);
			    			continue;
			    		}
			    		
			    		if("ar".equals(lrc_info[0])) {
			    			lyricText.setLyric_ar(lrc_info[1]);
			    			continue;
			    		}
			    		
			    		if("al".equals(lrc_info[0])) {
			    			//lyricText.setLyric_al(lrc_info[1]);
			    			continue;
			    		}
			    		
			    		if("by".equals(lrc_info[0])) {
			    			lyricText.setLyric_by(lrc_info[1]);
			    			continue;
			    		}
			    		
			    		if("offset".equals(lrc_info[0])) {
			    			lyricText.setLyric_offset(lrc_info[1]);
			    			continue;
			    		}
			    		
			    	} else {			    		
			    		
			    		LyricSentence sentence = new LyricSentence();
			    		sentence.setSentence_id(sentence_id);			    		
			    		sentence_id++;
			    		
			    		String[] lrc_time = lrc_str[0].split(",");
			    		int sentence_offset = Integer.parseInt(lrc_time[0]);
			    		sentence.setSentence_offset(sentence_offset);
			    		
			    		int sentence_duration = Integer.parseInt(lrc_time[1]);
			    		sentence.setSentence_duration(sentence_duration);

			    		parseLyricWord(sentence,lrc_str[1]);
			    		
			    		sentences.add(sentence);
			    	}
	    		}
	    	}
	    	lyricText.setSentences(sentences);
		}
	    catch (Exception e) {
		    return null;
	   }
		return lyricText;
	}
	
	public static void parseLyricWord(LyricSentence sentence,String original_sentence){
		
		List<LyricWord> words = new ArrayList<LyricWord>();
		String sentence_str="";
		
		String[] words_str = original_sentence.split("<");
		int count = words_str.length;
		
		for (int i = 0 ; i < count; i ++) {
			
			if(words_str[i].trim() != ""){
				String[] word_str = words_str[i].split(">");
				if(word_str.length>1){
					LyricWord word = new LyricWord();
					
					word.setWord(word_str[1]);
					sentence_str = sentence_str+word_str[1];
					
					String[] word_time = word_str[0].split(",");
		    		int word_offset = Integer.parseInt(word_time[0]);
		    		word.setWord_offset(word_offset);
		    		
		    		int word_duration = Integer.parseInt(word_time[1]);
		    		word.setWord_duration(word_duration);
		    		
		    		words.add(word);
				}

			}			
		}
		
		sentence.setSentence(sentence_str);
		sentence.setWords(words);
	}
}
