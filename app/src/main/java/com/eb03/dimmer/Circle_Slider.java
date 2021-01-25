package com.eb03.dimmer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;

public class Circle_Slider extends  View {
    // Valeur du customView
    private float mValue = 0;

    private boolean mDoubleClick = false;
    private boolean mDisableMove = false;

    // attributs de valeur
    private float mMin = 0;
    private float mMax = 100;

    // attribut d'activation
    private boolean mEnabled = true;

    // référence vers le listener
    private SliderChangeListener mSliderChangeListener = null;


    private final static float MIN_C1_DIAMETER = 40;
    private final static float MIN_C2_DIAMETER = 60;
    private final static float MIN_C3_DIAMETER = 70;



    // Dimensions par défaut
    private final static float DEFAULT_C1_DIAMETER = 110;
    private final static float DEFAULT_C2_DIAMETER = 220;
    private final static float DEFAULT_C3_DIAMETER = 240;
    private final static float DEFAULT_LINE_WIDTH = 6;
    private final static float DEFAULT_TEXT_SIZE = 85;

    //attributs de dimension (en pixels)

    private float mC1Diameter;
    private float mC2Diameter;
    private float mC3Diameter;
    private float mLigneLargeur;
    private float mTextSize;


    // attributs de couleur

    private int mValueBarColor;
    private int mC1Color;
    private int mC2Color;
    private int mC3Color;
    private int mLigneColor;
    private int mValueColor;

    // attributs de pinceaux
    private Paint mC1Paint;
    private Paint mC2Paint;
    private Paint mC3Paint;
    private Paint mValueBarPaint;
    private Paint mLignePaint;
    private Paint mValuePaint;







    private float dpToPixel(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }

    public Circle_Slider(Context context) {
        super(context);
        init(context,null);
    }

    public Circle_Slider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs) {


        mC1Paint = new Paint();
        mC2Paint = new Paint();
        mC3Paint = new Paint();
        mValueBarPaint = new Paint();
        mLignePaint = new Paint();
        mValuePaint= new Paint();


        mC3Color = ContextCompat.getColor(context, R.color.teal_700);
        mC2Color = ContextCompat.getColor(context,R.color.deepSkyBlue);
        mC1Color = ContextCompat.getColor(context,R.color.tomato);
        mValueBarColor = ContextCompat.getColor(context,R.color.dodgerBlue);
        mLigneColor = ContextCompat.getColor(context, R.color.darkStateBlue);
        mValueColor= ContextCompat.getColor(context,R.color.white);

        mC1Diameter = dpToPixel(DEFAULT_C1_DIAMETER);
        mC2Diameter = dpToPixel(DEFAULT_C2_DIAMETER);
        mC3Diameter = dpToPixel(DEFAULT_C3_DIAMETER);
        mLigneLargeur = dpToPixel(DEFAULT_LINE_WIDTH);
        mTextSize = DEFAULT_TEXT_SIZE;

        mValueBarPaint.setStrokeWidth((mC2Diameter-mC1Diameter)/2);
        mLignePaint.setStrokeWidth(mLigneLargeur);
        mLignePaint.setStrokeCap(Paint.Cap.ROUND);
        mValuePaint.setTextAlign(Paint.Align.CENTER);
        mValuePaint.setTextSize(mTextSize);

        //Récupération des attributs xml si présents
        if(attrs !=null){
            TypedArray attr = context.obtainStyledAttributes(attrs,R.styleable.Circle_Slider,0,0);
            mC3Diameter = attr.getDimension(R.styleable.Circle_Slider_C3Diameter,mC3Diameter);
            mC2Diameter = attr.getDimension(R.styleable.Circle_Slider_C2Diameter,mC2Diameter);
            mC1Diameter = attr.getDimension(R.styleable.Circle_Slider_C1Diameter,mC1Diameter);
            mLigneLargeur = attr.getDimension(R.styleable.Circle_Slider_LigneLargeur,mLigneLargeur);
            mTextSize = attr.getDimension(R.styleable.Circle_Slider_TextSize,mTextSize);
            mC3Color = attr.getColor(R.styleable.Circle_Slider_C3Color,mC3Color);
            mC2Color = attr.getColor(R.styleable.Circle_Slider_C2Color,mC2Color);
            mC1Color = attr.getColor(R.styleable.Circle_Slider_C1Color,mC1Color);
            mValueBarColor = attr.getColor(R.styleable.Circle_Slider_ValueBarColor,mValueBarColor);
            mLigneColor = attr.getColor(R.styleable.Circle_Slider_LigneColor,mLigneColor);
            mValueColor = attr.getColor(R.styleable.Circle_Slider_ValueColor,mValueColor);
            mEnabled = attr.getBoolean(R.styleable.Circle_Slider_enabled,mEnabled);
            attr.recycle();
        }





        if(mEnabled){
            mValueBarPaint.setColor(mValueBarColor);
            mC1Paint.setColor(mC1Color);
            mC2Paint.setColor(mC2Color);
            mC3Paint.setColor(mC3Color);
            mLignePaint.setColor(mLigneColor);
            mValuePaint.setColor(mValueColor);

        }else{
            mValueBarPaint.setColor(mC2Color);
        }


        mC3Paint.setStyle(Paint.Style.FILL);
        mC2Paint.setStyle(Paint.Style.FILL);
        mC1Paint.setStyle(Paint.Style.FILL);
        mValueBarPaint.setStyle(Paint.Style.FILL);






    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point center = new Point((int) mC3Diameter / 2, (int) mC3Diameter / 2);

        canvas.drawCircle(getWidth()/2,getHeight()/2,mC3Diameter/2,mC3Paint);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mC2Diameter/2,mC2Paint);

        canvas.drawArc(getWidth()/2-mC2Diameter/2,getHeight()/2-mC2Diameter/2,getWidth()/2+mC2Diameter/2,getHeight()/2+mC2Diameter/2,-90,valueToAngle(mValue),true,mValueBarPaint);

        canvas.drawCircle(getWidth()/2,getHeight()/2,mC1Diameter/2,mC1Paint);

        canvas.drawText(String.format("% 3.1f%%", mValue), getWidth()/2, getHeight()/2 - ((mValuePaint.descent() + mValuePaint.ascent()) / 2), mValuePaint);

        // Rayon correspondant au centre de la piste de réglage
        double rayon = (mC2Diameter + mC1Diameter)/4;
        // Calcul de la longueur des traits en fonction de la largeur de la piste de réglage
        double mLigneLongueur = (mC2Diameter - mC1Diameter)/4 * 0.5;

        // Dessin des graduations représentant les 1/16ème
        for (int i = 0; i <= 15; i++) {
            double angle = i * PI / 8;
            double ligneLongueur = mLigneLongueur;
            // Si on represente un 1/16eme qui n'est pas 1/8eme on divise par deux la longueur du trait
            if(((i + 1) % 2) == 0)
                ligneLongueur /= 2;
            canvas.drawLine((float) (getWidth()/2 + (rayon - ligneLongueur) * cos(angle)),
                    (float) (getHeight()/2 + (rayon - ligneLongueur) * sin(angle)),
                    (float) (getWidth()/2 + (rayon + ligneLongueur) * cos(angle)),
                    (float) (getHeight()/2 + (rayon + ligneLongueur) * sin(angle)),mLignePaint);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int suggestedWidth,suggestedHeight; // dimensions souhaitées par le slider
        int width,height; // dimensions calculées en tenant compte des spécifications du container

        suggestedHeight = (int) mC3Diameter+ getPaddingTop()+ getPaddingBottom();
        suggestedWidth = (int) mC3Diameter+ getPaddingLeft()+ getPaddingRight();

        width = resolveSize(suggestedWidth,widthMeasureSpec);
        height = resolveSize(suggestedHeight,heightMeasureSpec);

        setMeasuredDimension(width,height);
    }


    private float angleToValue(double angle) {
        return (float) (angle * (mMax - mMin) / 360 + mMin);
    }

    private float valueToAngle(float value) {
        return (value * 360/(mMax - mMin));
    }


    private float toValue(Point point) {
        double angle = 0;
        // Calcul de l'angle formé entre le point et l'angle 0 par rapport au centre du cercle
        angle = (toDegrees(atan((point.y - mC3Diameter / 2) / (point.x - mC3Diameter / 2))) + 90);
        // Si le point est dans la partie gauche du cercle on ajoute un offset de 180 (atan renvoie une valeur entre -90° e 90°)
        if (point.x < (mC3Diameter / 2))
            angle += 180;

        // Pour éviter d'avoir un changement trop important
        if (abs(angleToValue(angle) - mValue) >= 10) {
            // Comparaison entre les valeurs actuelles et les nouvelles valeurs pour empecher le passage de 0 à 100 et inversement
            if (mValue >= 98 && angleToValue(angle) <= 2)
                mValue = 100;
            if (mValue <= 2 && angleToValue(angle) >= 98)
                mValue = 0;
            // Si changement trop important on renvoie la valeur précedente ou les valeurs limites
            return mValue;
        }
        return angleToValue(angle);
    }

    /******************************************************************************/
    /*                    Gestion des évènements                                  */
    /******************************************************************************/




    public interface SliderChangeListener{
        void onChange(float value);
        void onDoubleClick(float value);
    }

    public void setSliderChangeListener(SliderChangeListener sliderChangeListener) {
        mSliderChangeListener = sliderChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!mDisableMove) {
                    mValue = toValue(new Point((int)event.getX(),(int)event.getY()));

                    if (mSliderChangeListener != null) mSliderChangeListener.onChange(mValue);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:

                if (mDoubleClick) {
                    mDisableMove = true;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDisableMove = false;
                        }
                    }, 200);
                    mValue = mMin;
                    if (mSliderChangeListener != null)
                        mSliderChangeListener.onDoubleClick(mValue);
                    invalidate();
                } else {
                    mDoubleClick = true;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDoubleClick = false;
                        }
                    }, 500);
                }

                invalidate();
                break;
            default:


        }

        return true;
    }


    /******************************************************************************************/
    /*                           Sauvegarde de l'état actuel du curseur                    */

    /******************************************************************************************/

    private class SavedState extends View.BaseSavedState {

        private final float mSavedValue;

        public SavedState(Parcelable superState) {
            super(superState);
            mSavedValue = mValue;
        }

        private SavedState(Parcel in) {
            super(in);
            mSavedValue = in.readFloat();
        }

        public final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[0];
            }
        };

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(mSavedValue);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState());
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        } else {
            super.onRestoreInstanceState(((SavedState) state).getSuperState());
            mValue = ((SavedState) state).mSavedValue;
        }
    }
}
