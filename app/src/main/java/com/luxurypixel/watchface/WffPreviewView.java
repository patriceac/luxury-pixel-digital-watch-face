package com.luxurypixel.watchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.View;

import com.cyberpat.luxurypixel.watchface.preview.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

final class WffPreviewView extends View {
    private static final float WFF_SIZE = 450f;
    private static final int SAMPLE_STEPS = 7645;
    private static final int SAMPLE_BATTERY = 78;
    private static final int SAMPLE_HEART_RATE = 72;

    private final Bitmap stepsIcon;
    private final Bitmap batteryIcon;
    private final Bitmap heartIcon;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private final Typeface medium = Typeface.create("sans-serif-medium", Typeface.NORMAL);
    private final Calendar sampleTime = Calendar.getInstance();

    WffPreviewView(Context context) {
        super(context);
        setKeepScreenOn(true);
        sampleTime.set(2026, Calendar.APRIL, 26, 10, 10, 32);
        sampleTime.set(Calendar.MILLISECOND, 0);
        stepsIcon = bitmap(R.drawable.steps_icon);
        batteryIcon = bitmap(R.drawable.battery_icon);
        heartIcon = bitmap(R.drawable.heart_icon);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = Math.min(getWidth(), getHeight()) / WFF_SIZE;
        float offsetX = (getWidth() - WFF_SIZE * scale) * 0.5f;
        float offsetY = (getHeight() - WFF_SIZE * scale) * 0.5f;

        canvas.drawColor(Color.BLACK);
        drawDial(canvas, offsetX, offsetY, scale);
        drawDate(canvas, offsetX, offsetY, scale);
        drawTime(canvas, offsetX, offsetY, scale);
        drawDivider(canvas, offsetX, offsetY, scale);
        drawComplications(canvas, offsetX, offsetY, scale);
    }

    private Bitmap bitmap(int resourceId) {
        return BitmapFactory.decodeResource(getResources(), resourceId);
    }

    private void drawDial(Canvas canvas, float ox, float oy, float scale) {
        float cx = ox + 225f * scale;
        float cy = oy + 225f * scale;
        float radius = 225f * scale;

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(
                cx,
                oy + 210f * scale,
                238f * scale,
                new int[]{Color.rgb(32, 30, 26), Color.rgb(9, 8, 7), Color.BLACK},
                new float[]{0f, 0.58f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.2f * scale);
        paint.setColor(Color.rgb(216, 181, 106));
        canvas.drawCircle(cx, cy, 218f * scale, paint);
        paint.setStrokeWidth(0.8f * scale);
        paint.setColor(Color.rgb(107, 79, 36));
        canvas.drawCircle(cx, cy, 213f * scale, paint);
    }

    private void drawDate(Canvas canvas, float ox, float oy, float scale) {
        Locale locale = Locale.getDefault();
        String pattern = android.text.format.DateFormat.getBestDateTimePattern(locale, "EEE MMM d");
        String date = new SimpleDateFormat(pattern, locale).format(sampleTime.getTime()).toUpperCase(locale);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(light);
        paint.setColor(Color.rgb(233, 197, 121));
        paint.setTextSize(29f * scale);
        drawCenteredText(canvas, date, ox, oy, scale, 62, 64, 326, 44);
    }

    private void drawTime(Canvas canvas, float ox, float oy, float scale) {
        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(getContext());
        Locale locale = Locale.getDefault();
        String pattern = is24Hour ? "HH:mm" : "hh:mm";
        String time = new SimpleDateFormat(pattern, locale).format(sampleTime.getTime());

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(medium);
        paint.setColor(Color.rgb(255, 244, 220));
        paint.setTextSize(136f * scale);
        drawCenteredText(canvas, time, ox, oy, scale, 0, 92, 450, 160, true);

        if (!is24Hour) {
            paint.setTypeface(light);
            paint.setColor(Color.rgb(233, 197, 121));
            paint.setTextSize(14f * scale);
            String amPm = new SimpleDateFormat("a", locale).format(sampleTime.getTime()).toUpperCase(locale);
            drawCenteredText(canvas, amPm, ox, oy, scale, 374, 234, 50, 20);
        }
    }

    private void drawDivider(Canvas canvas, float ox, float oy, float scale) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(1.1f * scale);
        paint.setColor(Color.rgb(200, 157, 71));
        canvas.drawLine(ox + 24f * scale, oy + 260f * scale, ox + 426f * scale, oy + 260f * scale, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 225, 160));
        canvas.drawCircle(ox + 225f * scale, oy + 261f * scale, 3.5f * scale, paint);
    }

    private void drawComplications(Canvas canvas, float ox, float oy, float scale) {
        Locale locale = Locale.getDefault();
        NumberFormat integerFormat = NumberFormat.getIntegerInstance(locale);
        boolean isFrench = "fr".equals(locale.getLanguage());

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f * scale);
        paint.setColor(Color.rgb(216, 181, 106));
        canvas.drawLine(ox + 174f * scale, oy + 325f * scale, ox + 174f * scale, oy + 363f * scale, paint);
        canvas.drawLine(ox + 276f * scale, oy + 325f * scale, ox + 276f * scale, oy + 363f * scale, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(ox + 174f * scale, oy + 345f * scale, 2.5f * scale, paint);
        canvas.drawCircle(ox + 276f * scale, oy + 345f * scale, 2.5f * scale, paint);

        drawBitmap(canvas, stepsIcon, ox, oy, scale, 110, 305, 28, 23);
        drawBitmap(canvas, batteryIcon, ox, oy, scale, 215, 303, 20, 29);
        drawBitmap(canvas, heartIcon, ox, oy, scale, 314, 305, 24, 21);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(light);
        paint.setColor(Color.WHITE);

        paint.setTextSize(27f * scale);
        drawCenteredText(canvas, integerFormat.format(SAMPLE_STEPS), ox, oy, scale, 82, 331, 84, 31);

        paint.setColor(Color.rgb(233, 197, 121));
        paint.setTextSize(13f * scale);
        drawCenteredText(canvas, isFrench ? "PAS" : "STEPS", ox, oy, scale, 88, 356, 72, 21);

        paint.setColor(Color.WHITE);
        paint.setTextSize(27f * scale);
        drawCenteredText(canvas, String.format(locale, "%d%%", SAMPLE_BATTERY), ox, oy, scale, 183, 331, 84, 31);

        paint.setColor(Color.rgb(233, 197, 121));
        paint.setTextSize(13f * scale);
        drawCenteredText(canvas, isFrench ? "BATTERIE" : "BATTERY", ox, oy, scale, 187, 356, 76, 21);

        paint.setColor(Color.WHITE);
        paint.setTextSize(27f * scale);
        drawCenteredText(canvas, String.format(locale, "%d", SAMPLE_HEART_RATE), ox, oy, scale, 284, 331, 84, 31);

        paint.setColor(Color.rgb(233, 197, 121));
        paint.setTextSize(13f * scale);
        drawCenteredText(canvas, "BPM", ox, oy, scale, 291, 356, 70, 21);
    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap, float ox, float oy, float scale, float x, float y, float width, float height) {
        RectF dest = rect(ox, oy, scale, x, y, width, height);
        canvas.drawBitmap(bitmap, null, dest, paint);
    }

    private RectF rect(float ox, float oy, float scale, float x, float y, float width, float height) {
        return new RectF(
                ox + x * scale,
                oy + y * scale,
                ox + (x + width) * scale,
                oy + (y + height) * scale);
    }

    private void drawCenteredText(Canvas canvas, String text, float ox, float oy, float scale, float x, float y, float width, float height) {
        drawCenteredText(canvas, text, ox, oy, scale, x, y, width, height, false);
    }

    private void drawCenteredText(
            Canvas canvas,
            String text,
            float ox,
            float oy,
            float scale,
            float x,
            float y,
            float width,
            float height,
            boolean softGlow) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        int textColor = paint.getColor();
        float centerX = ox + (x + width * 0.5f) * scale;
        float centerY = oy + (y + height * 0.5f) * scale;
        float baseline = centerY - (metrics.ascent + metrics.descent) * 0.5f;
        if (softGlow) {
            paint.setColor(Color.rgb(74, 55, 25));
            canvas.drawText(text, centerX + 1.4f * scale, baseline + 1.4f * scale, paint);
        } else {
            paint.setColor(Color.BLACK);
            canvas.drawText(text, centerX + scale, baseline + scale, paint);
        }
        paint.setColor(textColor);
        canvas.drawText(text, centerX, baseline, paint);
    }
}
