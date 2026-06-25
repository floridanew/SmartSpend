// app/src/main/java/com/example/smartspend/utils/ExportUtils.java
package com.example.smartspend.utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.smartspend.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ExportUtils {

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // ─────────────────────────────────────────────
    //  EXPORT CSV
    // ─────────────────────────────────────────────
    public static boolean exportCSV(Context context, List<Transaction> transactions) {
        String fileName = "smartspend_export_" +
                new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
                        .format(new Date()) + ".csv";

        try {
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ : MediaStore
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                values.put(MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS);
                Uri uri = context.getContentResolver()
                        .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) return false;
                outputStream = context.getContentResolver().openOutputStream(uri);
            } else {
                // Android 9 et moins
                String path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
                outputStream = new java.io.FileOutputStream(path);
            }

            if (outputStream == null) return false;

            // Écriture CSV
            StringBuilder sb = new StringBuilder();
            sb.append("date,type,categorie,montant,description\n");
            for (Transaction t : transactions) {
                sb.append(SDF.format(new Date(t.date))).append(",");
                sb.append(t.type).append(",");
                sb.append(t.category).append(",");
                sb.append(t.amount).append(",");
                sb.append(t.description != null ? t.description.replace(",", ";") : "").append("\n");
            }

            outputStream.write(sb.toString().getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────
    //  EXPORT PDF  (PdfDocument Android natif)
    // ─────────────────────────────────────────────
    public static boolean exportPDF(Context context, List<Transaction> transactions) {
        String fileName = "smartspend_export_" +
                new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
                        .format(new Date()) + ".pdf";

        PdfDocument document = new PdfDocument();

        // Dimensions A4 en points (72 dpi)
        int pageWidth  = 595;
        int pageHeight = 842;
        int margin     = 40;
        int lineHeight = 22;
        int rowsPerPage = (pageHeight - margin * 3) / lineHeight;

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(11);
        headerPaint.setFakeBoldText(true);
        headerPaint.setColor(Color.WHITE);

        Paint cellPaint = new Paint();
        cellPaint.setTextSize(10);
        cellPaint.setColor(Color.BLACK);

        Paint bgHeader = new Paint();
        bgHeader.setColor(Color.parseColor("#1565C0"));

        Paint bgEven = new Paint();
        bgEven.setColor(Color.parseColor("#E3F2FD"));

        Paint bgOdd = new Paint();
        bgOdd.setColor(Color.WHITE);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#BDBDBD"));
        linePaint.setStrokeWidth(0.5f);

        int pageNumber = 0;
        int globalRow = 0;
        Canvas canvas = null;
        PdfDocument.Page page = null;

        while (globalRow <= transactions.size()) {
            // Nouvelle page
            pageNumber++;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                    pageWidth, pageHeight, pageNumber).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();

            int y = margin;

            // ── Titre ──
            canvas.drawText("SmartSpend — Historique des transactions", margin, y, titlePaint);
            y += 30;

            // ── Sous-titre date ──
            Paint subPaint = new Paint();
            subPaint.setTextSize(9);
            subPaint.setColor(Color.GRAY);
            canvas.drawText("Exporté le " + SDF.format(new Date()) +
                    "  |  " + transactions.size() + " transaction(s)", margin, y, subPaint);
            y += 20;

            // ── En-tête tableau ──
            int[] colX = {margin, margin + 70, margin + 140, margin + 230, margin + 310};
            String[] headers = {"Date", "Type", "Catégorie", "Montant (F)", "Description"};
            int[] colW = {70, 70, 90, 80, pageWidth - margin - 310 - margin};

            canvas.drawRect(margin, y, pageWidth - margin, y + lineHeight, bgHeader);
            for (int i = 0; i < headers.length; i++) {
                canvas.drawText(headers[i], colX[i] + 4, y + 15, headerPaint);
            }
            y += lineHeight;

            // ── Lignes de données ──
            int rowOnPage = 0;
            while (globalRow < transactions.size() && rowOnPage < rowsPerPage) {
                Transaction t = transactions.get(globalRow);

                // Fond alterné
                Paint rowBg = (globalRow % 2 == 0) ? bgEven : bgOdd;
                canvas.drawRect(margin, y, pageWidth - margin, y + lineHeight, rowBg);

                // Montant coloré
                Paint amountPaint = new Paint(cellPaint);
                amountPaint.setColor("EXPENSE".equals(t.type)
                        ? Color.parseColor("#F44336")
                        : Color.parseColor("#388E3C"));

                String dateStr   = SDF.format(new Date(t.date));
                String typeStr   = "EXPENSE".equals(t.type) ? "Dépense" : "Revenu";
                String catStr    = t.category != null ? t.category : "";
                String amountStr = String.format(Locale.getDefault(), "%.0f", t.amount);
                String descStr   = t.description != null ? t.description : "";
                // Tronquer description si trop longue
                if (descStr.length() > 22) descStr = descStr.substring(0, 20) + "…";

                canvas.drawText(dateStr,   colX[0] + 4, y + 15, cellPaint);
                canvas.drawText(typeStr,   colX[1] + 4, y + 15, cellPaint);
                canvas.drawText(catStr,    colX[2] + 4, y + 15, cellPaint);
                canvas.drawText(amountStr, colX[3] + 4, y + 15, amountPaint);
                canvas.drawText(descStr,   colX[4] + 4, y + 15, cellPaint);

                // Ligne séparatrice
                canvas.drawLine(margin, y + lineHeight,
                        pageWidth - margin, y + lineHeight, linePaint);

                y += lineHeight;
                globalRow++;
                rowOnPage++;
            }

            // Numéro de page
            Paint pagePaint = new Paint();
            pagePaint.setTextSize(9);
            pagePaint.setColor(Color.GRAY);
            canvas.drawText("Page " + pageNumber,
                    pageWidth / 2f - 15, pageHeight - 20, pagePaint);

            document.finishPage(page);

            if (globalRow >= transactions.size()) break;
        }

        // ── Sauvegarde ──
        try {
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS);
                Uri uri = context.getContentResolver()
                        .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) { document.close(); return false; }
                outputStream = context.getContentResolver().openOutputStream(uri);
            } else {
                String path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
                outputStream = new java.io.FileOutputStream(path);
            }

            if (outputStream == null) { document.close(); return false; }
            document.writeTo(outputStream);
            outputStream.close();
            document.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            return false;
        }
    }
}