package com.kuna.dcimagerename_android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class Process extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		
		final Activity c = this;
		
		// process with new thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				int val = 0;
				
				// get directoryname
				File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				File[] fs = dir.listFiles();
				for (File f:fs) {
					if (!f.getName().endsWith(".php")) continue;
					val += ProcessFile(f);
				}
				
				final int resval = val;
				// process end
				c.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(c, String.format("%d개의 파일들이 변환되었습니다.", resval), Toast.LENGTH_SHORT).show();
					}
				});

				finish();
			}
		}).start();
	}
	
	public int ProcessFile(File f) {
		byte sig[] = new byte[256];
		FileInputStream fi;
		FileReader fr;
		try {
			fi = new FileInputStream(f);
			fi.read(sig, 0, 8);
			fi.close();
		} catch (Exception e) {
			Log.e("DCImageExtConvert", String.format("Cannot Read File %s", f.getName()));
		}
		
		// proc ext
        if (((sig[0]&0xFF) == 0xFF && (sig[1]&0xFF) == 0xD8 && (sig[2]&0xFF) == 0xFF && (sig[3]&0xFF) == 0xE0 && (sig[6]&0xFF) == 'J' && (sig[7]&0xFF) == 0x46) ||
            ((sig[0]&0xFF) == 0xFF && (sig[1]&0xFF) == 0xD8 && (sig[2]&0xFF) == 0xFF && (sig[3]&0xFF) == 0xE1 && (sig[6]&0xFF) == 0x45 && (sig[7]&0xFF) == 0x78) ||
            ((sig[0]&0xFF) == 0xFF && (sig[1]&0xFF) == 0xD8 && (sig[2]&0xFF) == 0xFF && (sig[3]&0xFF) == 0xE8 && (sig[6]&0xFF) == 0x53 && (sig[7]&0xFF) == 0x50))
        {
            ConvExt(f, "jpg");
        } else if (((sig[0]&0xFF) =='G' && (sig[1]&0xFF) == 'I' && (sig[2]&0xFF) == 'F' && (sig[3]&0xFF) == '8' && (sig[4]&0xFF) == '7' && (sig[5]&0xFF) == 'a') ||
            ((sig[0]&0xFF) == 'G' && (sig[1]&0xFF) == 'I' && (sig[2]&0xFF) == 'F' && (sig[3]&0xFF) == '8' && (sig[4]&0xFF) == '9' && (sig[5]&0xFF) == 'a')) 
        {
            ConvExt(f, "gif");
        }
        else if ((sig[0]&0xFF) == 0x89 && (sig[1]&0xFF) == 0x50 && (sig[2]&0xFF) == 0x4E && (sig[3]&0xFF) == 0x47 && (sig[4]&0xFF) == 0x0D && (sig[5]&0xFF) == 0x0A && (sig[6]&0xFF) == 0x1A && (sig[7]&0xFF) == 0x0A)
        {
            ConvExt(f, "png");
        }
        else if ((sig[0]&0xFF) == 0xFF && (sig[1]&0xFF) == 0xD8 && (sig[2]&0xFF) == 0xFF)
        {
        	// little signature error
            ConvExt(f, "jpg");
        } else {
        	Log.e("DCImageExtConv", String.format("Error: Undetermined File format: %s", f.getName()));
        	return 0;
        }
		
		return 1;
	}
	
	public void ConvExt(File f, String ext) {
		Date d = new Date(f.lastModified());
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String newname = f.getParent() + "/" + sd.format(d) + "." + ext;
		Log.i("DCImageExtConv", String.format("Converted: %s", newname));
		
		f.renameTo( new File( newname ));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.process, menu);
		return true;
	}

}
