/*
 * Copyright (c) 2015, Andrej Golovnin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  Neither the name of fontviewer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.golovnin.fontviewer;

import com.jgoodies.binding.beans.Model;

import java.awt.Font;

/**
 * @author Andrej Golovnin
 */
public final class Fonts extends Model {

    // Scale factors definition:
    // https://msdn.microsoft.com/en-us/library/dn742485.aspx
    // https://msdn.microsoft.com/en-us/library/windows/desktop/dn742485(v=vs.85).aspx

    private static final float SCALE_FACTOR_96_DPI  = 1.0f;
    private static final float SCALE_FACTOR_120_DPI = 1.25f;
    private static final float SCALE_FACTOR_144_DPI = 1.5f;
    private static final float SCALE_FACTOR_192_DPI = 2.0f;

    public static final String PROPERTY_FONT_10x10   = "font10x10";
    public static final String PROPERTY_FONT_DEFAULT = "fontDefault";
    public static final String PROPERTY_FONT_16x16   = "font16x16";
    public static final String PROPERTY_FONT_24x24   = "font24x24";
    public static final String PROPERTY_FONT_32x32   = "font32x32";
    public static final String PROPERTY_FONT_48x48   = "font48x48";

    private final Font font10x10;
    private final Font fontDefault;
    private final Font font16x16;
    private final Font font24x24;
    private final Font font32x32;
    private final Font font48x48;

    private Fonts(Font font10x10, Font fontDefault, Font font16x16,
                  Font font24x24, Font font32x32, Font font48x48)
    {
        this.font10x10 = font10x10;
        this.fontDefault = fontDefault;
        this.font16x16 = font16x16;
        this.font24x24 = font24x24;
        this.font32x32 = font32x32;
        this.font48x48 = font48x48;
    }

    static Fonts for96dpi(Font font) {
        return forScaleFactor(font, SCALE_FACTOR_96_DPI);
    }

    static Fonts for120dpi(Font font) {
        return forScaleFactor(font, SCALE_FACTOR_120_DPI);
    }

    static Fonts for144dpi(Font font) {
        return forScaleFactor(font, SCALE_FACTOR_144_DPI);
    }

    static Fonts for192dpi(Font font) {
        return forScaleFactor(font, SCALE_FACTOR_192_DPI);
    }

    private static Fonts forScaleFactor(Font font, float scaleFactor) {
        return new Fonts(
            font.deriveFont(Font.PLAIN, 10 * scaleFactor),
            font,
            font.deriveFont(Font.PLAIN, 16 * scaleFactor),
            font.deriveFont(Font.PLAIN, 24 * scaleFactor),
            font.deriveFont(Font.PLAIN, 32 * scaleFactor),
            font.deriveFont(Font.PLAIN, 48 * scaleFactor)
        );
    }

    public Font getFont10x10() {
        return font10x10;
    }

    public Font getFontDefault() {
        return fontDefault;
    }

    public Font getFont16x16() {
        return font16x16;
    }

    public Font getFont24x24() {
        return font24x24;
    }

    public Font getFont32x32() {
        return font32x32;
    }

    public Font getFont48x48() {
        return font48x48;
    }

}
