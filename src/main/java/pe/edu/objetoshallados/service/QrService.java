package pe.edu.objetoshallados.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Map;

public final class QrService {
    public String identifier(long itemId) {
        if (itemId <= 0) throw new IllegalArgumentException("Selecciona un objeto válido.");
        return "OBJ-" + String.format("%05d", itemId);
    }

    public BitMatrix generate(long itemId) {
        try {
            return new QRCodeWriter().encode(identifier(itemId), BarcodeFormat.QR_CODE, 240, 240,
                    Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                           EncodeHintType.MARGIN, 2));
        } catch (WriterException exception) {
            throw new IllegalStateException("No se pudo generar el código QR.", exception);
        }
    }
}
