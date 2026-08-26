package de.mb.autoexport;

import org.apache.fop.svg.PDFDocumentGraphics2D;

import java.awt.*;

public class NopGraphics extends PDFDocumentGraphics2D {
    public NopGraphics(PDFDocumentGraphics2D g) {
        super(g);
    }

    @Override
    public void scale(double sx, double sy) {
        throw new UnsupportedOperationException("scale");
    }

    @Override
    public void setColor(Color c) {
    }

    @Override
    public void setBackground(Color color) {
    }
}
