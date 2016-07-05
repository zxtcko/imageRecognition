package cn.edu.dhu.figures;

import android.util.Base64;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by X on 2016/5/1.
 */
public class WebServicer {
    /// webservice 设置
    private String wsNameSpace = "http://www.figures.com/webservices/";
    private String wsUrl       = Setting.getInstance().m_ServerHome + "FigureService.asmx";
    private static final String TAG = "WebServicer";

    public boolean uploadImage(String fileName,boolean bFront){
        try{
            String uploadBuffer = file2Base64Str(fileName);
            if (uploadBuffer==null)
                return false;
            //String methodName = "FileUploadImage";
            Log.i(TAG, "start");
            //调用webservice
            if (!connectWebService(uploadBuffer,bFront)) {
                Log.i(TAG, "上传图片"+fileName+"失败");
                return false;
            }
            else {
                Log.i(TAG, "上传图片"+fileName+"成功");
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //将文件转为Base64编码
    private String file2Base64Str(String srcUrl){
        try
        {
            // String srcUrl =PhotoName;
            FileInputStream fis = new FileInputStream(srcUrl);
            ByteArrayOutputStream baos =new ByteArrayOutputStream();

            byte[] buffer =new byte[8192];
            int count =0;
            while((count = fis.read(buffer)) >=0){
                baos.write(buffer,0, count);
            }
            String uploadBuffer =new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));

            fis.close();
            //进行Base64编码
            return uploadBuffer;
        }
        catch
                (Exception e){
            e.printStackTrace();
        }
        //return soapObject;
        return null;
    }

    //连接webservice,上传文件BASE64 str
    private    boolean connectWebService(String uploadBuffer,boolean bfront) throws IOException
    {
        // 调用的方法名称
        String methodName = "UploadImageFile";
        // SOAP Action
        String soapAction = wsNameSpace + methodName;

        SoapObject soapObject =new SoapObject(wsNameSpace, methodName);
        soapObject.addProperty("imageStr", uploadBuffer);
        soapObject.addProperty("bFront",bfront);
        //参数2  图片字符串
        SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soapObject);
        envelope.bodyOut = soapObject;
        envelope.dotNet =true;
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;

        HttpTransportSE httpTranstation =new HttpTransportSE(wsUrl);
        try  {
            httpTranstation.call(soapAction, envelope);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.i("WebServicer", "调用WebService上传图片异常！");
            return false;
        }
    }

    //远程测量
    public boolean startRemoteMeasure(float fHeight,float fWeight) {
        // 命名空间
        // String wsNameSpace = "http://www.figures.com/webservices/";
        // 调用的方法名称
        String methodName = "StartTraceImage";
        // EndPoint
        //String wsUrl = "http://10.199.15.197/FigureService.asmx";
        // SOAP Action
        String soapAction = wsNameSpace + methodName;

        //以下就是 调用过程了，不明白的话 请看相关webservice文档
        SoapObject soapObject = new SoapObject(wsNameSpace, methodName);
        soapObject.addProperty("fHeight", Float.toString(fHeight));  //参数1   图片名
        soapObject.addProperty("fWeight", Float.toString(fWeight));   //参数2  图片字符串

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soapObject);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        @SuppressWarnings("deprecation")
        AndroidHttpTransport httpTranstation = new AndroidHttpTransport(wsUrl);
        try {
            httpTranstation.call(soapAction, envelope);
            Object result = envelope.getResponse();
            Log.i(TAG,"connectWebService"+ result.toString());

            if(result.toString()==Boolean.toString(true))
                return true;
            else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"connectWebService" + e.toString());
            return false;
        }
    }
}
