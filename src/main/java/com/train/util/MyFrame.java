package com.train.util;

/**
 * @author ：陶伟基 ，微博：http://weibo.com/taoandtao
 * @date ：2012/12/10
 * @place：广州大学华软软件学院
 */
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
public class MyFrame extends javax.swing.JFrame {
    public static void main(String[] args) {
        MyFrame f = new MyFrame();
    }

    JLabel title;
    JLabel label_account;
    JLabel label_password;
    JTextField tf;
    JPasswordField psf;
    JRadioButton rb1;
    JRadioButton rb2;

    JButton bt1;
    JButton bt2;

    public MyFrame() {
        this.setVisible(true);
        this.setSize(250, 220);
        this.setVisible(true);
        this.setLocation(400, 200);

        title = new JLabel("华软BBS快捷登陆");
        label_account = new JLabel("账号：");
        label_password = new JLabel("密码：");
        tf = new JTextField();
        psf = new JPasswordField();
        rb1 = new JRadioButton("记住密码");
        rb2 = new JRadioButton("自动登陆");
        bt1 = new JButton("登陆");
        // 为指定的 Container 创建 GroupLayout
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        //创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGap(5);//添加间隔
        hGroup.addGroup(layout.createParallelGroup().addComponent(label_account)
                .addComponent(label_password));
        hGroup.addGap(5);
        hGroup.addGroup(layout.createParallelGroup().addComponent(title)
                .addComponent(psf).addComponent(rb1).addComponent(rb2)
                .addComponent(tf).addComponent(bt1));
        hGroup.addGap(5);
        layout.setHorizontalGroup(hGroup);
        //创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(title));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(label_account)
                .addComponent(tf));
        vGroup.addGap(5);
        vGroup.addGroup(layout.createParallelGroup().addComponent(label_password)
                .addComponent(psf));
        vGroup.addGroup(layout.createParallelGroup().addComponent(rb1));

        vGroup.addGroup(layout.createParallelGroup().addComponent(rb2));
        vGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING)
                .addComponent(bt1));
        vGroup.addGap(10);
        //设置垂直组
        layout.setVerticalGroup(vGroup);
    }
}
