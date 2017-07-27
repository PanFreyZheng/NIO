import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author :LoseMyself    pengfei.zheng@hand-china.com
 * @version :1.0
 * @description :
 * DMA
 * 通道单个开辟的cpu用于处理IO流
 *
 * 1.用于源节点与目标节点的连接,在Java NIO中负责缓冲区中的数据传输,Channel本身不存储数据,因此需要配合缓冲区进行传输.
 * 2.通道的主要实现类
 *   java.nio.channels.Channel接口
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *
 * 3.获取通道
 *   a.Java针对支持通道的类提供了getChannel()方法
 *          本地IO:
 *          FileInputStream/FileOutputStream
 *          RandomAccessFile
 *
 *          网络IO:
 *          Socket
 *          ServerSocket
 *          DatagramSocket
 *
 *   b.在JDK1.7中的NIO.2 针对各个通道提供了静态方法open();
 *                       的Files工具类的newByteChannel();
 * @date :201/7/27 10:08
 */
public class Channel {

    //利用通道完成文件复制
    @Test
    public void test1(){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");

            //获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            // 分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 将通道中的数据存入缓冲区中
            while(inChannel.read(buf) != -1){
                buf.flip();//切换读取数据的模式
                // 将缓冲区中的护具写入通道中
                outChannel.write(buf);
                buf.clear();//清空缓冲区
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if( outChannel !=null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inChannel != null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //利用直接缓冲区(内存映射文件)完成问价你的肤质
    @Test
    public void test2(){
        long start = System.currentTimeMillis();
        try {
            FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
                                            //Create 存在不存在均会复制, Create_New存在会报错
            FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW);

            //内存映射文件 作用和allocateDirect()工厂方法一样
            MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY,0,inChannel.size());
            MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE,0,inChannel.size());
            //直接对缓冲区进行数据的读写操作
            byte[]dst = new  byte[inMappedBuf.limit()];
            inMappedBuf.get(dst);
            outMappedBuf.get(dst);

            inChannel.close();
            outChannel.close();

            long end = System.currentTimeMillis();
            System.out.println("wasted:"+(end-start));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
