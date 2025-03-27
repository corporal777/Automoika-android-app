package kg.autojuuguch.automoikakg.ui.views.cropView.cropWindow.util;

import android.graphics.Bitmap;

public interface CustomCropCallback {
    void onSuccess(Bitmap cropped);
    void onError(Throwable error);
}
