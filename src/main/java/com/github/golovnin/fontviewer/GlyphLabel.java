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

import com.jgoodies.binding.value.ValueModel;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.*;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
import static java.util.Objects.requireNonNull;

/**
 * @author Andrej Golovnin
 */
class GlyphLabel extends JLabel {

    static {
        Toolkit kit = Toolkit.getDefaultToolkit();
        System.out.println("awt.font.desktophints = " +
                kit.getDesktopProperty("awt.font.desktophints"));
    }

    private final ValueModel forceGaspHintHolder;

    GlyphLabel(ValueModel forceGaspHintHolder) {
        this.forceGaspHintHolder = requireNonNull(forceGaspHintHolder);
        forceGaspHintHolder.addValueChangeListener(evt -> {
            revalidate();
            repaint();
        });
    }

    private Rectangle viewRectangle = new Rectangle();
    private Rectangle iconRectangle = new Rectangle();
    private Rectangle textRectangle = new Rectangle();

    @Override
    protected void paintComponent(Graphics g) {
        boolean forceGaspHint = Boolean.TRUE.equals(forceGaspHintHolder.getValue());
        if (forceGaspHint && g instanceof Graphics2D) {
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g;
            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, width, height);
            }
            g.setColor(getForeground());
            Object oldTextAntialiasingHint = g2.getRenderingHint(KEY_TEXT_ANTIALIASING);
            g2.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_GASP);
            FontMetrics fm = getFontMetrics(getFont());

            Insets insets = getInsets(null);
            String text = getText();

            viewRectangle.x = insets.left;
            viewRectangle.y = insets.top;
            viewRectangle.width = width - (insets.left + insets.right);
            viewRectangle.height = height - (insets.top + insets.bottom);
            iconRectangle.x = iconRectangle.y = iconRectangle.width = iconRectangle.height = 0;
            textRectangle.x = textRectangle.y = textRectangle.width = textRectangle.height = 0;

            String clippedText = SwingUtilities.layoutCompoundLabel(
                    this,
                    fm,
                    text,
                    null,
                    getVerticalAlignment(),
                    getHorizontalAlignment(),
                    getVerticalTextPosition(),
                    getHorizontalTextPosition(),
                    viewRectangle,
                    iconRectangle,
                    textRectangle,
                    getIconTextGap());

            g2.drawString(clippedText, textRectangle.x, textRectangle.y + fm.getAscent());

            if (oldTextAntialiasingHint != null) {
                g2.setRenderingHint(KEY_TEXT_ANTIALIASING, oldTextAntialiasingHint);
            }
        } else {
            super.paintComponent(g);
        }
    }

}
