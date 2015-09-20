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

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.BindingConverter;
import com.jgoodies.binding.value.ConverterValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.factories.Paddings;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import javax.swing.*;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import static com.github.golovnin.fontviewer.FontModel.*;
import static com.jgoodies.binding.beans.PropertyConnector.connectAndUpdate;
import static java.util.Objects.requireNonNull;

/**
 * @author Andrej Golovnin
 */
final class MainView {

    private final MainModel model;

    MainView(MainModel model) {
        this.model = requireNonNull(model, "model may not be null");
    }

    void show() {
        configureLaF();
        JFrame frame = new JFrame("Font :: Viewer");
        JOptionPane.setRootFrame(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(createMainView());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void configureLaF() {
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                UIManager.setLookAndFeel(new WindowsLookAndFeel());
            } catch (UnsupportedLookAndFeelException e) {
                // Ignore
            }
        }
    }

    private JComponent createMainView() {
        return FormBuilder.create()
                .columns("120dlu, $ug, f:240dlu:g")
                .rows("p, $rg, f:240dlu:g, $rg, f:80dlu:g, $rg, p")
                .padding(Paddings.DIALOG)

                .add("Fonts:")           .xy(1, 1)
                .add(createFontList())   .xywh(1, 3, 1, 3)

                .add("Glyphs:")          .xy(3, 1)
                .add(createGlyphsList()) .xy(3, 3)
                .add(createGlyphView())  .xy(3, 5)

                .add(createButtonBar())  .xy(1, 7)
                .build();
    }

    private JComponent createFontList() {
        JList<FontModel> list = createList(model.getFonts(), null);
        return createScrollPane(list);
    }

    private JComponent createGlyphsList() {
        DefaultListCellRenderer r = new DefaultListCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        JList<String> list = createList(model.getGlyphs(), r);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(0);
        connectAndUpdate(getFontModel(PROPERTY_DEFAULT_FONT), list, "font");
        return createScrollPane(list);
    }

    private JComponent createButtonBar() {
        return ButtonBarBuilder.create()
            .addButton(new AddAction(), new ReloadAction())
            .build();
    }

    private JComponent createGlyphView() {
        return createScrollPane(FormBuilder.create()
                .columns("f:p:g, $ug, f:p:g, $ug, f:p:g, $ug, f:p:g, $ug, f:p:g, $ug, f:p:g, f:0:g")
                .rows("p, $pg, f:p:g, $rg, p, f:0:g")
                .background(UIManager.getColor("List.background"))

                .add(createUnicodeField()).xyw(1, 1, 12, "l, c")

                .add(createGlyphLabel(PROPERTY_SMALLER_FONT)).xy(1, 3)
                .add(createSizeLabel(PROPERTY_SMALLER_FONT)) .xy(1, 5)

                .add(createGlyphLabel(PROPERTY_SMALL_FONT))  .xy(3, 3)
                .add(createSizeLabel(PROPERTY_SMALL_FONT))   .xy(3, 5)

                .add(createGlyphLabel(PROPERTY_DEFAULT_FONT)).xy(5, 3)
                .add(createSizeLabel(PROPERTY_DEFAULT_FONT)) .xy(5, 5)

                .add(createGlyphLabel(PROPERTY_MEDIUM_FONT)) .xy(7, 3)
                .add(createSizeLabel(PROPERTY_MEDIUM_FONT))  .xy(7, 5)

                .add(createGlyphLabel(PROPERTY_LARGE_FONT))  .xy(9, 3)
                .add(createSizeLabel(PROPERTY_LARGE_FONT))   .xy(9, 5)

                .add(createGlyphLabel(PROPERTY_LARGER_FONT)) .xy(11, 3)
                .add(createSizeLabel(PROPERTY_LARGER_FONT))  .xy(11, 5)

                .build());
    }

    private JComponent createUnicodeField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBorder(null);
        field.setMargin(new Insets(0, 0, 0, 0));
        Bindings.bind(field, new ConverterValueModel(
                model.getGlyphs().getSelectionHolder(),
                new UnicodeConverter()));
        return field;
    }

    private JComponent createGlyphLabel(String fontPropertyName) {
        JLabel l = new JLabel();
        l.setHorizontalAlignment(SwingConstants.CENTER);
        Bindings.bind(l, model.getGlyphs().getSelectionHolder());
        connectAndUpdate(getFontModel(fontPropertyName), l, "font");
        return l;
    }

    private JComponent createSizeLabel(String fontPropertyName) {
        JLabel l = new JLabel();
        l.setHorizontalAlignment(SwingConstants.CENTER);
        Bindings.bind(l, new ConverterValueModel(
                getFontModel(fontPropertyName),
                new SizeConverter()));
        return l;
    }

    private ValueModel getFontModel(String propertyName) {
        return model.getFontModel().getModel(propertyName);
    }

    private static JComponent createScrollPane(JComponent content) {
        JScrollPane pane = new JScrollPane(content);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return pane;
    }

    @SuppressWarnings("unchecked")
    private static <E> JList<E> createList(SelectionInList<E> selectionInList, ListCellRenderer cellRenderer) {
        JList list = new JList();
        Bindings.bind(list, selectionInList);
        if (cellRenderer != null) {
            list.setCellRenderer(cellRenderer);
        }
        return list;
    }

    private final class AddAction extends AbstractAction {

        AddAction() {
            super("Add\u2026");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.addFont();
        }
    }

    private final class ReloadAction extends AbstractAction {

        ReloadAction() {
            super("Reload");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.reloadFonts();
        }
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

    private static final class UnicodeConverter implements
            BindingConverter<String, String>
    {

        @Override
        public String targetValue(String sourceValue) {
            return sourceValue != null
                 ? "\\u" + Integer.toHexString(sourceValue.charAt(0)).toUpperCase()
                 : "";
        }

        @Override
        public String sourceValue(String targetValue) {
            return null;
        }
    }

}
