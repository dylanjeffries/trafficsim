package com.trafficsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public final class UIStyling {

    // Fonts
    public static BitmapFont TITLE_FONT;
    public static BitmapFont BODY_FONT;

    // Widget Styles
    public static Label.LabelStyle TITLE_LABEL_STYLE;
    public static Label.LabelStyle BODY_LABEL_STYLE;
    public static TextField.TextFieldStyle TEXTFIELD_STYLE;

    public static void init(Textures textures) {
        // Fonts
        TITLE_FONT = generateFont("RobotoMono-Regular.ttf", 32);
        BODY_FONT = generateFont("RobotoMono-Regular.ttf", 22);
        BODY_FONT.setColor(Color.BLACK);

        // Widget Styles
        TITLE_LABEL_STYLE = new Label.LabelStyle(TITLE_FONT, Color.BLACK);
        BODY_LABEL_STYLE = new Label.LabelStyle(BODY_FONT, Color.BLACK);

        TEXTFIELD_STYLE = new TextField.TextFieldStyle(BODY_FONT,
                Color.BLACK,
                new Image(textures.get("textfield_cursor")).getDrawable(),
                new Image(textures.get("textfield_selection")).getDrawable(),
                new Image(textures.get("textfield_background")).getDrawable());

    }

    private static BitmapFont generateFont(String fontFileName, int fontSize) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontFileName));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = fontSize;
        return fontGenerator.generateFont(fontParameter);
    }
}
