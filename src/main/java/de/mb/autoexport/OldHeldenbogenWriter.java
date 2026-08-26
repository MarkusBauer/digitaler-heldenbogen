package de.mb.autoexport;

import de.mb.reflection.HeldReflector;
import helden.cloudinterface.Cloudinterface;
import helden.cloudinterface.HeldenContainer;
import helden.cloudinterface.HeldenContainerImpl;
import helden.plugin.datenplugin.DatenPluginHeldenWerkzeug;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.util.UnitConv;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;


public class OldHeldenbogenWriter {
    public static void heldToPdf(Object held, File output) throws IOException {
        // Their printing fails if you haven't selected the held at least once.
        // At this point, I don't care anymore. You'll likely have to open it to get to the point where it will be exported.
        // maybe this fixes it, maybe not.
        DatenPluginHeldenWerkzeug werkzeug = HeldReflector.getInstance().getWerkzeug(held);
        werkzeug.setAktivenHeld(werkzeug.getSelectesHeld());

        Printable printable = getPrintableForHeld(held);
        heldPrintableToPdf(printable, output);
    }

    public static Printable getPrintableForHeld(Object held) {
        try {
            HeldenContainer container = (HeldenContainer) HeldenContainerImpl.class.getConstructors()[0].newInstance(held);
            return Cloudinterface.getInstance().printHeld(container);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void heldPrintableToPdf(Printable printable, File output) throws IOException {
        Dimension A4 = new Dimension((int) Math.ceil(UnitConv.mm2pt(210)), (int) Math.ceil(UnitConv.mm2pt(297)));

        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(output.toPath()))) {
            PageFormat pf = new PageFormat();
            Paper paper = new Paper();
            paper.setSize(A4.width, A4.height);
            int margin = (int) UnitConv.mm2pt(5);
            paper.setImageableArea(margin, margin, paper.getWidth() - 2 * margin - 3, paper.getHeight() - 2 * margin);
            pf.setPaper(paper);

            FixedImageResolutionGraphics gen = new FixedImageResolutionGraphics();
            gen.setGraphicContext(new GraphicContext());
            gen.setupDocument(os, A4.width, A4.height);

            int pageNumber = 0;
            int renderResult;
            do {
                // This ugly block is just here to detect if there's another page, without actually drawing it.
                // Whoever invented the Printable interface - I HATE YOU.
                Graphics graphics = gen.nop();
                try {
                    if (printable.print(graphics, pf, pageNumber) == Printable.NO_SUCH_PAGE) {
                        break;
                    }
                } catch (UnsupportedOperationException ignored) {
                } finally {
                    graphics.dispose();
                }

                // now draw the page for real
                gen.nextPage();
                graphics = gen.create();
                renderResult = printable.print(graphics, pf, pageNumber++);
                graphics.dispose();
            } while (renderResult == Printable.PAGE_EXISTS);

            gen.finish();

        } catch (PrinterException e) {
            throw new RuntimeException(e);
        }
    }
}
