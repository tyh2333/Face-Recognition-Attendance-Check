package com.example.IOT_Proj1.tools;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class toolsUnit {
    /**
     *      官网代码： 关于如何使用人脸识别接口
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             **/
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;  // @return assess_token
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
    /*
    *   通过设置 inJustDecodeBounds为true，获取到outHeight(图片原始高度)和
    * outWidth(图片的原始宽度)，然后计算一个inSampleSize(缩放值)，然后就可以取图片了，这里要注意的是，
    * inSampleSize 可能小于0，必须做判断。
    */

    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpt = new BitmapFactory.Options();
        newOpt.inJustDecodeBounds = true;
        // (1) 获取图片的宽高: Options.inJustDecodeBounds=true,这时候decode的bitmap为null
        // 只是把图片的宽高放在Options里:
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpt);
        newOpt.inJustDecodeBounds = false;
        int w = newOpt.outWidth;
        int h = newOpt.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f; //h = 800f
        float ww = 480f; //w = 480f
        // (2) 第二步就是设置合适的压缩比例inSampleSize:
        // 这时候获得合适的Bitmap. 由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int scale = 1;  //scale = 1表示不缩放
        // 情况1： 宽度大就根据宽度固定大小缩放
        if (w > h && w > ww) { scale = (int) (newOpt.outWidth / ww); }
        // 情况2： 高度高就根据宽度固定大小缩放
        else if (w < h && h > hh) { scale = (int) (newOpt.outHeight / hh); }
        if (scale <= 0) scale = 1;
        // inSampleSize = 2，缩略图的宽和高都是原图片的1/2，大小就为原大小的1/4。对于任何值< = 1的同样处置为1。
        newOpt.inSampleSize = scale; //设置缩放比例
        bitmap = BitmapFactory.decodeFile(srcPath, newOpt);
        return compressImage(bitmap);
    }
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        // （1）质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, BAOS);
        int options = 100;
        // （2）循环判断如果压缩后图片大于300kb则继续压缩
        while (BAOS.toByteArray().length / 1024 > 300) {
            BAOS.reset();//重置 BAOS 即清空
            // 这里压缩options%，把压缩后的数据存放到 BAOS 中
            image.compress(Bitmap.CompressFormat.JPEG, options, BAOS);
            options -= 10;//每次都减少10
        }
        // （3）把压缩后的数据 BAOS 存放到ByteArrayInputStream中
        ByteArrayInputStream inputs_Bitmap = new ByteArrayInputStream(BAOS.toByteArray());
        // （4）把 ByteArrayInputStream 数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(inputs_Bitmap, null, null);
        // （5）返回生成的 bit 图片
        return bitmap;
    }

    public static byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
