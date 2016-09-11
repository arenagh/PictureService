package org.arenadev.pictureservice.tools;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class OpenCVTest {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat im = Imgcodecs.imread("/netdrive/pictmp/saveimage/e/aab95d122913bba984bac35bffea88e8.png");	// 入力画像の取得
        Imgcodecs.imwrite("/home/mitchey/result.tif",im);			// 画像データをJPG形式で保存
	}

}
