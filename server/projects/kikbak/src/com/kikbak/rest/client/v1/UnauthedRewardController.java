package com.kikbak.rest.client.v1;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.kikbak.client.service.v1.OfferExhaustedException;
import com.kikbak.client.service.v1.OfferExpiredException;
import com.kikbak.client.service.v1.RewardService;
import com.kikbak.jaxb.v1.barcode.BarcodeResponse;
import com.kikbak.jaxb.v1.claim.ClaimCreditRequest;
import com.kikbak.jaxb.v1.claim.ClaimCreditResponse;
import com.kikbak.jaxb.v1.statustype.SuccessStatus;

@Controller
@RequestMapping("/unauth/rewards")
public class UnauthedRewardController extends AbstractController {

    private static final Logger logger = Logger.getLogger(UnauthedRewardController.class);

    @Autowired
    RewardService service;

    @RequestMapping(value = "/generateBarcode//{barcode}/{height}/{width}/", method = RequestMethod.GET)
    public void generateBarcode(
            @PathVariable("barcode") String barcode, @PathVariable("height") Integer height,
            @PathVariable("width") Integer width, final HttpServletResponse httpResponse) {

        try {
            BitMatrix result = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, width, height);
            httpResponse.setContentType("image/png");
            httpResponse.addHeader("barcode", barcode);
            MatrixToImageWriter.writeToStream(result, "png", httpResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @RequestMapping(value = "/claim/{userId}/", method = RequestMethod.POST)
    public ClaimCreditResponse claimCredit(@PathVariable("userId") Long userId,
            @RequestBody ClaimCreditRequest request, final HttpServletResponse httpResponse) {
        try {
            ClaimCreditResponse response = new ClaimCreditResponse();
            response.setStatus(SuccessStatus.OK);
            service.claimCredit(userId, request.getClaim());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
    
    @RequestMapping(value = "/allocateAnonymousBarcode/{refferalCode}", method = RequestMethod.GET)
    public BarcodeResponse allocateAnonymousBarcode(@PathVariable("refferalCode") String referralCode,
            final HttpServletResponse httpResponse) {
        try {
            BarcodeResponse response = new BarcodeResponse();
            service.getBarcode(referralCode, response);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;            
        } catch (OfferExpiredException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_GONE);
            return null;
        } catch (OfferExhaustedException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    // TEMPORARY FOR TESTING 
    @RequestMapping(value = "/registerBarcode/{barcode}", method = RequestMethod.GET)
    public void registerBarcode(@PathVariable("barcode") String code,
            final HttpServletResponse httpResponse) throws IOException {
        try {
            service.registerBarcodeRedemption(code, new Date());
            httpResponse.getWriter().print("OK!");
            return;
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().print(e.getMessage());
            return;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().print(e.getMessage());
            return;
        }
    }
}
