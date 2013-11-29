package com.example.yycq;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yycq.bean.LyricSentence;
import com.example.yycq.bean.LyricText;
import com.example.yycq.config.Config;
import com.example.yycq.media.KaraokeMedia;
import com.example.yycq.media.KaraokeMedia.OnSentenceChangeListener;
import com.example.yycq.parse.LyricParse;

public class RecorderActivity extends Activity {

 	private final static String lyricpath="dyzdj.txt";
 	private List<LyricSentence> sentences;
 	private List<TextView> txtViewSentence = null;
 	private LinearLayout lyricLinearLayout;
 	private static int currentSentence_index ;
 	private static int lastSentence_index;
 	int screenWidth = 0;
 	 int screenHeight = 0;
 	ScrollView mainScrollView;
 	
	// 音频获取源  
    private int audioSource = MediaRecorder.AudioSource.MIC;  
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025  
    private static int sampleRateInHz = 44100;  
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;  
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;  
    // 缓冲区字节大小  
    private int bufferSizeInBytes = 0;  
    private Button Start;  
    private Button Stop;  
    private AudioRecord audioRecord;  
    private boolean isRecord = false;// 设置正在录制的状态  
    //AudioName裸音频数据文件  
    private static final String AudioName = "/sdcard/love.raw";  
    //NewAudioName可播放的音频文件  
    private static final String NewAudioName = "/sdcard/new.wav";
    
    Button button3;
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setContentView(R.layout.activity_recorder);
         lyricLinearLayout = (LinearLayout)findViewById(R.id.article_main);
         mainScrollView = (ScrollView)findViewById(R.id.mainScrollView);
         DisplayMetrics dm = new DisplayMetrics();  
         dm = getResources().getDisplayMetrics();  
         screenWidth  = dm.widthPixels;      // 屏幕宽（像素，如：480px）  
         screenHeight = dm.heightPixels;     // 屏幕高（像素，如：800px）    
         initLrcText();
         
         init();  
         
         button3 = (Button)this.findViewById(R.id.button3);
         button3.setOnClickListener(new TestAudioListener());  
     }
     
     private void init() {  
//         Start = (Button) this.findViewById(R.id.start);  
//         Stop = (Button) this.findViewById(R.id.stop);  
//         Start.setOnClickListener(new TestAudioListener());  
//         Stop.setOnClickListener(new TestAudioListener());  
         creatAudioRecord(); 
         startRecord();  
     } 
     private void creatAudioRecord() {  
         // 获得缓冲区字节大小  
         bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,channelConfig, audioFormat);  
         // 创建AudioRecord对象  
         audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);  
     }  
     
     class TestAudioListener implements OnClickListener {  
         @Override  
         public void onClick(View v) {  
             if (v == Start) {  
                 startRecord();  
             }  
             if (v == button3) {  
                 stopRecord();  
             }  
   
         }  
     }  
   
     private void startRecord() {  
         audioRecord.startRecording();  
         // 让录制状态为true  
         isRecord = true;  
         // 开启音频文件写入线程  
         new Thread(new AudioRecordThread()).start();  
     }  
   
     private void stopRecord() {  
         close();  
     }  
   
     private void close() {  
         if (audioRecord != null) {  
             isRecord = false;//停止文件写入  
             audioRecord.stop();  
             audioRecord.release();//释放资源  
             audioRecord = null;  
         }  
     }  
   
     class AudioRecordThread implements Runnable {  
         @Override  
         public void run() {  
             writeDateTOFile();//往文件中写入裸数据  
             copyWaveFile(AudioName, NewAudioName);//给裸数据加上头文件  
         }  
     }  
   
     /** 
      * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频， 
      * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM 
      * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。 
      */  
     private void writeDateTOFile() {  
         //new一个byte数组用来存一些字节数据，大小为缓冲区大小  
         byte[] audiodata = new byte[bufferSizeInBytes];  
         FileOutputStream fos = null;  
         int readsize = 0;  
         try {  
             File file = new File(AudioName);  
             if (file.exists()) {  
                 file.delete();  
             }  
             fos = new FileOutputStream(file);// 建立一个可存取字节的文件  
         } catch (Exception e) {  
             e.printStackTrace();  
         }  
         while (isRecord == true) {
             readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);  
             if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {  
                 try {  
                     fos.write(audiodata);  
                 } catch (IOException e) {  
                     e.printStackTrace();  
                 }  
             }  
         }  
         try {  
             fos.close();// 关闭写入流  
         } catch (IOException e) {  
             e.printStackTrace();  
         }  
     }  
   
     // 这里得到可播放的音频文件  
     private void copyWaveFile(String inFilename, String outFilename) {  
         FileInputStream in = null;  
         FileOutputStream out = null;  
         long totalAudioLen = 0;  
         long totalDataLen = totalAudioLen + 36;  
         long longSampleRate = sampleRateInHz;  
         int channels = 2;  
         long byteRate = 16 * sampleRateInHz * channels / 8;  
         byte[] data = new byte[bufferSizeInBytes];  
         try {  
             in = new FileInputStream(inFilename);  
             out = new FileOutputStream(outFilename);  
             totalAudioLen = in.getChannel().size();  
             totalDataLen = totalAudioLen + 36;  
             WriteWaveFileHeader(out, totalAudioLen, totalDataLen,  
                     longSampleRate, channels, byteRate);  
             while (in.read(data) != -1) {  
                 out.write(data);  
             }  
             in.close();  
             out.close();  
         } catch (FileNotFoundException e) {  
             e.printStackTrace();  
         } catch (IOException e) {  
             e.printStackTrace();  
         }  
     }  
   
     /** 
      * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。 
      * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav 
      * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有 
      * 自己特有的头文件。 
      */  
     private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,  
             long totalDataLen, long longSampleRate, int channels, long byteRate)  
             throws IOException {  
         byte[] header = new byte[44];  
         header[0] = 'R'; // RIFF/WAVE header  
         header[1] = 'I';  
         header[2] = 'F';  
         header[3] = 'F';  
         header[4] = (byte) (totalDataLen & 0xff);  
         header[5] = (byte) ((totalDataLen >> 8) & 0xff);  
         header[6] = (byte) ((totalDataLen >> 16) & 0xff);  
         header[7] = (byte) ((totalDataLen >> 24) & 0xff);  
         header[8] = 'W';  
         header[9] = 'A';  
         header[10] = 'V';  
         header[11] = 'E';  
         header[12] = 'f'; // 'fmt ' chunk  
         header[13] = 'm';  
         header[14] = 't';  
         header[15] = ' ';  
         header[16] = 16; // 4 bytes: size of 'fmt ' chunk  
         header[17] = 0;  
         header[18] = 0;  
         header[19] = 0;  
         header[20] = 1; // format = 1  
         header[21] = 0;  
         header[22] = (byte) channels;  
         header[23] = 0;  
         header[24] = (byte) (longSampleRate & 0xff);  
         header[25] = (byte) ((longSampleRate >> 8) & 0xff);  
         header[26] = (byte) ((longSampleRate >> 16) & 0xff);  
         header[27] = (byte) ((longSampleRate >> 24) & 0xff);  
         header[28] = (byte) (byteRate & 0xff);  
         header[29] = (byte) ((byteRate >> 8) & 0xff);  
         header[30] = (byte) ((byteRate >> 16) & 0xff);  
         header[31] = (byte) ((byteRate >> 24) & 0xff);  
         header[32] = (byte) (2 * 16 / 8); // block align  
         header[33] = 0;  
         header[34] = 16; // bits per sample  
         header[35] = 0;  
         header[36] = 'd';  
         header[37] = 'a';  
         header[38] = 't';  
         header[39] = 'a';  
         header[40] = (byte) (totalAudioLen & 0xff);  
         header[41] = (byte) ((totalAudioLen >> 8) & 0xff);  
         header[42] = (byte) ((totalAudioLen >> 16) & 0xff);  
         header[43] = (byte) ((totalAudioLen >> 24) & 0xff);  
         out.write(header, 0, 44);  
     }  
     
     
     private void initLrcText(){
     	LyricText lyricText = LyricParse.getLyricTextNew(lyricpath, this);
     	sentences = new ArrayList<LyricSentence>();
     	initialLrc(lyricText);
     	initButton(lyricText);
     }
     
 	public boolean initialLrc(LyricText lyricText){
 		try{
 		  if (lyricText == null || lyricText.getSentences().size() == 0){
 		    return false;
 		  }
 		  else{
 			sentences = lyricText.getSentences();
 			currentSentence_index = -1;
 			lastSentence_index	= -1;	
 			txtViewSentence = new ArrayList<TextView>();
 			LinearLayout layout2 = (LinearLayout) findViewById(R.id.article_main);
 	  	    LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
 	  	    		                               android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
 	        int mCount = sentences.size();
 			for (int i = 0; i < mCount; i ++){
 				TextView tempText1 = new TextView(this);
 				tempText1.setTextSize(Config.FONTSIZE);
 				tempText1.setTextSize(Config.FONTCOLOR);			
 				tempText1.setText(sentences.get(i).getSentence());
 				txtViewSentence.add(tempText1);
 				tempText1.setTextColor(Config.FONTCOLOR);
 				tempText1.setId(i);
 				tempText1.setOnClickListener( new View.OnClickListener(){
 					@Override
 					public void onClick(View v) {
 						if (currentSentence_index == -1)
 							return;
 						currentSentence_index = v.getId();
 					}
 				});
 				layout2.addView(tempText1, i,params);
 			}		    
 		    return true;
 		  }
 		}
 		catch(Exception ex){
 			return false;			
 		}
 	}
 	
 	public void setSentenceForUI(int currentIndex) {
 		 if (currentSentence_index != -1){
 		     txtViewSentence.get(currentIndex).setTextColor(Config.SELECTED_FONTCOLOR); 
 		     txtViewSentence.get(currentIndex).setBackgroundColor(Color.WHITE);
 		 }		
 		 
 		 if(lastSentence_index!=-1) {
 			 txtViewSentence.get(lastSentence_index).setTextColor(Color.BLACK);
 			 txtViewSentence.get(lastSentence_index).setBackgroundColor(Color.TRANSPARENT);
 		 }
 		 
 	       int topSize = lyricLinearLayout.getTop();
 	       int textviewSize = ((TextView)txtViewSentence.get(currentSentence_index)).getHeight();
 	       if((topSize+currentSentence_index*textviewSize)>screenHeight/2){
 	    	   System.out.println("DDDDDDDDDDDDDD");
 	    	   mainScrollView.scrollBy(0, textviewSize);
 	       }
 	       
 		 lastSentence_index = currentSentence_index;
 	}
 	
 	public void setWordForUI(int showWord_index) {		
 		if(currentSentence_index < 0 || currentSentence_index >= sentences.size())
 			return;
 		String sentence = sentences.get(currentSentence_index).getSentence();
 		SpannableString spannableString = new SpannableString(sentence);		   
 		
       spannableString.setSpan(new ForegroundColorSpan(Color.CYAN), 0,showWord_index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
       ((TextView)txtViewSentence.get(currentSentence_index)).setText(spannableString);
 	}
 	
 	private void initButton(final LyricText lyricText){
 		
 		KaraokeMedia.getInstance().SetOnSentenceChangeListener(new OnSentenceChangeListener(){

 			@Override
 			public void OnSentenceChange(int currentIndex) {
 				currentSentence_index = currentIndex;
 				setSentenceForUI(currentIndex);
 			}

 			@Override
 			public void OnWordChange(int showWord_index) {
 				setWordForUI(showWord_index);
 			}
 			
 		});	
 		
 		KaraokeMedia.playArticleMedia(lyricText,getBaseContext());
 	}
 	
 	@Override
 	public boolean onKeyDown(int keyCode, KeyEvent event) {
 		
 		if (keyCode == KeyEvent.KEYCODE_BACK) {
 			KaraokeMedia.stopArticleMedia();
 			
 			finish();
 		}
 		return false;
 	}
}
