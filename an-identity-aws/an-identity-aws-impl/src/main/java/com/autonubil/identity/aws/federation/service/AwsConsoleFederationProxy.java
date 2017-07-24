package com.autonubil.identity.aws.federation.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.openid.impl.entities.OAuthAccessSession;
import com.autonubil.identity.openid.impl.entities.OAuthApprovalSession;
import com.autonubil.identity.openid.impl.entities.OAuthSession;
import com.autonubil.identity.openid.impl.entities.OAuthToken;
import com.autonubil.identity.openid.impl.services.OAuth2ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@PropertySource("aws.federation.properties")
@ConditionalOnProperty(name="aws.federation.enabled", havingValue="true", matchIfMissing = true)
public class AwsConsoleFederationProxy {

	@Value("${aws.federation.access_key}")
	private String awsAccessKey;
	@Value("${aws.federation.secret_access_key}")
	private String awsSecretKey;
	
	@Value("${aws.federation.issuer_url}")
	String issuerURL;
	@Value("${aws.federation.console_url}")
	String consoleURL;
	@Value("${aws.federation.sign_url}")
	String signInURL ;
	
	
	@Autowired
	OAuth2ServiceImpl oauthService;
	
	public String getConsoleAccessUrl(String issuer, Identity identity) throws IOException {
		
		if (issuer != null)
			throw new NotImplementedException("later...");
		 
		
		if (identity== null)
			throw new NullPointerException("identity must not be null");

		
		/* Calls to AWS STS APIs must be signed using the access key ID 
		   and secret access key of an IAM user or using existing temporary 
		   credentials. 
		*/
		AWSCredentials credentials = new BasicAWSCredentials(this.awsAccessKey, this.awsSecretKey);

		AWSSecurityTokenServiceClient stsClient =  new AWSSecurityTokenServiceClient(credentials);

		OAuthApprovalSession session = new OAuthApprovalSession(null, null, null, null,null, identity.getUser().getSourceId(), identity.getUser().getUsername());
		OAuthAccessSession token = oauthService.createAccessToken(session, issuer, identity.getUser().getUsername());
		ObjectMapper mapper = new ObjectMapper();
//		String jwt = mapper.writeValueAsString(token);

		AssumeRoleWithWebIdentityRequest asr = new AssumeRoleWithWebIdentityRequest();

		asr.setRoleArn("arn:aws:iam::118275097572:role/href-openid");
		asr.setWebIdentityToken(token.getToken());
		// asr.setProviderId("href.synology.me/");
		asr.setRoleSessionName(identity.getUser().getId()+ "@an-tintranet" );
		
		AssumeRoleWithWebIdentityResult sessionTokenResult = stsClient.assumeRoleWithWebIdentity(asr);
		Credentials stsCredentials = sessionTokenResult.getCredentials();
		String subjectFromWIF = sessionTokenResult.getSubjectFromWebIdentityToken();

		// Create the sign-in token using temporary credentials,
		// including the access key ID,  secret access key, and security token.
		String sessionJson = String.format(
		  "{\"%1$s\":\"%2$s\",\"%3$s\":\"%4$s\",\"%5$s\":\"%6$s\"}",
		  "sessionId", stsCredentials.getAccessKeyId(),
		  "sessionKey", stsCredentials.getSecretAccessKey(),
		  "sessionToken", stsCredentials.getSessionToken());
		
		String getSigninTokenURL = signInURL + 
                "?Action=getSigninToken" +
                "&SessionDuration=43200" + 
                "&SessionType=json&Session=" + 
                URLEncoder.encode(sessionJson,"UTF-8");

		URL url = new URL(getSigninTokenURL);
		
		// Send the request to the AWS federation endpoint to get the sign-in token
		URLConnection conn = url.openConnection ();
		BufferedReader bufferReader = new BufferedReader(new  InputStreamReader(conn.getInputStream()));
		JsonNode siginTokenNode = mapper.readTree(bufferReader);
		/*
		
		String signinToken = bufferReader.readLine();
		ObjectMapper mapper = new ObjectMapper();
		*/
		
		String signinToken = siginTokenNode.get("SigninToken").asText();
		String signinTokenParameter = "&SigninToken=" + URLEncoder.encode(signinToken,"UTF-8");
		
		// The issuer parameter is optional, but recommended. Use it to direct users
		// to your sign-in page when their session expires.
		
		String issuerParameter = "&Issuer=" + URLEncoder.encode(issuer, "UTF-8");
		
		// Finally, present the completed URL for the AWS console session to the user
		
		String destinationParameter = "&Destination=" + URLEncoder.encode(consoleURL,"UTF-8");
		String loginURL = signInURL + "?Action=login" + signinTokenParameter + issuerParameter + destinationParameter;
		
		System.err.println(loginURL);
		
		return loginURL;
		
		/*
		 // TODO: use sts.eu-central-1.amazonaws.com
		GetFederationTokenRequest getFederationTokenRequest = new GetFederationTokenRequest();
		getFederationTokenRequest.setDurationSeconds(3600);
		getFederationTokenRequest.setName(identity.getUser().getUsername());

		// A sample policy for accessing Amazon Simple Notification Service (Amazon SNS) in the console.

		String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":\"s3:*\"," +
		  "\"Effect\":\"Allow\",\"Resource\":\"*\"}]}";

		
		// arn:aws:iam::118275097572:policy/veb/VEBDefaultUser
		// getFederationTokenRequest.setPolicy("arn:aws:iam::118275097572:policy/veb/VEBDefaultUser" );
		
		getFederationTokenRequest.setPolicy(policy);
		stsClient.assumeRoleWithWebIdentity(assumeRoleWithWebIdentityRequest)

		GetFederationTokenResult federationTokenResult =  stsClient.getFederationToken(getFederationTokenRequest);

		Credentials federatedCredentials = federationTokenResult.getCredentials();

		// The issuer parameter specifies your internal sign-in
		// page, for example https://mysignin.internal.mycompany.com/.
		// The console parameter specifies the URL to the destination console of the
		// AWS Management Console. This example goes to Amazon SNS. 
		// The signin parameter is the URL to send the request to.

		// Create the sign-in token using temporary credentials,
		// including the access key ID,  secret access key, and security token.
		String sessionJson = String.format(
		  "{\"%1$s\":\"%2$s\",\"%3$s\":\"%4$s\",\"%5$s\":\"%6$s\"}",
		  "sessionId", federatedCredentials.getAccessKeyId(),
		  "sessionKey", federatedCredentials.getSecretAccessKey(),
		  "sessionToken", federatedCredentials.getSessionToken());
		              
		// Construct the sign-in request with the request sign-in token action, a
		// 12-hour console session duration, and the JSON document with temporary 
		// credentials as parameters.

		String getSigninTokenURL = signInURL + 
		                           "?Action=getSigninToken" +
		                           "&SessionDuration=43200" + 
		                           "&SessionType=json&Session=" + 
		                           URLEncoder.encode(sessionJson,"UTF-8");

		URL url = new URL(getSigninTokenURL);

		// Send the request to the AWS federation endpoint to get the sign-in token
		URLConnection conn = url.openConnection ();

		BufferedReader bufferReader = new BufferedReader(new  InputStreamReader(conn.getInputStream()));  
		String returnContent = bufferReader.readLine();

		String signinToken = String.format("\"SigninToken\": \"%s\"", returnContent);  // new JSONObject(returnContent).getString("SigninToken");

		String signinTokenParameter = "&SigninToken=" + URLEncoder.encode(signinToken,"UTF-8");

		// The issuer parameter is optional, but recommended. Use it to direct users
		// to your sign-in page when their session expires.

		String issuerParameter = "&Issuer=" + URLEncoder.encode(issuerURL, "UTF-8");

		// Finally, present the completed URL for the AWS console session to the user

		String destinationParameter = "&Destination=" + URLEncoder.encode(consoleURL,"UTF-8");
		String loginURL = signInURL + "?Action=login" + signinTokenParameter + issuerParameter + destinationParameter;
		
		return loginURL;
		*/
	}
	
}
