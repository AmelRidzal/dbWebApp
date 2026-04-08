package com.base.dbase.services;

import org.springframework.stereotype.Service;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.io.*;
import java.nio.charset.Charset;

@Service
public class PrintService {

    // Change this to match exactly how the printer appears in Windows
    private static final String PRINTER_NAME = "POS-58";

    public boolean printReceipt(String name, String phone, String date, String problem) {
        try {
            // Build receipt text with ESC/POS commands
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            // Initialize printer
            bos.write(new byte[]{0x1B, 0x40});

            // Center align
            bos.write(new byte[]{0x1B, 0x61, 0x01});

            // Bold on
            bos.write(new byte[]{0x1B, 0x45, 0x01});
            writeLine(bos, "SERVIS PRIJEMNICA");
            // Bold off
            bos.write(new byte[]{0x1B, 0x45, 0x00});

            writeLine(bos, "--------------------------------");

            // Left align
            bos.write(new byte[]{0x1B, 0x61, 0x00});

            writeLine(bos, "Datum:    " + date);
            writeLine(bos, "Ime:      " + (name != null ? name : ""));
            writeLine(bos, "Tel:      " + (phone != null ? phone : ""));
            writeLine(bos, "");
            writeLine(bos, "Opis:");
            writeLine(bos, (problem != null ? problem : ""));
            writeLine(bos, "");
            writeLine(bos, "--------------------------------");
            writeLine(bos, "");
            writeLine(bos, "");
            writeLine(bos, "");

            // Cut paper
            bos.write(new byte[]{0x1D, 0x56, 0x42, 0x00});

            byte[] receipt = bos.toByteArray();

            // Find printer by name
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();

            javax.print.PrintService[] services =
                    PrintServiceLookup.lookupPrintServices(flavor, null);

            javax.print.PrintService targetService = null;
            for (javax.print.PrintService service : services) {
                if (service.getName().equalsIgnoreCase(PRINTER_NAME)) {
                    targetService = service;
                    break;
                }
            }

            if (targetService == null) {
                System.err.println("Printer not found: " + PRINTER_NAME);
                System.err.println("Available printers:");
                for (javax.print.PrintService s : services) {
                    System.err.println("  - " + s.getName());
                }
                return false;
            }

            DocPrintJob job = targetService.createPrintJob();
            Doc doc = new SimpleDoc(receipt, flavor, null);
            job.print(doc, attrs);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void writeLine(ByteArrayOutputStream bos, String text) throws IOException {
        bos.write((text + "\n").getBytes("CP852"));
    }
}