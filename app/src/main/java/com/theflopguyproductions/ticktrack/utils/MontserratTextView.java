package com.theflopguyproductions.ticktrack.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.theflopguyproductions.ticktrack.R;


public class MontserratTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final static int MONTSERRAT_THIN = 0;
    private final static int MONTSERRAT_THIN_ITALIC = 1;
    private final static int MONTSERRAT_LIGHT = 2;
    private final static int MONTSERRAT_LIGHT_ITALIC = 3;
    private final static int MONTSERRAT_REGULAR = 4;
    private final static int MONTSERRAT_ITALIC = 5;
    private final static int MONTSERRAT_MEDIUM = 6;
    private final static int MONTSERRAT_MEDIUM_ITALIC = 7;
    private final static int MONTSERRAT_BOLD = 8;
    private final static int MONTSERRAT_BOLD_ITALIC = 9;
    private final static int MONTSERRAT_BLACK = 10;
    private final static int MONTSERRAT_BLACK_ITALIC = 11;
    private final static int MONTSERRAT_SEMI_BOLD = 12;
    private final static int MONTSERRAT_SEMI_BOLD_ITALIC = 13;
    private final static int MONTSERRAT_EXTRA_LIGHT = 14;
    private final static int MONTSERRAT_EXTRA_LIGHT_ITALIC = 15;


    private final static SparseArray<Typeface> mTypefaces = new SparseArray<Typeface>(16);

    public MontserratTextView(Context context) {
        super(context);
    }

    public MontserratTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }


    public MontserratTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.MontserratTextView);

        int typefaceValue = values.getInt(R.styleable.MontserratTextView_typeface, 0);
        values.recycle();

        setTypeface(obtaintTypeface(context, typefaceValue));
    }

    private Typeface obtaintTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    private Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface;
        switch (typefaceValue) {
            case MONTSERRAT_THIN:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Thin.ttf");
                break;
            case MONTSERRAT_THIN_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-ThinItalic.ttf");
                break;
            case MONTSERRAT_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.ttf");
                break;
            case MONTSERRAT_LIGHT_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-LightItalic.ttf");
                break;
            case MONTSERRAT_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
                break;
            case MONTSERRAT_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Italic.ttf");
                break;
            case MONTSERRAT_MEDIUM:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Medium.ttf");
                break;
            case MONTSERRAT_MEDIUM_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-MediumItalic.ttf");
                break;
            case MONTSERRAT_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Bold.ttf");
                break;
            case MONTSERRAT_BOLD_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-BoldItalic.ttf");
                break;
            case MONTSERRAT_BLACK:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Black.ttf");
                break;
            case MONTSERRAT_BLACK_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-BlackItalic.ttf");
                break;
            case MONTSERRAT_SEMI_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBold.ttf");
                break;
            case MONTSERRAT_SEMI_BOLD_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBoldItalic.ttf");
                break;
            case MONTSERRAT_EXTRA_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-ExtraLight.ttf");
                break;
            case MONTSERRAT_EXTRA_LIGHT_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-ExtraLightItalic.ttf");
                break;
            default:
                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
        }
        return typeface;
    }

}