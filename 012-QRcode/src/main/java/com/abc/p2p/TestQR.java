package com.abc.p2p;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.sound.midi.Soundbank;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestQR
 * @Description 生成二维码测试
 * @author PowerNode
 * @Date 2020/11/27 11:40
 * @version 1.0
 */
public class TestQR {

    public static void main(String[] args) throws Exception {
        //设置字符集
        Map<EncodeHintType,Object> map= new HashMap<EncodeHintType, Object>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        //创建一个矩阵对象
        BitMatrix bitMatrix=new MultiFormatWriter().encode("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1259790059,411253384&fm=26&gp=0.jpg", BarcodeFormat.QR_CODE,200,200, map);
        //路径
        Path path= FileSystems.getDefault().getPath("D:\\course\\09-p2p\\资料","xx.jpg");

        //将矩阵对象转换为二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);
        System.out.println("生成成功");
    }
}
