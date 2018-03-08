package net.appitiza.android.lifedrop.utils;


public class HomeButtonZoomInterpoletor implements android.view.animation.Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    public HomeButtonZoomInterpoletor(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}