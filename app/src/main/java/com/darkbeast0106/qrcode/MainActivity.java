package com.darkbeast0106.qrcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {
    private TextView textResult;
    private Button btnScan, btnGenerate;
    private EditText editTextQR;
    private ImageView imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btnGenerate.setOnClickListener(v -> {
            String text = editTextQR.getText().toString();
            if (text.isEmpty()){
                Toast.makeText(this, "Üres a beviteli mező", Toast.LENGTH_SHORT).show();
                return;
            }
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                //A szövegből QR_CODE bitmátrix készítése
                BitMatrix bitMatrix = multiFormatWriter.encode(text,
                        BarcodeFormat.QR_CODE, 500,500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                //A bitmátrixból kép készítése amit meg tudunk jeleníteni.
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                imageResult.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });
        btnScan.setOnClickListener(v -> {
            // Kamera megnyitása QR_CODE lekéréséhez. A beolvasás után visszatérve az alkalmazáshoz.
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.setPrompt("QR CODE SCAN");
            intentIntegrator.setCameraId(0);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setBarcodeImageEnabled(true);
            intentIntegrator.initiateScan();
        });
    }

    // Akkor hívódik meg ha az Activityből megnyitott másik activity bezáródáskor adatot küld vissza
    // ennek az Activitynek
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Megvizsgáljuk, hogy IntentIntegrator által készített hívás volt-e.
        IntentResult result =
                IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            // Ha Intentintegratoros hívás volt, megnézzük, hogy küldött-e vissza adatot.
            if (result.getContents() == null) {
                Toast.makeText(this, "Kiléptél a scanből", Toast.LENGTH_SHORT).show();
            } else {
                textResult.setText(result.getContents());

                //Ha a beolvasott QR_CODE egy url-t tartalmaz akkor megpróbál rálépni az url-re.
                try {
                    Uri url = Uri.parse(result.getContents());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                } catch (Exception exception){
                    Log.d("URI ERROR", exception.toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        btnGenerate = findViewById(R.id.btn_generate_qr);
        btnScan = findViewById(R.id.btn_scan_qr);
        textResult = findViewById(R.id.text_Scan_Result);
        editTextQR = findViewById(R.id.edit_text_qr);
        imageResult = findViewById(R.id.image_result);
    }
}