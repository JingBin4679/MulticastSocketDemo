package demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class MulticastSocketDemo implements Runnable {
	// ���巢�����ݱ���Ŀ�ĵ�
	public static final int DEST_PORT = 30000;
	public static final String MULTICAST_IP = "230.0.0.1";
	public InetAddress multicastAddr = null;
	// ����ÿ�����ݱ�������СΪ4KB
	private static final int DATA_LEN = 4096;
	// ��������������ݵ��ֽ�����
	byte[] inBuff = new byte[DATA_LEN];
	// ��ָ�����ֽ����鴴��׼���������ݵ�DatagramPacket����
	private DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
	// ����һ�����ڷ��͵�DatagramPacket����
	private DatagramPacket outPacket = null;
	// ����һ��MulticastSocket����
	private MulticastSocket socket = null;

	public void init() throws IOException {
		try {
			// ����һ��MulticastSocket�����ָ���˿ڣ�ָ���˿�ΪDEST_PORT��
			socket = new MulticastSocket(DEST_PORT);
			//����һ���㲥��ַ
			multicastAddr = InetAddress.getByName(MULTICAST_IP);
			// ��MulticastSocket���뵽�㲥��ַ��
			socket.joinGroup(multicastAddr);
			// ʹ���ھ�������
			socket.setTimeToLive(1);
			// ���͵����ݱ����͵�����
			socket.setLoopbackMode(false);
			// ��ʼ�������õ�MulticastSocket��������һ������Ϊ0���ֽ�����
			outPacket = new DatagramPacket(new byte[0], 0,multicastAddr, DEST_PORT);
			// �����������ݵ��߳�
			new Thread(this).start();
			// ��������������
			Scanner scan = new Scanner(System.in);
			// ���ϵض�ȡ�������룬û������ʱ��������ȴ�����
			while (scan.hasNextLine()) {
				// �����������һ���ַ���ת�����ֽ�����
				byte[] buff = scan.nextLine().getBytes();
				// ���÷����õ�DatagramPacket�е��ֽ�����
				outPacket.setData(buff);
				// �������ݱ�
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
			// ��ȡSocket�е����ݣ����������ݷ���inPacket����װ���ֽ�������
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