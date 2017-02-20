package speechapi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class SpeechAPI {
	
	public void GetStatus(String opID){
		HttpClient httpclient = HttpClients.createDefault();
		try
        {
            URIBuilder builder = new URIBuilder(opID);

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", Messages.getString("SpeechAPI.SubscriptionKey"));

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null){
            	JSONObject resp = new JSONObject(EntityUtils.toString(entity));
            	if (resp.getString("status").contains("succeeded")){
                	JSONObject resp1 = resp.getJSONObject("processingResult");
                	//System.out.println(resp1.getString("identifiedProfileId"));
                	System.out.println("This voice belonggs to :" + Messages.MatchKey(resp1.getString("identifiedProfileId")));
            	}
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
	}

	public String CreateProfile(String ID){
		String retVal = "";
		
		if (Messages.isExists("SpeechAPI."+ID)) {
			retVal = Messages.getString("SpeechAPI."+ID);
		}
		else {
			HttpClient httpclient = HttpClients.createDefault();
	
			try
	        {
	            URIBuilder builder = new URIBuilder(Messages.getString("SpeechAPI.URLBase") + "identificationProfiles");
	
	            URI uri = builder.build();
	            HttpPost request = new HttpPost(uri);
	            request.setHeader("Content-Type", "application/json");
	            request.setHeader("Ocp-Apim-Subscription-Key", Messages.getString("SpeechAPI.SubscriptionKey"));
	
	            // Request body
	            JSONObject obj = new JSONObject();
	            obj.put("locale", "en-US");
	            StringEntity reqEntity = new StringEntity(obj.toString());
	            request.setEntity(reqEntity);
	
	            HttpResponse response = httpclient.execute(request);
	            HttpEntity entity = response.getEntity();
	
	            if (entity != null){
	            	JSONObject resp = new JSONObject(EntityUtils.toString(entity));
	            	retVal = resp.getString("identificationProfileId");
	                System.out.println(retVal);
	            }
	        }
	        catch (Exception e){
	            System.out.println(e.getMessage());
	        }
		}
        return retVal;
	}
	
	private byte[] ReadWAVFile(String fileName) {
		//System.out.println(fileName);
		byte[] retVal = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
	
			int read;
			byte[] buff = new byte[1024];
			while ((read = in.read(buff)) > 0)
			{
			    out.write(buff, 0, read);
			}
			out.flush();
			in.close();
			retVal = out.toByteArray();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return retVal;
	}
	
	public void CreateEnrollment(String ID){

		HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder(Messages.getString("SpeechAPI.URLBase") +
            		"identificationProfiles/"+Messages.getString("SpeechAPI."+ID) +
            		"/enroll");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            
            request.setHeader("Content-Type", "applicaation/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", Messages.getString("SpeechAPI.SubscriptionKey"));

            ByteArrayEntity reqEntity = new ByteArrayEntity(
            		ReadWAVFile(Messages.getString("SpeechAPI.WAVFilePath"))
            	);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) 
            {
            	System.out.println("Enrollment Successful!!");
            	org.apache.http.Header[] headers = response.getAllHeaders();
            	for (int i =0; i < headers.length;i++)
            		System.out.println(headers[i].getName() + ":" + headers[i].getValue());
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
	}

	public void SpeakerVerify(String ID) {
	
        HttpClient httpclient = HttpClients.createDefault();
        System.out.println("Processing Speaker Identification...");
        try
        {
            URIBuilder builder = new URIBuilder(Messages.getString("SpeechAPI.URLBase") 
            		+ "identify?identificationProfileIds=" + 
            		Messages.getString("SpeechAPI."+"Ravi")+","+Messages.getString("SpeechAPI."+"Lavanya")
            		);
            builder.setParameter("shortAudio", "true");
            //System.out.println(builder.toString());
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", Messages.getString("SpeechAPI.SubscriptionKey"));


            // Request body
            ByteArrayEntity reqEntity = new ByteArrayEntity(
            		ReadWAVFile(Messages.getString("SpeechAPI.WAVFilePath"))
            	);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) 
            {
            	org.apache.http.Header[] headers = response.getAllHeaders();
            	for (int i =0; i < headers.length;i++) {
            		if (headers[i].getName().contains("Operation")) {
                    	try{Thread.sleep(1500);}catch(InterruptedException e){System.out.println(e);}
            			GetStatus(headers[i].getValue());
            		}
            		//System.out.println(headers[i].getName() + ":" + headers[i].getValue());
            	}
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	public static void main(String[] args) {
		SpeechAPI s = new SpeechAPI();
//		s.CreateProfile("Lavanya");
//		s.CreateEnrollment("Lavanya");
		s.SpeakerVerify("Lavanya");
		s = null;
	}
}
