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

import javax.swing.UIManager;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.OpenType;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Andrej Golovnin
 */
public final class FontModel extends Model {

    public static final String PROPERTY_DEFAULT_FONT    = "defaultFont";
    public static final String PROPERTY_FONT_96_DPI     = "fonts96dpi";
    public static final String PROPERTY_FONT_120_DPI    = "fonts120dpi";
    public static final String PROPERTY_FONT_144_DPI    = "fonts144dpi";
    public static final String PROPERTY_FONT_192_DPI    = "fonts192dpi";
    public static final String PROPERTY_GLYPHS          = "glyphs";
    public static final String PROPERTY_FORCE_GASP_HINT = "forceGaspHint";

    private final File file;
    private Font defaultFont;
    private Fonts fonts96dpi;
    private Fonts fonts120dpi;
    private Fonts fonts144dpi;
    private Fonts fonts192dpi;
    private List<String> glyphs;
    private boolean forceGaspHint;

    FontModel(File file) {
        this.file = requireNonNull(file, "file may not be null");
    }

    void load() throws IOException, FontFormatException {
        int size = UIManager.getFont("List.font").getSize();
        Font newDefaultFont = Font.createFont(Font.TRUETYPE_FONT, file)
                .deriveFont(Font.PLAIN, size);
        dumpLoadedFont(newDefaultFont);
        List<String> newGlyphs = new ArrayList<>(newDefaultFont.getNumGlyphs());
        for (char c = 0; c < 0xFFFF; c++) {
            if (newDefaultFont.canDisplay(c)) {
                newGlyphs.add(String.valueOf(c));
            }
        }
        this.defaultFont = newDefaultFont;
        this.fonts96dpi = Fonts.for96dpi(newDefaultFont);
        this.fonts120dpi = Fonts.for120dpi(newDefaultFont);
        this.fonts144dpi = Fonts.for144dpi(newDefaultFont);
        this.fonts192dpi = Fonts.for192dpi(newDefaultFont);
        this.glyphs = newGlyphs;
        EventQueue.invokeLater(this::fireMultiplePropertiesChanged);
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public Fonts getFonts96dpi() {
        return fonts96dpi;
    }

    public Fonts getFonts120dpi() {
        return fonts120dpi;
    }

    public Fonts getFonts144dpi() {
        return fonts144dpi;
    }

    public Fonts getFonts192dpi() {
        return fonts192dpi;
    }

    public List<String> getGlyphs() {
        return glyphs;
    }

    public boolean isForceGaspHint() {
        return forceGaspHint;
    }

    public void setForceGaspHint(boolean forceGaspHint) {
        boolean oldForceGaspHint = isForceGaspHint();
        this.forceGaspHint = forceGaspHint;
        firePropertyChange(PROPERTY_FORCE_GASP_HINT, oldForceGaspHint, forceGaspHint);
    }

    @Override
    public String toString() {
        return defaultFont.getFontName();
    }

    private static void dumpLoadedFont(Font font) {
        System.out.println("Loaded font: " + font);
        try {
            Method getFont2D = Font.class.getDeclaredMethod("getFont2D");
            getFont2D.setAccessible(true);
            Object font2D = getFont2D.invoke(font);

            Class<?> font2DClass = font2D.getClass();
            Field useJavaRasterizer = font2DClass.getSuperclass().getDeclaredField("useJavaRasterizer");
            useJavaRasterizer.setAccessible(true);
            System.out.println("useJavaRasterizer: " + useJavaRasterizer.get(font2D));

            Method getTableSize = font2DClass.getDeclaredMethod("getTableSize", int.class);
            getTableSize.setAccessible(true);
            System.out.println("GASP table size: " + getTableSize.invoke(font2D, OpenType.TAG_GASP));
        } catch (  NoSuchMethodException
                 | InvocationTargetException
                 | IllegalAccessException
                 | NoSuchFieldException e)
        {
            // Ignore
        }
    }

}
