package com.app.rewardapp.Config;

import com.app.rewardapp.R;

public class Config {

    //===========  Enable DEFAULT_LANGUAGE  ======================//
    public static String  DEFAULT_LANGUAGE ="en"; //default language selection


    //===========  Rate us Popup Config  ======================//
    // After how much time app open show rating popup
    public static int REPEAT_COUNT=5;
    public static int TRIGGER_COUNT=5;

    //===========  Game Layout Style  ======================//
    public static int GAME_STYLE=0;  // 0 = old / 1 = new layout


    //===========  Intro Slider Edit  ======================//

    // Slide 1 Json Animation File Name
    public static int SLIDE_ONE_ICON = R.raw.welcome;  // you can find animation file from here https://lottiefiles.com/

    // Slide 2 Json Animation File Name
    public static int SLIDE_TWO_ICON = R.raw.invite;

    // Slide 3 Json Animation File Name
    public static int SLIDE_THREE_ICON = R.raw.slide_three;


}
