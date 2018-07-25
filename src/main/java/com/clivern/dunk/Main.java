/*
 * MAOZ BEN YEHUDA
 */
 
package com.clivern.dunk;

import static spark.Spark.*;
import com.clivern.racter.BotPlatform;
import com.clivern.racter.receivers.webhook.*;

import com.clivern.racter.senders.*;
import com.clivern.racter.senders.templates.*;

import java.io.Reader;
import java.net.URL;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;


public class Main {
	
	static int flag=0;
	
	private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
    }
	
	private static String GetImagePath(String text) throws IOException {
		InputStream jso = new URL("https://soccer.sportmonks.com/api/v2.0/players/"+text+"?api_token=hh2OxHZRGsyv2OadxKZphffSrnBnzXBaJ9vBRvr6EZDMZOosvPd21IfWZqeX").openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(jso, Charset.forName("UTF-8")));
		String jsonText = readAll(in);
		JSONObject json = new JSONObject(jsonText);
		JSONObject param = json.getJSONObject("data");
		String path = param.get("image_path").toString();
		return path;
	}

    public static void main(String[] args) throws IOException
    {
        // Verify Token Route
        get("/", (request, response) -> {
            BotPlatform platform = new BotPlatform("src/main/java/resources/config.properties");
            platform.getVerifyWebhook().setHubMode(( request.queryParams("hub.mode") != null ) ? request.queryParams("hub.mode") : "");
            platform.getVerifyWebhook().setHubVerifyToken(( request.queryParams("hub.verify_token") != null ) ? request.queryParams("hub.verify_token") : "");
            platform.getVerifyWebhook().setHubChallenge(( request.queryParams("hub.challenge") != null ) ? request.queryParams("hub.challenge") : "");

            if( platform.getVerifyWebhook().challenge() ){
                platform.finish();
                response.status(200);
                return ( request.queryParams("hub.challenge") != null ) ? request.queryParams("hub.challenge") : "";
            }

            platform.finish();
            response.status(403);
            return "Verification token mismatch";
        });

        post("/", (request, response) -> {
            String body = request.body();
            BotPlatform platform = new BotPlatform("src/main/java/resources/config.properties");
            platform.getBaseReceiver().set(body).parse();
            HashMap<String, MessageReceivedWebhook> messages = (HashMap<String, MessageReceivedWebhook>) platform.getBaseReceiver().getMessages();
            for (MessageReceivedWebhook message : messages.values()) {

                String user_id = (message.hasUserId()) ? message.getUserId() : "";
                String page_id = (message.hasPageId()) ? message.getPageId() : "";
                String message_id = (message.hasMessageId()) ? message.getMessageId() : "";
                String message_text = (message.hasMessageText()) ? message.getMessageText() : "";
                String quick_reply_payload = (message.hasQuickReplyPayload()) ? message.getQuickReplyPayload() : "";
                Long timestamp = (message.hasTimestamp()) ? message.getTimestamp() : 0;
                HashMap<String, String> attachments = (message.hasAttachment()) ? (HashMap<String, String>) message.getAttachment() : new HashMap<String, String>();

                platform.getLogger().info("User ID#:" + user_id);
                platform.getLogger().info("Page ID#:" + page_id);
                platform.getLogger().info("Message ID#:" + message_id);
                platform.getLogger().info("Message Text#:" + message_text);
                platform.getLogger().info("Quick Reply Payload#:" + quick_reply_payload);

                for (String attachment : attachments.values()) {
                    platform.getLogger().info("Attachment#:" + attachment);
                }

                String text = message.getMessageText();
                MessageTemplate message_tpl = platform.getBaseSender().getMessageTemplate();
                ButtonTemplate button_message_tpl = platform.getBaseSender().getButtonTemplate();
                ListTemplate list_message_tpl = platform.getBaseSender().getListTemplate();
                GenericTemplate generic_message_tpl = platform.getBaseSender().getGenericTemplate();
                ReceiptTemplate receipt_message_tpl = platform.getBaseSender().getReceiptTemplate();
				System.out.println("the flag value is: "+flag);
				try{
                if( text.equals("player") ){
					flag = 1;
					System.out.println("the flag value is: "+flag);
					System.out.println("PLAYER TABBBBBB");
                    message_tpl.setRecipientId(message.getUserId());
                    message_tpl.setMessageText("what is the player id?");
                    message_tpl.setNotificationType("REGULAR");
                    platform.getBaseSender().send(message_tpl);


				}else if( Integer.parseInt(text) < 100 && Integer.parseInt(text) > 0 && flag == 1){
					System.out.println("in the integer tab with: "+text);
					String path = GetImagePath(text);
					message_tpl.setRecipientId(message.getUserId());
                    message_tpl.setAttachment("image", path, false);
                    message_tpl.setNotificationType("SILENT_PUSH");
                    platform.getBaseSender().send(message_tpl);
					flag = 0;

                
                }else{
					System.out.println("in the else");
                    message_tpl.setRecipientId(message.getUserId());
                    message_tpl.setMessageText("wrong input");
                    message_tpl.setNotificationType("REGULAR");
                    platform.getBaseSender().send(message_tpl);
                }
				}
				catch(Exception e){
					System.out.println("An Error Occur");
                    message_tpl.setRecipientId(message.getUserId());
                    message_tpl.setMessageText("wrong input");
                    message_tpl.setNotificationType("REGULAR");
                    platform.getBaseSender().send(message_tpl);
				}
                return "ok";
            }
            return "bla";
        });
    }
}

	
		
