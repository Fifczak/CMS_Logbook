/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.nio.ByteBuffer;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.graphics.ImageFormat.YUV_422_888;
import static android.graphics.ImageFormat.YUV_444_888;

public class QRCodeImageAnalyzer implements ImageAnalysis.Analyzer {

  private QRCodeFoundListener listener;
  public QRCodeImageAnalyzer(QRCodeFoundListener listener) {
    this.listener = listener;
  }

  @Override
  public void analyze(@NonNull ImageProxy image) {
    if (image.getFormat() == YUV_420_888 || image.getFormat() == YUV_422_888 || image.getFormat() == YUV_444_888) {
      ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
      byte[] imageData = new byte[byteBuffer.capacity()];
      byteBuffer.get(imageData);

      PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
              imageData,
              image.getWidth(), image.getHeight(),
              0, 0,
              image.getWidth(), image.getHeight(),
              false
      );

      BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

      try {
        Result result = new QRCodeMultiReader().decode(binaryBitmap);
        listener.onQRCodeFound(result.getText());
      } catch (FormatException | ChecksumException | NotFoundException e) {
        listener.qrCodeNotFound();
      }
    }

    image.close();
  }
}