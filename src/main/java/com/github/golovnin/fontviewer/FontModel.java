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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Andrej Golovnin
 */
public final class FontModel extends Model {

    static final String PROPERTY_SMALLER_FONT = "smallerFont";
    static final String PROPERTY_SMALL_FONT   = "smallFont";
    static final String PROPERTY_DEFAULT_FONT = "defaultFont";
    static final String PROPERTY_MEDIUM_FONT  = "mediumFont";
    static final String PROPERTY_LARGE_FONT   = "largeFont";
    static final String PROPERTY_LARGER_FONT  = "largerFont";
    static final String PROPERTY_GLYPHS       = "glyphs";

    private final File file;
    private Font smallerFont;
    private Font smallFont;
    private Font defaultFont;
    private Font mediumFont;
    private Font largeFont;
    private Font largerFont;
    private List<String> glyphs;

    FontModel(File file) {
        this.file = requireNonNull(file, "file may not be null");
    }

    void load() throws IOException, FontFormatException {
        int size = UIManager.getFont("List.font").getSize();
        Font newDefaultFont = Font.createFont(Font.TRUETYPE_FONT, file)
                .deriveFont(Font.PLAIN, size);
        List<String> newGlyphs = new ArrayList<>();
        for (char c = 0; c < 0xFFFF; c++) {
            if (newDefaultFont.canDisplay(c)) {
                newGlyphs.add(String.valueOf(c));
            }
        }
        int smallerStep = size / 4;
        int biggerStep = size / 2;
        this.smallerFont = newDefaultFont.deriveFont(Font.PLAIN, size - smallerStep *2);
        this.smallFont = newDefaultFont.deriveFont(Font.PLAIN, size - smallerStep);
        this.defaultFont = newDefaultFont;
        this.mediumFont = newDefaultFont.deriveFont(Font.PLAIN, size + biggerStep);
        this.largeFont = newDefaultFont.deriveFont(Font.PLAIN, size + biggerStep * 2);
        this.largerFont = newDefaultFont.deriveFont(Font.PLAIN, size + biggerStep * 4);
        this.glyphs = newGlyphs;
        EventQueue.invokeLater(this::fireMultiplePropertiesChanged);
    }

    public Font getSmallerFont() {
        return smallerFont;
    }

    public Font getSmallFont() {
        return smallFont;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public Font getMediumFont() {
        return mediumFont;
    }

    public Font getLargeFont() {
        return largeFont;
    }

    public Font getLargerFont() {
        return largerFont;
    }

    public List<String> getGlyphs() {
        return glyphs;
    }

    @Override
    public String toString() {
        return defaultFont.getFontName();
    }

}
