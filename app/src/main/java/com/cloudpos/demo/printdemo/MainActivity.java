package com.cloudpos.demo.printdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudpos.demo.printdemo.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private TextView numText;
    private InputStream inputStream;
    private PdfRenderer pdfRenderer;
    private PrinterSingleton printer;
    private File file;
    private PdfRenderer.Page currentPage;
    private int pageNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button contentBtn = findViewById(R.id.printPDF);
        Button pageBtn = findViewById(R.id.printPDFInPages);
        Button brightBtn = findViewById(R.id.printPDFWithBrightness);
        Button previous = findViewById(R.id.btnPrevious);
        Button next = findViewById(R.id.btnNext);
        contentBtn.setOnClickListener(this);
        pageBtn.setOnClickListener(this);
        brightBtn.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        numText = findViewById(R.id.numText);
        printer = PrinterSingleton.getInstance(this);
        imageView = (ImageView) findViewById(R.id.wv_imgview);
        openPDFview();
        showPage(pageNum);
    }

    public void openPDFview() {
        try {
            file = FileUtils.copyAssetToFile(this, "ticket.pdf");
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imageView.setImageBitmap(bitmap);
        numText.setText(pageNum + 1 + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printPDF:
                printer.printPDFAsync(getInputStream());
                break;
            case R.id.printPDFInPages:
                printer.printPDFInPagesAsync(getInputStream(), pageNum + 1);
                break;
            case R.id.printPDFWithBrightness:
                printer.printPDFWithBrightness(getInputStream(), pageNum + 1, 128);
                break;
            case R.id.btnPrevious:
                if (pageNum > 0) {
                    pageNum--;
                    showPage(pageNum);
                    numText.setText(pageNum + 1 + "");
                }
                break;
            case R.id.btnNext:
                if (pageNum < pdfRenderer.getPageCount() - 1) {
                    pageNum++;
                    showPage(pageNum);
                    numText.setText(pageNum + 1 + "");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (currentPage != null) {
            currentPage.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        if (file != null) {
            file.delete();
        }
        super.onDestroy();
    }

    public InputStream getInputStream() {
        try {
            inputStream = this.getResources().getAssets().open("ticket.pdf");
            return inputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}