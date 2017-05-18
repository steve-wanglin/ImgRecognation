
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Steve-Wang on 17/5/9.
 */
public class GetPersonalInfo {


    public static final String requestUrl="http://zhixing.court.gov.cn/search/newsearch";
    public static final String selectedContry="全国法院（包含地方各级法院）";
    public static final String captchaId="0e7eba1361f8405db1b48c4c021e72d3";
    public static final String cardNum="";
    public static final String pName="";
    public static final String captchaCode="dvw6";

    public static void main(String[] args) {

        getPersonalINFO();
    }


    public static void getPersonalINFO(){

        CloseableHttpClient httpClient= HttpClients.createDefault();
        List<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("captchaId", captchaId));
        formparams.add(new BasicNameValuePair("cardNum", cardNum));
        formparams.add(new BasicNameValuePair("j_captcha", captchaCode));
        formparams.add(new BasicNameValuePair("pname", pName));
        formparams.add(new BasicNameValuePair("searchCourtName",selectedContry));
        formparams.add(new BasicNameValuePair("selectCourtArrange", "1"));
        formparams.add(new BasicNameValuePair("selectCourtId", "1"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost httppost = new HttpPost(requestUrl);
        httppost.setEntity(entity);
        try {
            CloseableHttpResponse response=httpClient.execute(httppost);

            HttpEntity respEntity=response.getEntity();
            if(entity!=null){

                System.out.println("The response structure as below:------------------");
                System.out.println(EntityUtils.toString(respEntity, Consts.UTF_8));
                System.out.println("------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
