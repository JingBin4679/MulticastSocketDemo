package demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class MulticastSocketDemo implements Runnable {
	// 定义发送数据报的目的地
	public static final int DEST_PORT = 30000;
	public static final String MULTICAST_IP = "230.0.0.1";
	public InetAddress multicastAddr = null;
	// 定义每个数据报的最大大小为4KB
	private static final int DATA_LEN = 4096;
	// 定义接收网络数据的字节数组
	byte[] inBuff = new byte[DATA_LEN];
	// 以指定的字节数组创建准备接收数据的DatagramPacket对象
	private DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
	// 定义一个用于发送的DatagramPacket对象
	private DatagramPacket outPacket = null;
	// 定义一个MulticastSocket对象
	private MulticastSocket socket = null;

	public void init() throws IOException {
		try {
			// 创建一个MulticastSocket，必須指定端口，指定端口为DEST_PORT，
			socket = new MulticastSocket(DEST_PORT);
			//创建一个广播地址
			multicastAddr = InetAddress.getByName(MULTICAST_IP);
			// 将MulticastSocket加入到广播地址中
			socket.joinGroup(multicastAddr);
			// 使用在局域网中
			socket.setTimeToLive(1);
			// 发送的数据报回送到自身
			socket.setLoopbackMode(false);
			// 初始化发送用的MulticastSocket，它包含一个长度为0的字节数组
			outPacket = new DatagramPacket(new byte[0], 0,multicastAddr, DEST_PORT);
			// 创建接收数据的线程
			new Thread(this).start();
			// 创建键盘输入流
			Scanner scan = new Scanner(System.in);
			// 不断地读取键盘输入，没有数据时会堵塞，等待输入
			while (scan.hasNextLine()) {
				// 将键盘输入的一行字符串转换成字节数组
				byte[] buff = scan.nextLine().getBytes();
				// 设置发送用的DatagramPacket中的字节数据
				outPacket.setData(buff);
				// 发送数据报
				socket.send(outPacket);
			}
		} finally {
			socket.close();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// 读取Socket中的数据，读到的数据放在inPacket所封装的字节数组中
			while (true) {
				socket.receive(inPacket);
				System.out.println(new String(inBuff, 0, inPacket.getLength()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(socket !=null){
				try {
					socket.leaveGroup(multicastAddr);
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		new MulticastSocketDemo().init();
	}

}