package de.mb.heldenbogen;


import org.openqa.selenium.Pdf;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.print.PageSize;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class PdfCreator implements Closeable {

    protected final RemoteWebDriver driver;

    public static void createSingle(String html, File output) {
        try (PdfCreator creator = new PdfCreator()) {
            creator.create(html, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PdfCreator() {
        driver = getDriver();
    }

    @Override
    public void close() throws IOException {
        driver.quit();
    }

    public synchronized void create(String html, File output) {
        File tmpfile = null;
        try {
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            if (bytes.length > 1200000) {
                tmpfile = File.createTempFile("heldenbogen", ".html");
                Files.write(tmpfile.toPath(), bytes);
                driver.get("file://" + tmpfile.getAbsolutePath().replace('\\', '/'));
            } else {
                driver.get("data:text/html;base64," + Base64.getEncoder().encodeToString(bytes));
            }
            Thread.sleep(50);
            PrintOptions options = new PrintOptions();
            options.setPageSize(PageSize.ISO_A4);
            options.setScale(1.0);
            Pdf pdf = driver.print(options);
            byte[] pdfContent = Base64.getDecoder().decode(pdf.getContent());
            Files.write(output.toPath(), pdfContent);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (tmpfile != null)
                tmpfile.delete();
        }
    }

    private RemoteWebDriver getDriver() {
        try {
            return getChromeDriver();
        } catch (Exception e) {
            System.err.println("Chrome failed, trying Edge... " + e.getMessage());
            try {
                return getEdgeDriver();
            } catch (Exception e2) {
                System.err.println("Edge failed, trying Firefox... " + e.getMessage());
                return getFirefoxDriver();
            }
        }
    }

    private RemoteWebDriver getFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        options.addArguments("--width=2560", "--height=1440");
        return new FirefoxDriver(options);
    }

    private RemoteWebDriver getChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        if (new File("/usr/bin/chromium").exists()) {
            options.setBinary("/usr/bin/chromium");
            System.err.println("Try chromium over chrome...");
        }
        options.addArguments("--headless=new");
        options.addArguments("--window-size=2560,1440");
        return new ChromeDriver(options);
    }

    private RemoteWebDriver getEdgeDriver() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=2560,1440");
        return new EdgeDriver(options);
    }
}
