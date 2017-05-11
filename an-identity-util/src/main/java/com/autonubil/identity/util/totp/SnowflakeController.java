package com.autonubil.identity.util.totp;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.zxing.WriterException;

@Controller
@RequestMapping(value="/autonubil/totp")
public class SnowflakeController {
	
	
	@RequestMapping(value="",method={RequestMethod.GET, RequestMethod.POST})
	public void render(HttpServletResponse response, @RequestParam String subject, @RequestParam String secret, @RequestParam(required=false,defaultValue="200") int size) throws IOException, WriterException {
		response.setContentType("image/png");
		String barCodeData = TotpUtil.generateSecretUrl(secret, subject, subject); 
		TotpUtil.createQRCode(response.getOutputStream(), barCodeData, size, size);
	}

}
