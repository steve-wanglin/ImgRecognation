

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * Created by Steve-Wang on 17/5/10.
 */
public class ImageRecognition {

    public static  String dowloadImgUrl="http://zhixing.court.gov.cn/search/captcha.do?captchaId=51ef411b764c490abc3171c6604dee53&random=";
    public static  boolean isWhite(int colorInt){

        Color color=new Color(colorInt);
        return color.getRed()+color.getGreen()+color.getBlue()==765;
    }


    public  static BufferedImage  removeBackground(String picFile) throws IOException {


        BufferedImage img= ImageIO.read(new File(picFile));
        int width=img.getWidth();
        int height=img.getHeight();
        for(int x=0;x<width;++x){
            for(int y=0;y<height;++y){
                if(isWhite(img.getRGB(x,y))){
                    img.setRGB(x,y,Color.white.getRGB());
                }else{
                    img.setRGB(x,y,Color.black.getRGB());
                }
            }
        }

            return img;
    }

    public static  Map<BufferedImage,String> loadTrainData() throws IOException {
        Map<BufferedImage,String> map=new HashMap<>();
        File dict=new File("img");
        File[] files=dict.listFiles();
        for(File file:files){
            map.put(ImageIO.read(file),file.getName().charAt(0)+"");
        }
        return map;
    }


    public static String getSingleCharOcr(BufferedImage img,
                                          Map<BufferedImage, String> map) {
        String result = "";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = width * height;
        for (BufferedImage bi : map.keySet()) {
            int count = 0;
            Label1: for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (isWhite(img.getRGB(x, y)) != isWhite(bi.getRGB(x, y))) {
                        count++;
                        if (count >= min)
                            break Label1;
                    }
                }
            }
            if (count < min) {
                min = count;
                result = map.get(bi);
            }
        }
        return result;
    }


    public static String getAllOcr(String file) throws Exception {
        BufferedImage img = removeBackground(file);
//        List<BufferedImage> listImg = splitImage(img);
        Map<BufferedImage, String> map = loadTrainData();
        String result = "";
//        for (BufferedImage bi : listImg) {
//            result += getSingleCharOcr(bi, map);
//        }
        ImageIO.write(img, "JPG", new File("result//"+result+".jpg"));
        return result;
    }

    public static void downloadImage(){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(dowloadImgUrl);



        for(int i=0;i<10;i++){
            dowloadImgUrl=dowloadImgUrl+String.valueOf(Math.random());
            try {
                httpGet.setURI(new URI(dowloadImgUrl));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String picName="img"+File.separator+i+".png";
            try {
                CloseableHttpResponse response1 =httpclient.execute(httpGet);
                HttpEntity entity=response1.getEntity();
                if(entity!=null){
                    InputStream ipsm=entity.getContent();
                    File file = new File(picName);
                    try {
                        OutputStream fout = new FileOutputStream(file) {
                        };
                        int l = -1;
                        byte[] tmp = new byte[1024];
                        while ((l = ipsm.read(tmp)) != -1) {
                            fout.write(tmp, 0, l);
                            // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
                        }
                        fout.flush();
                        fout.close();
                    } finally {
                        // 关闭低层流。
                        ipsm.close();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public static  void  main(String[] args) throws IOException {

        ImageBase64 base=new ImageBase64();

        String fileName="img"+File.separator+"26.png";
        String imgStr=base.getImageBinary(fileName);




        String host = "http://ali-checkcode2.showapi.com";
        String path = "/checkcode";
        String method = "POST";
        String appcode = "4b03cee8bc6248a383fec8c465070582";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("convert_to_jpg", "1");
        bodys.put("img_base64", imgStr);
        bodys.put("typeId", "3040");

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        downloadImage();


     /*   File file=new File("img/imgStr.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outputStream=new FileOutputStream(file);
        outputStream.write(imgStr.getBytes());*/




    }


    public void recognitionAllImg(){

        try{
            Map<BufferedImage,String> bufferedImg=loadTrainData();
            for(BufferedImage img:bufferedImg.keySet()){
                if(img!=null) {
                    System.out.println("file Name is:" + bufferedImg.get(img) + " value is :" + recognitionImg(img));
                }

            }

        }catch (Exception ex){
            System.out.println(ex);
        }

    }
    public  static  String recognitionImg(BufferedImage image){

//        File imageFile = new File(fileName);
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setLanguage("zhengxin");
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        String result="";

        try {
             result = instance.doOCR(image);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public  static  String recognitionImg(String image){

        File imageFile = new File(image);
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setLanguage("zhengxin");
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        String result="";

        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

}
