/**
 * Copyright 2014 Jeroen Mols
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dingning.cardzs.utils;

import android.content.Context;
import android.os.Environment;
import com.dingning.cardzs.utils.TimeUtils;

import java.io.File;

public class VideoFile {

	private static final String	DING_NING_CLOUD_FILE= "ydkj";
	private static final String	VOICE_FORMAT = ".mp3";
	private static final String	VIDEO_FORMAT = ".mp4";
	public static final int VOICE = 1;
	public static final int VIDEO = 2;
	private int type;

	private  String	mFilename, mCloudFolder, mFilePath;
	private final Context 		context;

	public VideoFile(Context context, String filename, int  type) {
		this.context = context;
		this.mFilename = filename;
		this.type = type;
		initFile();
	}

	public void initFile() {
		String mTempFilename = fileName();
		mCloudFolder = generateFilePath() + File.separator + mTempFilename;
		mFilePath = getFile().getAbsolutePath() + File.separator + mTempFilename;
	}


	public String getFullPath(){
		return mFilePath;
	}
	public File getFile() {

		File file;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			file = new File(context.getExternalCacheDir().getPath()+File.separator + generateFilePath()+File.separator);
			if (!file.exists()) {
				file.mkdirs();
			}
		} else {
			file = new File(context.getCacheDir().getPath()+File.separator + generateFilePath()+File.separator);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return file;
	}

	private String generateFilePath() {
		return DING_NING_CLOUD_FILE +File.separator +TimeUtils.getYear()+ File.separator + TimeUtils.getMonth()+ File.separator+ TimeUtils.getDayInMonth();
	}

	public String getCloudFolder() {

		return mCloudFolder;
	}

	private String fileName(){
		if(type == VideoFile.VIDEO){
			return isValidFilename()? mFilename + VIDEO_FORMAT :System.currentTimeMillis() + VIDEO_FORMAT;
		}else if(type == VideoFile.VOICE){
			return isValidFilename()? mFilename + VOICE_FORMAT :System.currentTimeMillis() + VOICE_FORMAT;
		}
		return  null;
	}

	private boolean isValidFilename() {
		if (mFilename == null) return false;
		if (mFilename.isEmpty()) return false;

		return true;
	}

	public void deleteFile(){
		File file = new File(mFilePath);
		if (file.exists())
			file.delete();
		mFilePath = null;
		mCloudFolder = null;

	}
}
