import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 29.12.2018.
 */
public class Main extends HttpServlet {

    private static final int DEFAULT_IMAGE_WIDTH = 220; //in pixels
    private static final int DEFAULT_IMAGE_HEIGHT = 220; //in pixels
    private static final int DEFAULT_MARGIN = 2; // padding from borders
    private static final String DEFAULT_TEXT = "TestQR";

    private Map checkParams(int margin){
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.MARGIN, margin);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        return hints;
    }

    private void makeImage(HttpServletRequest req, HttpServletResponse resp, int imageWidth, int imageHeight, String text, Map hints) throws IOException, WriterException {


        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/png");

        ServletContext sc = getServletContext();
        OutputStream os = resp.getOutputStream();
        ImageIO.write(image, "png", resp.getOutputStream());

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String textString = req.getParameter("s");
        String sImageWidth = req.getParameter("w");
        String sImageHeight = req.getParameter("h");
        String sMargin = req.getParameter("m");

        int marginSize;
        try{
            if(sMargin == null){
                marginSize = DEFAULT_MARGIN;
            } else {
                marginSize = Integer.parseInt(sMargin);
            }
        } catch(NumberFormatException e){
            marginSize = DEFAULT_MARGIN;
        }

        int imageWidth;
        try{
            if(sImageWidth != null){
                imageWidth = Integer.parseInt(sImageWidth);
            } else {
                imageWidth = DEFAULT_IMAGE_WIDTH;
            }
            imageWidth = Integer.parseInt(sImageWidth);
        } catch(NumberFormatException nfe){
            imageWidth = DEFAULT_IMAGE_WIDTH;
        }

        int imageHeight;
        try{
            if(sImageHeight != null){
                imageHeight = Integer.parseInt(sImageHeight);
            } else {
                imageHeight = DEFAULT_IMAGE_HEIGHT;
            }
        } catch(NumberFormatException nfe){
            imageHeight = DEFAULT_IMAGE_HEIGHT;
        }

        if(textString != null && !textString.trim().isEmpty()) textString = textString;
        else textString = DEFAULT_TEXT;


        try {
            makeImage(req,resp,imageWidth,imageHeight,textString,checkParams(marginSize));
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
