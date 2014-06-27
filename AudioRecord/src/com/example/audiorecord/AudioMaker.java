package com.example.audiorecord;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioMaker extends Activity {
    /** Called when the activity is first created. */
    static  int frequency = 8000;//分辨率  
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
    static final int audioEncodeing = AudioFormat.ENCODING_PCM_16BIT; 
    static final int yMax = 50;//Y轴缩小比例最大值  
    static final int yMin = 1;//Y轴缩小比例最小值  
	
	int minBufferSize;//采集数据需要的缓冲区大小
	AudioRecord audioRecord;//录音
	AudioProcess audioProcess = new AudioProcess();//处理
	
    Button btnStart,btnExit;  //开始停止按钮
    SurfaceView sfv;  //绘图所用
    ZoomControls zctlX,zctlY;//频谱图缩放
    Spinner spinner;//下拉菜单
    ArrayList<String> list=new ArrayList<String>();
    ArrayAdapter<String>adapter;//下拉菜单适配器
    TextView tView;
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initControl();
        }
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
    
  //初始化控件信息
    private void initControl() {
    	//获取采样率
        tView = (TextView)this.findViewById(R.id.tvSpinner);
        spinner = (Spinner)this.findViewById(R.id.spinnerFre);
        String []ls =getResources().getStringArray(R.array.action);
        for(int i=0;i<ls.length;i++){
        	list.add(ls[i]);
        }
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("请选择采样率");
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
        	@SuppressWarnings("unchecked")
        	public void onItemSelected(AdapterView arg0,View agr1,int arg2,long arg3){
        		frequency = Integer.parseInt(adapter.getItem(arg2));
        		tView.setText("您选择的是:"+adapter.getItem(arg2)+"HZ");
        		Log.i("sunshine",String.valueOf(minBufferSize));
        		arg0.setVisibility(View.VISIBLE);
        	}
        	@SuppressWarnings("unchecked")
        	public void onNothingSelected(AdapterView arg0){
        		arg0.setVisibility(View.VISIBLE);
        	}
        });
        
        
        Context mContext = getApplicationContext();
        //按键
        btnStart = (Button)this.findViewById(R.id.btnStart);
        btnExit = (Button)this.findViewById(R.id.btnExit);
        //按键事件处理
        btnStart.setOnClickListener(new ClickEvent());
        btnExit.setOnClickListener(new ClickEvent());
        //画笔和画板
        sfv = (SurfaceView)this.findViewById(R.id.SurfaceView01);
        //初始化显示
        audioProcess.initDraw(yMax/2, sfv.getHeight(),mContext,frequency);
        //画板缩放
        zctlY = (ZoomControls)this.findViewById(R.id.zctlY);
        zctlY.setOnZoomInClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(audioProcess.rateY - 5>yMin){
                	audioProcess.rateY = audioProcess.rateY - 5;  
	                setTitle("Y轴缩小"+String.valueOf(audioProcess.rateY)+"倍");
                }else{
                	audioProcess.rateY = 1;
	                setTitle("原始尺寸");
                }
            }  
        });  
          
        zctlY.setOnZoomOutClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(audioProcess.rateY<yMax){
                	audioProcess.rateY = audioProcess.rateY + 5;      
	                setTitle("Y轴缩小"+String.valueOf(audioProcess.rateY)+"倍");  
                }else {
                	setTitle("Y轴已经不能再缩小");
				}
            }  
        });
	}
    
    /**
     * 按键事件处理
     */
    class ClickEvent implements View.OnClickListener{
    	@Override
    	public void onClick(View v){
    		Button button = (Button)v;
    		if(button == btnStart){
    			if(button.getText().toString().equals("Start")){
        	        try {
            			//录音
            	        minBufferSize = AudioRecord.getMinBufferSize(frequency, 
            	        		channelConfiguration, 
            	        		audioEncodeing);
            	        //minBufferSize = 2 * minBufferSize; 
            	        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,frequency,
            	        		channelConfiguration,
            	        		audioEncodeing,
            	        		minBufferSize);
            			audioProcess.baseLine = sfv.getHeight()-100;
            			audioProcess.frequence = frequency;
            			audioProcess.start(audioRecord, minBufferSize, sfv);
            			Toast.makeText(AudioMaker.this, 
            					"当前设备支持您所选择的采样率:"+String.valueOf(frequency), 
            					Toast.LENGTH_SHORT).show();
            			btnStart.setText(R.string.btn_exit);
            	        spinner.setEnabled(false);
    				} catch (Exception e) {
    					// TODO: handle exception
            			Toast.makeText(AudioMaker.this, 
            					"当前设备不支持你所选择的采样率"+String.valueOf(frequency)+",请重新选择", 
            					Toast.LENGTH_SHORT).show();
    				}
        		}else if (button.getText().equals("Stop")) {
        			spinner.setEnabled(true);
    				btnStart.setText(R.string.btn_start);
    				audioProcess.stop(sfv);
    			}
    		}
    		else {
    			new AlertDialog.Builder(AudioMaker.this) 
    	         .setTitle("提示") 
    	         .setMessage("确定退出?") 
    	         .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
    	        public void onClick(DialogInterface dialog, int whichButton) { 
    	        setResult(RESULT_OK);//确定按钮事件 
 				AudioMaker.this.finish();
    	         finish(); 
    	         } 
    	         }) 
    	         .setNegativeButton("取消", new DialogInterface.OnClickListener() { 
    	        public void onClick(DialogInterface dialog, int whichButton) { 
    	         //取消按钮事件 
    	         } 
    	         }) 
    	         .show();
			}
    		
    	}
    }
}