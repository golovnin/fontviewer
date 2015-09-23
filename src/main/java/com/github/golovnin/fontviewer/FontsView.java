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

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.BindingConverter;
import com.jgoodies.binding.value.ConverterValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Font;

import static com.github.golovnin.fontviewer.Fonts.*;
import static com.jgoodies.binding.beans.PropertyConnector.connectAndUpdate;
import static java.util.Objects.requireNonNull;

/**
 * @author Andrej Golovnin
 */
final class FontsView {

    private final ValueModel glyphHolder;
    private final PresentationModel<Fonts> model;

    FontsView(ValueModel glyphHolder, PresentationModel<Fonts> model) {
        this.glyphHolder = requireNonNull(glyphHolder, "glyphHolder may not be null");
        this.model = requireNonNull(model, "model may not be null");
    }

    JComponent createView() {
        return FormBuilder.create()
            .columns("p, $lcg, f:max(10dlu;p):g, $ug, f:max(16dlu;p):g, $ug, " +
                    "f:max(24dlu;p):g, $ug, f:max(32dlu;p):g, $ug, " +
                    "f:max(48dlu;p):g, f:0:g")
            .rows("f:0:g, f:p:g, $rg, p")
            .background(UIManager.getColor("List.background"))
            .opaque(true)
            .padding(Paddings.DIALOG)

            .addROLabel("Glyph:")                             .xy(1, 2, "d, b")
            .addROLabel("Size:")                              .xy(1, 4)

            .add(createGlyphLabel(PROPERTY_FONT_10x10))       .xy(3, 2)
            .add(createSizeLabel(PROPERTY_FONT_10x10))        .xy(3, 4)

            .add(createGlyphLabel(PROPERTY_FONT_16x16))       .xy(5, 2)
            .add(createSizeLabel(PROPERTY_FONT_16x16))        .xy(5, 4)

            .add(createGlyphLabel(PROPERTY_FONT_24x24))       .xy(7, 2)
            .add(createSizeLabel(PROPERTY_FONT_24x24))        .xy(7, 4)

            .add(createGlyphLabel(PROPERTY_FONT_32x32))       .xy(9, 2)
            .add(createSizeLabel(PROPERTY_FONT_32x32))        .xy(9, 4)

            .add(createGlyphLabel(PROPERTY_FONT_48x48))       .xy(11, 2)
            .add(createSizeLabel(PROPERTY_FONT_48x48))        .xy(11, 4)

            .build();
    }

    private JComponent createGlyphLabel(String fontPropertyName) {
        JLabel l = new JLabel();
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.BOTTOM);
        Bindings.bind(l, glyphHolder);
        connectAndUpdate(model.getModel(fontPropertyName), l, "font");
        return l;
    }

    private JComponent createSizeLabel(String fontPropertyName) {
        JLabel l = new JLabel();
        l.setHorizontalAlignment(SwingConstants.CENTER);
        Bindings.bind(l, new ConverterValueModel(
                model.getModel(fontPropertyName),
                new SizeConverter()));
        return l;
    }

    private static final class SizeConverter implements
            BindingConverter<Font, String>
    {

        @Override
        public String targetValue(Font sourceValue) {
            return sourceValue != null
                 ? String.valueOf(sourceValue.getSize())
                 : "";
        }

        @Override
        public Font sourceValue(String targetValue) {
            return null;
        }
    }

}
