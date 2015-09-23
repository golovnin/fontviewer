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
import com.jgoodies.binding.list.SelectionInList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import static com.github.golovnin.fontviewer.FontModel.*;
import static java.util.Objects.requireNonNull;

/**
 * @author Andrej Golovnin
 */
final class MainModel {

    private final SelectionInList<FontModel> fonts;
    private final SelectionInList<String> glyphs;
    private final PresentationModel<FontModel> fontModel;
    private final PresentationModel<Fonts> fonts96dpiModel;
    private final PresentationModel<Fonts> fonts120dpiModel;
    private final PresentationModel<Fonts> fonts144dpiModel;
    private final PresentationModel<Fonts> fonts192dpiModel;

    MainModel() {
        this.fonts = new SelectionInList<>();
        this.fontModel = new PresentationModel<>(fonts.getSelectionHolder());
        this.fonts96dpiModel = new PresentationModel<>(fontModel.getModel(PROPERTY_FONT_96_DPI));
        this.fonts120dpiModel = new PresentationModel<>(fontModel.getModel(PROPERTY_FONT_120_DPI));
        this.fonts144dpiModel = new PresentationModel<>(fontModel.getModel(PROPERTY_FONT_144_DPI));
        this.fonts192dpiModel = new PresentationModel<>(fontModel.getModel(PROPERTY_FONT_192_DPI));
        this.glyphs = new SelectionInList<>(fontModel.getModel(PROPERTY_GLYPHS));
    }

    SelectionInList<FontModel> getFonts() {
        return fonts;
    }

    SelectionInList<String> getGlyphs() {
        return glyphs;
    }

    PresentationModel<FontModel> getFontModel() {
        return fontModel;
    }

    PresentationModel<Fonts> getFonts96dpiModel() {
        return fonts96dpiModel;
    }

    PresentationModel<Fonts> getFonts120dpiModel() {
        return fonts120dpiModel;
    }

    PresentationModel<Fonts> getFonts144dpiModel() {
        return fonts144dpiModel;
    }

    PresentationModel<Fonts> getFonts192dpiModel() {
        return fonts192dpiModel;
    }

    void addFont() {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        String lastDir = prefs.get("lastdir", null);
        JFileChooser chooser = new JFileChooser(lastDir);
        chooser.setFileFilter(new TTFFileFilter());
        int option = chooser.showOpenDialog(JOptionPane.getRootFrame());
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            File currentDir = chooser.getCurrentDirectory();
            prefs.put("lastdir", currentDir.getAbsolutePath());
            new FontLoader(file).execute();
        }
    }

    void reloadFonts() {
        FontModel[] models = new FontModel[fonts.getList().size()];
        new FontReloader(fonts.getList().toArray(models)).execute();
    }

    private void showError(Throwable e) {
        // TODO
        e.printStackTrace();
    }

    private final class FontLoader extends SwingWorker<FontModel, Void> {

        private final File file;

        FontLoader(File file) {
            this.file = requireNonNull(file, "file may not be null");
        }

        @Override
        protected FontModel doInBackground() throws Exception {
            FontModel fm = new FontModel(file);
            fm.load();
            return fm;
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                return;
            }
            if (isDone()) {
                try {
                    FontModel fm = get();
                    fonts.getList().add(fm);
                    if (fonts.isSelectionEmpty()) {
                        fonts.setSelectionIndex(0);
                    }
                } catch (InterruptedException e) {
                    showError(e);
                } catch (ExecutionException e) {
                    showError(e.getCause());
                }
            }
        }

    }

    private final class FontReloader extends SwingWorker<Void, Void> {

        private final FontModel[] models;

        FontReloader(FontModel[] models) {
            this.models = models;
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (FontModel fm : models) {
                fm.load();
            }
            return null;
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                return;
            }
            if (isDone()) {
                try {
                    get();
                } catch (InterruptedException e) {
                    showError(e);
                } catch (ExecutionException e) {
                    showError(e.getCause());
                }
            }
        }

    }

    private static final class TTFFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory()
                || f.getName().toLowerCase().endsWith(".ttf");
        }

        @Override
        public String getDescription() {
            return "TrueType fonts";
        }
    }

}
