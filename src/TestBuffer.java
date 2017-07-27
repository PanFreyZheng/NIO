import org.junit.Test;

import java.nio.*;

/**
 * @author :LoseMyself    pengfei.zheng@hand-china.com
 * @version :1.0
 * @description :缓冲区在NIO负责数据的存取,缓冲区就是数组.用于存储不同类型的数据
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 *
 * 1.缓冲区管理方式几乎一致,通过 allocate()获取缓冲区
 * 2.put()存入数据 get()获取数据
 * 3.4个核心属性:
 *   capacity:容量,表示缓冲区中最大存储数据的容量,一旦声明不可改变
 *   limit:界限,表示缓冲区中可以操作数据的大小.(limit后数据不能进行读写)
 *   position:位置,缓冲区中正在操作的数据位置   0<= mark <= position <= limit <= capacity
 *   mark:标记,记录当前position的位置,可通过reset()恢复到mark的位置
 * 4.直接缓冲区和非直接缓冲区
 *   非直接缓冲区:通过allocate()方法分配缓冲区,将缓冲区建立在JVM的内存中
 *   直接缓冲区:通过allocateDirect()方法分配直接缓冲区,将缓冲区建立在物理内存中.可以提高效率,有风险,当有大数据或者数据长期保存的时候使用
 * @date :2017/7/26 14:59
 */

public class TestBuffer {
    @Test
    public void test1(){
        //分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("=====allocate=====");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //利用put方法存数据到缓冲区中
        String str = "test";
        buf.put(str.getBytes());
        System.out.println("=====put=====");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //切换到读取数据的模式position变为0,limit存为position之前的值,即为查询其区间内的数据
        buf.flip();
        System.out.println("=====filp=====");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //get()获取数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,dst.length);
        System.out.println("===== get =====");
        System.out.println(new String(dst,0,dst.length));
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //可重复读数据 position再归0
        buf.rewind();
        System.out.println("===== rewind =====");
        //clear()清空缓冲区,但是缓冲区的数据依旧存在,只是被遗忘状态
        buf.clear();
        System.out.println((char)buf.get(1));
    }

    @Test
    public void test2(){
        String str = "asdfg";

        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());
        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println(new String(dst,0,2));
        System.out.println(buf.position());
        //标记当前位置position的值
        buf.mark();
        buf.get(dst,2,2);
        System.out.println(new String(dst,2,2));
        System.out.println(buf.position());
        //重置position的位置变为mark
        buf.reset();
        System.out.println(buf.position());
        //判断缓冲区中是否还有剩余数据
        if(buf.hasRemaining()){
            //剩下的数量
            System.out.println(buf.remaining());
        }
    }

    @Test
    public void test3(){
        // 在heap堆中开辟空间,所以是数组
//        ByteBuffer buf = new ByteBuffer().allocateDirect(1024);

//        System.out.println(buf.isDirect());
    }
}
