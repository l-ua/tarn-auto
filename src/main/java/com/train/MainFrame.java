package com.train;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.train.entity.Passenger;
import com.train.entity.TranInfo;
import com.train.enums.SeatEnum;
import com.train.service.OrderQueueWaitTime;
import com.train.service.TrainService;
import com.train.swing.*;
import com.train.util.CheckUtils;
import com.train.util.Images;
import com.train.util.Logger;
import com.train.util.MaskAdapter;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends BaseFrame {
    private final static Logger logger = new Logger();
    /**
     *
     */
    private static final long serialVersionUID = 3354878488469592363L;
    private JCheckBox autoQueryCheckbox;//是否自动查询余票
    private JCheckBox autoBuyTicket;//是否自动购票
    private JCheckBox canselAutoBuyTicket;//取消自动购票
    // "商务座", "特等座", "一等座",
    //       "二等座", "高级软卧 ", “动卧”"软卧", "硬卧", "软座", "硬座", "无座"
    private JCheckBox autoBuyShangWuZuo;//在自选席别的没有的情况下自动购商务座特等座
    private JCheckBox autoYiDengZuo;//在自选席别的没有的情况下自动购一等座
    private JCheckBox autoErDengZuo;//在自选席别的没有的情况下自动购er等座
    private JCheckBox autoGaoJiRuanWo;//在自选席别的没有的情况下自动购高级软卧
    private JCheckBox autoRuanWo;//在自选席别的没有的情况下自动购软卧
    private JCheckBox autoBuyDongWo;//在自选席别的没有的情况下自动购无座票
    private JCheckBox autoYingWo;//在自选席别的没有的情况下自动购硬卧
    private JCheckBox autoRuanZuo;//在自选席别的没有的情况下自动购软座
    private JCheckBox autoYingZuo;//在自选席别的没有的情况下自动购硬座
    private JCheckBox autoBuyWuZuo;//在自选席别的没有的情况下自动购无座票
    private JLabel seatOrderField; //用户选择的座位顺序
    private JTextField ticketNoOrder; //车次优先级
    private JSpinner delayTimeSpinner; //余票查询执行时间
    private JLabel exchangeLabel;
    private JTextField fromStationTelecode;
    private JLabel messageJLabel;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JLabel startDateJLabel;
    private JLabel passengerJLabel;
    private JScrollPane ticketDataJScrollPane;
    private JScrollPane passengerJScrollPane;
    private JPanel mainPanel;
    private JPanel passengerJPanel;
    private JPanel panel2;

    private JPanel panel3;
    private JPanel searchJPanel;
    private JPanel panel6;
    private JPanel ticketDataJPanel;
    private JTable passengerTable;
    private JButton queryButton;
    private JButton buyButton;
    private JTable ticketTable;
    private JTextField toStationTelecode;
    private JCheckBox trainClassArr1;
    private JCheckBox trainClassArr2;
    private JCheckBox trainClassArr3;
    private JCheckBox trainClassArr4;
    private JCheckBox trainClassArr5;
    private JCheckBox trainClassArr6;
    private JCheckBox trainClassArr7;
    private DatePickerTextField trainDate;
    private DefaultTableModel ticketTableModel;
    private DefaultTableModel passengerTableModel;
    private JSONArray ticketResult;
    private boolean hasTicket = false;
    public static JTextArea textArea = new JTextArea();
    private StringBuffer passengerTicketStr;
    private StringBuffer oldPassengerStr;
    // 车次优先级别
    private String ticketNoStr;
    private String seatType;
    private JLabel logJLabel;
    private JButton submitJButton;
    private JLabel xiBieDesc;
    private JTabbedPane jTabbedpane = new JTabbedPane();
    private Set<String> submitData;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private MaskAdapter maskPanel;
    private JSONObject sationMap;

    Map<String, TranInfo> code2TranMap;

    JComboBox comboBox;

    volatile Long count;


    volatile List<Object[]> rowDatas = new ArrayList<>();
    volatile List<TranInfo> tranInfos = new ArrayList<>();

    public MainFrame() {
        super();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        setIconImage(Toolkit.getDefaultToolkit().createImage(Images.getImage("logo.png")));
        initComponents();
        init();
        setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                try {
                    TrayIcon[] trayIcons = tray.getTrayIcons();
                    System.out.println(trayIcons.length);
                    if (trayIcons.length <= 0)
                        tray.add(trayIcon);
                    setVisible(false);
                } catch (AWTException e1) {
                    e1.printStackTrace();
                }
            }
//
        });
        if (SystemTray.isSupported()) {
            tray();
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (!prompt("确定退出系统?")) {
                return;
            }
            tray.remove(trayIcon);
            System.exit(0);
        }
        super.processWindowEvent(e);
    }

    private void tray() {
        // 获得本操作系统托盘的实例
        tray = SystemTray.getSystemTray();
        // 显示在托盘中的图标
        // 构造一个右键弹出式菜单
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(Images.getImage("logo.png")), "12306抢票系统");
        // 这句很重要，没有会导致图片显示不出来
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            boolean flag = true;

            public void mouseClicked(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    if (flag) {
                        setVisible(flag);
                        // 还原成原来的窗口，而不是显示在任务栏
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                        flag = !flag;
                    } else {
                        setVisible(flag);
                        flag = !flag;
                    }
                }
                if (e.isMetaDown()) {
                    if (flag) {
                        setVisible(flag);
                        // 还原成原来的窗口，而不是显示在任务栏
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                        flag = !flag;
                    } else {
                        setVisible(flag);
                        flag = !flag;
                    }
                }
            }
        });
    }

    private void initComponents() {
        // mainScrollPanel = new JScrollPane();
        mainPanel = new JPanel();
        passengerJPanel = new JPanel();
        panel2 = new JPanel();
        passengerJLabel = new JLabel();
        panel3 = new JPanel();
        passengerJScrollPane = new JScrollPane();
        passengerTable = new JTable();
        searchJPanel = new JPanel();
        fromStationTelecode = new JTextField();
        exchangeLabel = new JLabel();
        toStationTelecode = new JTextField();
        startDateJLabel = new JLabel();
        trainDate = new DatePickerTextField();
        queryButton = new JButton();
        buyButton = new JButton();
        canselAutoBuyTicket = new JCheckBox();
        canselAutoBuyTicket.setText("取消自动购买");
        panel6 = new JPanel();
        trainClassArr1 = new JCheckBox();
        trainClassArr2 = new JCheckBox();
        trainClassArr3 = new JCheckBox();
        trainClassArr4 = new JCheckBox();
        trainClassArr5 = new JCheckBox();
        trainClassArr6 = new JCheckBox();
        trainClassArr7 = new JCheckBox();
        autoQueryCheckbox = new JCheckBox();
        autoBuyTicket = new JCheckBox();
        ticketNoOrder = new JTextField();
        seatOrderField = new JLabel();
        jLabel15 = new JLabel();
        jLabel16 = new JLabel();
        delayTimeSpinner = new JSpinner();
        messageJLabel = new JLabel();
        ticketDataJPanel = new JPanel();
        ticketDataJScrollPane = new JScrollPane();
        ticketTable = new JTable();


        autoBuyShangWuZuo = new JCheckBox();//在自选席别的没有的情况下自动购商务座特等座
        autoYiDengZuo = new JCheckBox();//在自选席别的没有的情况下自动购一等座
        autoErDengZuo = new JCheckBox();//在自选席别的没有的情况下自动购er等座
        autoGaoJiRuanWo = new JCheckBox();//在自选席别的没有的情况下自动购高级软卧
        autoRuanWo = new JCheckBox();//在自选席别的没有的情况下自动购软卧
        autoYingWo = new JCheckBox();//在自选席别的没有的情况下自动购硬卧
        autoBuyDongWo = new JCheckBox();
        autoRuanZuo = new JCheckBox();//在自选席别的没有的情况下自动购软座
        autoYingZuo = new JCheckBox();//在自选席别的没有的情况下自动购硬座
        autoBuyWuZuo = new JCheckBox();//在自选席别的没有的情况下自动购无座票
        comboBox = new JComboBox();
        comboBox.addItem("席別优先");
        comboBox.addItem("车次优先");

//		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        passengerJPanel.setBorder(BorderFactory.createTitledBorder("常用联系人"));

        GroupLayout panel1Layout = new GroupLayout(passengerJPanel);
        passengerJPanel.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 1212, Short.MAX_VALUE));
        panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 37, Short.MAX_VALUE));
        passengerJPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //if (e.isMetaDown()) {
                toGetpassengerJson();
                // }
            }
        });

        passengerJLabel.setText("乘客信息");

        logJLabel = new JLabel("日志");

        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(passengerJLabel)
                                .addPreferredGap(ComponentPlacement.RELATED, 788, Short.MAX_VALUE)
                                .addComponent(logJLabel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                .addGap(489))
        );
        panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel2Layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(passengerJLabel)
                                .addComponent(logJLabel))
        );
        panel2.setLayout(panel2Layout);

        passengerJScrollPane.setViewportView(passengerTable);
        JScrollPane textAreaScrollPane = new JScrollPane();
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setTabSize(10);
        textArea.setLineWrap(true);
        textAreaScrollPane.setViewportView(textArea);
        GroupLayout panel3Layout = new GroupLayout(panel3);
        panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(passengerJScrollPane, GroupLayout.PREFERRED_SIZE, 821, GroupLayout.PREFERRED_SIZE)
                                .addGap(18)
                                .addComponent(textAreaScrollPane, GroupLayout.PREFERRED_SIZE, 449, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(82, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(panel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel3Layout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(passengerJScrollPane, GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                                        .addComponent(textAreaScrollPane, GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                                .addContainerGap())
        );
        panel3.setLayout(panel3Layout);

        fromStationTelecode.setForeground(new Color(153, 153, 153));
        fromStationTelecode.setText("请输入起点");


        toStationTelecode.setForeground(new Color(153, 153, 153));
        toStationTelecode.setText("请输入终点");

        ticketNoOrder.setForeground(new Color(153, 153, 153));
        ticketNoOrder.setText("输入车次(逗号隔开)");

        seatOrderField.setForeground(Color.RED);
        seatOrderField.setText("");

        autoBuyShangWuZuo.setText("自动购买商务座特等座");//在自选席别的没有的情况下自动购商务座特等座
        autoYiDengZuo.setText("自动购买一等座");//在自选席别的没有的情况下自动购一等座
        autoErDengZuo.setText("自动购买二等座");//在自选席别的没有的情况下自动购er等座
        autoGaoJiRuanWo.setText("自动购买高级软卧");//在自选席别的没有的情况下自动购高级软卧
        autoRuanWo.setText("自动购买软卧");//在自选席别的没有的情况下自动购软卧
        autoYingWo.setText("自动购买硬卧");//在自选席别的没有的情况下自动购硬卧
        autoBuyDongWo.setText("自动购买动卧");//在自选席别的没有的情况下自动购硬卧
        autoRuanZuo.setText("自动购买软座");//在自选席别的没有的情况下自动购软座
        autoYingZuo.setText("自动购买硬座");//在自选席别的没有的情况下自动购硬座
        autoBuyWuZuo.setText("自动购买无座");//在自选席别的没有的情况下自动购无座票

        startDateJLabel.setText("出发日期");
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        queryButton.setText("查询");
        buyButton.setText("自动购票");

        GroupLayout panel5Layout = new GroupLayout(searchJPanel);
        panel5Layout.setHorizontalGroup(
                panel5Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(fromStationTelecode, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(exchangeLabel)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(toStationTelecode, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                                .addGap(18)
                                .addComponent(startDateJLabel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(trainDate, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                .addGap(21)
                                .addComponent(queryButton, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                                .addGap(21)
                                .addComponent(ticketNoOrder, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                                .addGap(21)
                                .addGap(732))

        );
        panel5Layout.setVerticalGroup(
                panel5Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel5Layout.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(panel5Layout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(queryButton)
                                                .addComponent(toStationTelecode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(startDateJLabel)
                                                .addComponent(trainDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panel5Layout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(fromStationTelecode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(exchangeLabel))
                                        .addGap(4)
                                        .addGroup(panel5Layout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(ticketNoOrder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(exchangeLabel)

                                        )

                                )

                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        ));
        searchJPanel.setLayout(panel5Layout);

        trainClassArr1.setText("全部");

        trainClassArr2.setText("D-动车");

        trainClassArr3.setText("Z-直达");

        trainClassArr4.setText("T-特快");

        trainClassArr5.setText("K-快速");

        trainClassArr6.setText("其他");

        trainClassArr7.setText("GC-高铁/城际");
        trainClassArr1.setSelected(true);
        trainClassArr2.setSelected(true);
        trainClassArr3.setSelected(true);
        trainClassArr4.setSelected(true);
        trainClassArr5.setSelected(true);
        trainClassArr6.setSelected(true);
        trainClassArr7.setSelected(true);
        autoQueryCheckbox.setText("自动查询");
        autoBuyTicket.setText("自动购票");

        jLabel15.setText("每隔");

        jLabel16.setText("秒重新查询车票信息");

        delayTimeSpinner.setValue(3);

        messageJLabel.setText("注：“有”：票源充足 “无”：票已售完 “*”：未到起售时间 “--”：无此席别 “按住ctrl键实现多选”");
        messageJLabel.setForeground(Color.red);
        submitJButton = new JButton("提交");
        xiBieDesc = new JLabel("席别优先级：");
        autoBuyShangWuZuo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoBuyShangWuZuo.isSelected()) {
                    addSeat(SeatEnum.SHANG_WU_ZUO_TE_DENG_ZUO);
                } else {
                    removeSeat(SeatEnum.SHANG_WU_ZUO_TE_DENG_ZUO);
                }

            }
        });
        autoYiDengZuo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoYiDengZuo.isSelected()) {
                    addSeat(SeatEnum.YI_DENG_ZUO);
                } else {
                    removeSeat(SeatEnum.YI_DENG_ZUO);
                }

            }
        });
        autoErDengZuo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoErDengZuo.isSelected()) {
                    addSeat(SeatEnum.ER_DENG_ZUO);
                } else {
                    removeSeat(SeatEnum.ER_DENG_ZUO);
                }

            }
        });
        autoGaoJiRuanWo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoGaoJiRuanWo.isSelected()) {
                    addSeat(SeatEnum.GAO_JI_RUAN_WO);
                } else {
                    removeSeat(SeatEnum.GAO_JI_RUAN_WO);
                }

            }
        });
        autoRuanWo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoRuanWo.isSelected()) {
                    addSeat(SeatEnum.RUAN_WO);
                } else {
                    removeSeat(SeatEnum.RUAN_WO);
                }

            }
        });

        autoBuyDongWo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoBuyDongWo.isSelected()) {
                    addSeat(SeatEnum.DONG_WO);
                } else {
                    removeSeat(SeatEnum.DONG_WO);
                }

            }
        });
        autoYingWo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoYingWo.isSelected()) {
                    addSeat(SeatEnum.YING_WO);
                } else {
                    removeSeat(SeatEnum.YING_WO);
                }

            }
        });
        canselAutoBuyTicket.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (canselAutoBuyTicket.isSelected()) {
                    TrainService.logger.info("取消自动购买车票");
                    TrainService.logger.info("取消自动购买车票");
                    TrainService.logger.info("取消自动购买车票");
                    TrainService.logger.info("取消自动购买车票");
                    TrainService.logger.info("取消自动购买车票");
                }
            }
        });
        autoRuanZuo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoRuanZuo.isSelected()) {
                    addSeat(SeatEnum.RUAN_ZUO);
                } else {
                    removeSeat(SeatEnum.RUAN_ZUO);
                }

            }
        });
        autoYingZuo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoYingZuo.isSelected()) {
                    addSeat(SeatEnum.YING_ZUO);
                } else {
                    removeSeat(SeatEnum.YING_ZUO);
                }

            }
        });
        autoBuyWuZuo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoBuyWuZuo.isSelected()) {
                    addSeat(SeatEnum.WU_ZUO);
                } else {
                    removeSeat(SeatEnum.WU_ZUO);
                }
            }
        });
        submitJButton.addActionListener(e -> {
            int[] rows = ticketTable.getSelectedRows();
            if (rows.length <= 0) {
                alert("请选择车次");
                return;
            }
            getPassengersParameters();
            String string = oldPassengerStr.toString();
            if (CheckUtils.isNull(string)) {
                alert("请选择乘客");
                return;
            }
            submitJButton.setEnabled(false);
            submitData = new HashSet<String>();
            for (Integer integer : rows) {
                submitData.add(ticketTable.getValueAt(integer, 1).toString());
                toSubmutOrderRequest(integer);
            }
            submitJButton.setEnabled(true);
        });

        GroupLayout panel6Layout = new GroupLayout(panel6);
        panel6Layout.setHorizontalGroup(
                panel6Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel6Layout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(panel6Layout.createSequentialGroup()
                                                .addComponent(autoQueryCheckbox)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel15)
                                                .addGap(9)
                                                .addComponent(delayTimeSpinner, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(jLabel16)
                                                .addGap(230)
                                                .addComponent(submitJButton))
                                        .addGroup(panel6Layout.createSequentialGroup()
                                                //.addComponent(autoBuyTicket)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(autoBuyShangWuZuo).addGap(9)
                                                .addComponent(autoYiDengZuo).addGap(9)
                                                .addComponent(autoErDengZuo).addGap(9)
                                                .addComponent(autoGaoJiRuanWo).addGap(9)
                                                .addComponent(autoRuanWo).addGap(9)
                                                .addComponent(autoBuyDongWo).addGap(9)
                                                .addComponent(autoYingWo).addGap(9)
                                                .addComponent(autoRuanZuo).addGap(9)
                                                .addComponent(autoYingZuo).addGap(9)
                                                .addComponent(autoBuyWuZuo).addGap(9)
                                                .addComponent(comboBox).addGap(9)
                                                .addComponent(buyButton).addGap(9)

                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGap(20))

                                        .addGroup(panel6Layout.createSequentialGroup()
                                                .addComponent(xiBieDesc).addGap(9)
                                                .addComponent(seatOrderField).
                                                        addComponent(canselAutoBuyTicket))
                                        .addGroup(panel6Layout.createSequentialGroup()
                                                .addComponent(trainClassArr1)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(trainClassArr7)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(trainClassArr2)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(trainClassArr3)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(trainClassArr4)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(trainClassArr5)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(trainClassArr6)
                                                .addGap(98)
                                                .addComponent(messageJLabel)))
                                .addContainerGap(387, Short.MAX_VALUE))
        );
        panel6Layout.setVerticalGroup(
                panel6Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel6Layout.createSequentialGroup()
                                .addGroup(panel6Layout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(trainClassArr1)
                                        .addComponent(messageJLabel)
                                        .addComponent(trainClassArr7)
                                        .addComponent(trainClassArr2)
                                        .addComponent(trainClassArr3)
                                        .addComponent(trainClassArr4)
                                        .addComponent(trainClassArr5)
                                        .addComponent(trainClassArr6))
                                .addGap(4)
                                .addGroup(panel6Layout.createParallelGroup(Alignment.CENTER)
                                        .addComponent(autoQueryCheckbox)
                                        .addComponent(jLabel15)
                                        .addComponent(delayTimeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16)
                                        .addComponent(submitJButton))
                                .addGap(4)
                                .addGroup(panel6Layout.createParallelGroup(Alignment.CENTER)
                                        //.addComponent(autoBuyTicket).addGap(9)
                                        .addComponent(autoBuyShangWuZuo).addGap(9)
                                        .addComponent(autoYiDengZuo).addGap(9)
                                        .addComponent(autoErDengZuo).addGap(9)
                                        .addComponent(autoGaoJiRuanWo).addGap(9)
                                        .addComponent(autoRuanWo).addGap(9)
                                        .addComponent(autoBuyDongWo).addGap(9)
                                        .addComponent(autoYingWo).addGap(9)
                                        .addComponent(autoRuanZuo).addGap(9)
                                        .addComponent(autoYingZuo).addGap(9)
                                        .addComponent(autoBuyWuZuo).addGap(9)
                                        .addComponent(comboBox).addGap(9)
                                        .addComponent(buyButton).addGap(9))
                                .addGroup(panel6Layout.createParallelGroup(Alignment.CENTER)
                                        .addComponent(xiBieDesc).addGap(9)
                                        .addComponent(seatOrderField).addGap(9).addComponent(canselAutoBuyTicket))
                                .addContainerGap(20, Short.MAX_VALUE))
        );
        panel6.setLayout(panel6Layout);

        ticketDataJScrollPane.setViewportView(ticketTable);

        GroupLayout panel7Layout = new GroupLayout(ticketDataJPanel);
        panel7Layout.setHorizontalGroup(
                panel7Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(ticketDataJScrollPane, GroupLayout.DEFAULT_SIZE, 1338, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panel7Layout.setVerticalGroup(
                panel7Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(panel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(ticketDataJScrollPane, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(43, Short.MAX_VALUE))
        );
        ticketDataJPanel.setLayout(panel7Layout);

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(1)
                                .addComponent(ticketDataJPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel6, GroupLayout.PREFERRED_SIZE, 1339, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(20, Short.MAX_VALUE))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(1)
                                .addComponent(searchJPanel, GroupLayout.PREFERRED_SIZE, 1359, Short.MAX_VALUE))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(1)
                                .addGroup(mainPanelLayout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(panel3, 0, 0, Short.MAX_VALUE)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(passengerJPanel, GroupLayout.PREFERRED_SIZE, 1349, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addComponent(panel2, 0, 0, Short.MAX_VALUE)))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(passengerJPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(2)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(2)
                                .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(searchJPanel, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(ticketDataJPanel, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
                                .addGap(426))
        );
        mainPanel.setLayout(mainPanelLayout);
        maskPanel = MaskAdapter.getMaskpanel(mainPanel, false);
        jTabbedpane.addTab("购票页面", maskPanel);
        jTabbedpane.addTab("订单管理", new OrderPanel());
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jTabbedpane,
                GroupLayout.DEFAULT_SIZE, 1215, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jTabbedpane,
                GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE));
        // pack();
    }

    private void removeSeat(SeatEnum seatEnum) {
        if (CheckUtils.isNull(seatOrderField.getText())) {
            return;
        }
        if (seatOrderField.getText().indexOf("," + seatEnum.getName()) > -1) {
            seatOrderField.setText(seatOrderField.getText().replace("," + seatEnum.getName(), ""));
        }
        if (seatOrderField.getText().indexOf(seatEnum.getName() + ",") > -1) {
            seatOrderField.setText(seatOrderField.getText().replace(seatEnum.getName() + ",", ""));
        }
        if (seatOrderField.getText().indexOf(seatEnum.getName()) > -1) {
            seatOrderField.setText(seatOrderField.getText().replace(seatEnum.getName(), ""));
        }
    }

    private void addSeat(SeatEnum seatEnum) {
        if (CheckUtils.isNull(seatOrderField.getText())) {
            seatOrderField.setText(seatEnum.getName());
            return;
        }
        if (seatOrderField.getText().indexOf(seatEnum.getName()) > -1) {
            return;
        }
        seatOrderField.setText(seatOrderField.getText() + "," + seatEnum.getName());
    }


    public void init() {
        setTitle("12306 抢票助手");
        try {
            exchangeLabel.setIcon(new ImageIcon(Images.getImage("exchange.jpg")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        exchangeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exchangeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String temp1 = fromStationTelecode.getText();
                String temp2 = toStationTelecode.getText();
                if (temp1.equals("请输入起点")) {
                    temp1 = "请输入终点";
                    toStationTelecode.setForeground(new Color(153, 153, 153));
                } else {
                    toStationTelecode.setForeground(new Color(0, 0, 0));
                }
                if (temp2.equals("请输入终点")) {
                    temp2 = "请输入起点";
                    fromStationTelecode.setForeground(new Color(153, 153, 153));
                } else {
                    fromStationTelecode.setForeground(new Color(0, 0, 0));
                }
                fromStationTelecode.setText(temp2);
                toStationTelecode.setText(temp1);
            }
        });
        AutoComplete.setupAutoComplete(fromStationTelecode, "请输入起点");
        AutoComplete.setupAutoComplete(toStationTelecode, "请输入终点");
        trainClassArr1.addActionListener(trainClassArrListener);
        trainClassArr2.addActionListener(trainClassArrListener);
        trainClassArr3.addActionListener(trainClassArrListener);
        trainClassArr4.addActionListener(trainClassArrListener);
        trainClassArr5.addActionListener(trainClassArrListener);
        trainClassArr6.addActionListener(trainClassArrListener);
        trainClassArr7.addActionListener(trainClassArrListener);
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryLeftTicket();
            }
        });

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long sleepTime = 1000L;
                canselAutoBuyTicket.setSelected(false);
                count = 1l;
                new Thread() {
                    @Override
                    public void run() {
                        while (!canselAutoBuyTicket.isSelected()) {
                            autoBuyTicket();
                            try {
                                TrainService.logger.info("====休息 " + sleepTime/1000 + " 秒后再次抢票===");
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e1) {

                            }
                        }
                    }
                }.start();


            }
        });
        trainDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        ticketTableModel = new DefaultTableModel(null, new String[]{"选择", "车次", "发站", "到站", "历时", "商务座特等座", "一等座",
                "二等座", "高级软卧 ", "软卧", "动卧", "硬卧", "软座", "硬座", "无座", "其他", ""}) {
            private static final long serialVersionUID = -4944805197374427410L;

            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                }
                return false;
            }
        };
        ticketTable.setModel(ticketTableModel);
        ticketTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ticketTable.setRowHeight(25);
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();//单元格渲染器
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        ticketTable.setDefaultRenderer(Object.class, tcr);//设置渲染器
        ticketTable.setRowSelectionAllowed(true);
        ticketTable.getColumnModel().getColumn(16).setMaxWidth(-1);
        RowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(ticketTableModel);
        ticketTable.setRowSorter(sorter);
        TableCellRenderer cellRenderer = new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JCheckBox ck = new JCheckBox();
                ck.setText((row + 1) + "");
                ck.setSelected(isSelected);
                return ck;
            }
        };
        ticketTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        passengerTableModel = new DefaultTableModel(null, new String[]{"席别", "票种", "姓名", "证件类型", "证件号码", "手机号码"}) {
            private static final long serialVersionUID = -7640954343136822571L;

            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                }
                return false;
            }
        };
        passengerTable.getTableHeader().setReorderingAllowed(false);
        passengerTable.setModel(passengerTableModel);
        passengerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        passengerTable.setRowHeight(20);
        JComboBox<KeyValue> seatTypeCode = new JComboBox<KeyValue>(new DefaultComboBoxModel<KeyValue>(new KeyValue[]
                {new KeyValue("商务座特等座", "9"), new KeyValue("一等座", "M"),
                        new KeyValue("二等座", "O"), new KeyValue("高级软卧", "6"), new KeyValue("软卧", "4"),
                        new KeyValue("动卧", "5"),
                        new KeyValue("硬卧", "3"), new KeyValue("软座", "2"), new KeyValue("硬座", "1")}));
        passengerTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(seatTypeCode));
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (autoQueryCheckbox.isSelected()) {
                        queryLeftTicket();
                    }
                    int delay = Integer.parseInt(delayTimeSpinner.getValue().toString());
                    if (delay <= 0) {
                        delay = 3;
                    }
                    try {
                        Thread.sleep(delay * 1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();
        toGetpassengerJson();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(1024, 480);
        this.getContentPane().setBackground(Color.white);
    }

    private void autoBuyTicket() {

        logJLabel.updateUI();
        TrainService.logger.info("开启第" + ++count + "次抢票任务");
        if (CheckUtils.isNull(seatOrderField.getText())) {
            tip("请选择自动购买席别");
            return;
        }
        // 查票
        for (int i = 0; i < passengerJPanel.getComponents().length; i++) {
            passengerJPanel.getComponent(i).setEnabled(true);
        }
        passengerTable.setEnabled(true);
        if (MainFrame.this.fromStationTelecode.getText().equals("请输入起点")) {
            tip("请输入起点");
            MainFrame.this.fromStationTelecode.requestFocus();
            return;
        }
        if (MainFrame.this.toStationTelecode.getText().equals("请输入终点")) {
            tip("请输入终点");
            MainFrame.this.toStationTelecode.requestFocus();
            return;
        }
        maskPanel.setMask(true);
        String train_date = MainFrame.this.trainDate.getText();
        String from_station_telecode = MainFrame.this.fromStationTelecode.getText().split("-")[1];
        String to_station_telecode = MainFrame.this.toStationTelecode.getText().split("-")[1];
        JSONObject jsonObject = TrainService.queryTrain(from_station_telecode, to_station_telecode, train_date);
        sationMap = jsonObject.containsKey("map") ? jsonObject.getJSONObject("map") : new JSONObject();
        ticketResult = jsonObject.containsKey("result") ? jsonObject.getJSONArray("result") : new JSONArray();
        mapperTranInfo();
        //  sortNoTickResult();
        addTicketTableModel();

        maskPanel.setMask(false);
        // 优先车次，然后坐席
        ticketNoStr = ticketNoOrder.getText();
        Integer colIndex = getCanBuyTickect();
        if (null == colIndex) {
            return;
        }
        getPassengersParameters();
        String string = oldPassengerStr.toString();
        if (CheckUtils.isNull(string)) {
            alert("请选择乘客");
            return;
        }
        submitJButton.setEnabled(false);
        submitData = new HashSet<String>();
        submitData.add(ticketTable.getValueAt(colIndex, 1).toString());
        toSubmutOrderRequest(colIndex);
        submitJButton.setEnabled(true);
    }

    private Integer findBySeatFirst() {
        String[] seatNames = seatOrderField.getText().trim().split(",");

        for (String seatName : seatNames) {
            for (TranInfo tranInfo : tranInfos) {
                if (tranInfo.isHaveTicket(seatName, tranInfo)) {
                    seatType = SeatEnum.getEnumByName(seatName).getCode();
                    return tranInfo.getIndex();
                }
            }
        }

        return null;
    }

    private Integer getCanBuyTickect() {
        if (CheckUtils.isNull(ticketNoOrder.getText())) {
            tip("车次优先,车次不能为空，多个车次之间用逗号隔开");
            return null;
        }
        String[] tranCodes = ticketNoOrder.getText().trim().split(",");

        for (String tranCode : tranCodes) {
            if (null != code2TranMap.get(tranCode)) {
                if (CheckUtils.isNull(seatOrderField.getText())) {
                    return code2TranMap.get(tranCode).getIndex();
                } else {
                    seatType = findSeatType(code2TranMap.get(tranCode));
                    if (CheckUtils.isNull(seatType)) {
                        TrainService.logger.info("用户所选的席别没有余票");
                        return null;
                    }
                    return code2TranMap.get(tranCode).getIndex();
                }
            }
        }
        TrainService.logger.info("自选的车次中，不含有余票");
        TrainService.logger.info("结束第" + count + "次抢票任务");

        return null;


    }

    private String findSeatType(TranInfo tranInfo) {
        String[] seatNames = seatOrderField.getText().trim().split(",");
        for (String seatName : seatNames) {
            if (tranInfo.isHaveTicket(seatName, tranInfo)) {
                return SeatEnum.getEnumByName(seatName).getCode();
            }
        }

        return null;
    }

    private void sortNoTickResult() {
        if (orderByTran()) {
            // 車次優先
            if (CheckUtils.isNull(ticketNoStr)) {
                sortBySeat(false);
            } else {
                sortByTran(false);
            }
        } else {
            // 默認  系別優先
            sortBySeat(true);
        }

    }

    private void mapperTranInfo() {
        tranInfos = new ArrayList<>();
        TranInfo tranInfo;
        for (int i = 0; i < ticketResult.size(); i++) {
            String data = ticketResult.getString(i);
            String[] datas = data.split("\\|");
            String stationTrainCode = datas[3];
            String trainCode = stationTrainCode.substring(0, 1);
            tranInfo = new TranInfo();
            tranInfo.setIndex(i);
            tranInfo.setSelect(false);
            tranInfo.setTranCode(datas[3]);
            tranInfo.setFromName(sationMap.getString(datas[6]) + "(" + datas[8] + ")");
            tranInfo.setToName(sationMap.getString(datas[7]) + "(" + datas[9] + ")");
            tranInfo.setCostTime(datas[10]);
            tranInfo.setShangWuZuoTeDengZuo(datas[32].equals("") ? "--" : datas[32]);
            tranInfo.setYiDengZuo(datas[31].equals("") ? "--" : datas[31]);
            tranInfo.setErDengZuo(datas[30].equals("") ? "--" : datas[30]);
            tranInfo.setGaoJiRuanWo(datas[21].equals("") ? "--" : datas[21]);

            tranInfo.setRuanWo(datas[23].equals("") ? "--" : datas[23]);
            tranInfo.setDongWo(datas[33].equals("") ? "--" : datas[33]);
            tranInfo.setYingWo(datas[28].equals("") ? "--" : datas[28]);
            tranInfo.setRuanZuo(datas[24].equals("") ? "--" : datas[24]);
            tranInfo.setYingZuo(datas[29].equals("") ? "--" : datas[29]);
            tranInfo.setWuZuo(datas[26].equals("") ? "--" : datas[26]);
            StringBuffer sb = new StringBuffer();
            sb.append(datas[0]).append(",");
            sb.append(datas[12]).append(",");
            sb.append(datas[15]).append(",");
            sb.append(datas[2]).append(",");
            sb.append(datas[6]).append(",");
            sb.append(datas[7]).append(",");
            sb.append(sationMap.getString(datas[6])).append(",");
            sb.append(sationMap.getString(datas[7])).append(",");
            sb.append(stationTrainCode);
            tranInfo.setTranInfoMsg(sb.toString());
            System.out.println(i + " tran info  " + tranInfo.toString());
            tranInfos.add(tranInfo);
        }

        code2TranMap = tranInfos.stream().parallel().collect(Collectors.toMap(k -> k.getTranCode(), v -> v));
    }


    private boolean orderByTran() {
        return comboBox.getSelectedIndex() == 1;
    }

    private void sortBySeat(boolean changeType) {
        if (noSelect() && changeType) {
            sortByTran(false);
        } else {

        }

    }

    private void sortByTran(boolean changeType) {
        if (CheckUtils.isNull(ticketNoStr)) {
            return;
        }
        List<String> tickets = Arrays.asList(ticketNoStr.toUpperCase().trim().split(","));
        Map<String, TranInfo> code2TranMap = tranInfos.stream().collect(Collectors.toMap(k -> k.getTranCode().toUpperCase(), v -> v));

        List<TranInfo> temps = new ArrayList<>();
        // 把车次优先的放在前面
        for (String ticket : tickets) {
            if (CheckUtils.isNotNull(code2TranMap.get(ticket.toUpperCase()))) {
                temps.add(code2TranMap.get(ticket.toUpperCase()));
            }
        }
        // 其他的按照默认顺序排序
        for (TranInfo ticket : tranInfos) {
            if (tickets.contains(ticket.getTranCode().toUpperCase())) {
                continue;
            }
            temps.add(ticket);
        }
        // 车次没有勾选
        if (noSelect()) {
            tranInfos = temps;
        }

    }

    private boolean noSelect() {

        List<String> zuoweis = new ArrayList<>();
        boolean isSelect = false;
        if (autoBuyShangWuZuo.isSelected()) {
            isSelect = true;
            zuoweis.add("9");
        }

        if (autoYiDengZuo.isSelected()) {
            isSelect = true;
            zuoweis.add("M");
            zuoweis.add("M");
        }
        if (autoErDengZuo.isSelected()) {
            isSelect = true;
            zuoweis.add("O");
        }
        if (autoGaoJiRuanWo.isSelected()) {
            isSelect = true;
            zuoweis.add("6");
        }
        if (autoRuanWo.isSelected()) {
            isSelect = true;
            zuoweis.add("4");
        }
        if (autoYingWo.isSelected()) {
            isSelect = true;
            zuoweis.add("3");
        }
        if (autoRuanZuo.isSelected()) {
            isSelect = true;
            zuoweis.add("2");
        }
        if (autoYingZuo.isSelected()) {
            isSelect = true;
            zuoweis.add("1");
        }
        if (autoBuyWuZuo.isSelected()) {
            isSelect = true;
            zuoweis.add("0");
        }

        return isSelect;
    }

    public void toGetpassengerJson() {
        new Thread() {
            {
                setPriority(MAX_PRIORITY);
            }

            public void run() {
                passengerJPanel.removeAll();
                passengerJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
                List<Passenger> passengers = TrainService.passengers();
                for (int i = 0; i < passengers.size(); i++) {
                    Passenger passenger = passengers.get(i);
                    JCheckBox c = new JCheckBox(passenger.getPassenger_name());
                    c.addActionListener(new PassengerCheckboxListener(passenger, MainFrame.this));
                    passengerJPanel.add(c);
                }
                passengerJPanel.updateUI();
            }

            ;
        }.start();
    }

    public void delPassengerRow(Passenger passenger) {
        int rowCount = passengerTableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String passengerName = passengerTableModel.getValueAt(i, 2).toString();
            if (passengerName.equals(passenger.getPassenger_name())) {
                passengerTableModel.removeRow(i);
            }
        }
    }

    public void addPassengerRow(Passenger passenger) {
        int rowCount = passengerTableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object valueAt = passengerTableModel.getValueAt(i, 2);
            if (valueAt != null) {
                String passengerName = valueAt.toString();
                if (passengerName.equals(passenger.getPassenger_name())) {
                    return;
                }
            }
        }
        if (rowCount < 5) {
            passengerTableModel.addRow(new Object[]{new KeyValue("硬座", "1"), passenger.getPassenger_type_name(), passenger.getPassenger_name(), new KeyValue(passenger.getPassenger_id_type_name(), passenger.getPassenger_id_type_code()),
                    passenger.getPassenger_id_no(), passenger.getMobile_no() + passenger.getPhone_no()});
        }
    }

    public ActionListener trainClassArrListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Boolean flag = false;
            JCheckBox[] obj = null;
            if (obj == null)
                obj = new JCheckBox[]{trainClassArr2, trainClassArr3, trainClassArr4, trainClassArr5, trainClassArr6, trainClassArr7};
            if ("全部".equals(e.getActionCommand())) {
                flag = trainClassArr1.isSelected();
                for (JCheckBox object : obj) {
                    object.setSelected(flag);
                }
                flag = false;
            }
            for (JCheckBox object : obj) {
                boolean selected = object.isSelected();
                if (!selected)
                    flag = true;

            }
            trainClassArr1.setSelected(!flag);
            addTicketTableModel();
        }
    };

    private String getTrainClass() {
        String trainClass = "";
        if (MainFrame.this.trainClassArr1.isSelected()) {
            trainClass += "QB#";
        }
        if (MainFrame.this.trainClassArr7.isSelected()) {
            trainClass += "GC#";
        }
        if (MainFrame.this.trainClassArr2.isSelected()) {
            trainClass += "D#";
        }
        if (MainFrame.this.trainClassArr3.isSelected()) {
            trainClass += "Z#";
        }
        if (MainFrame.this.trainClassArr4.isSelected()) {
            trainClass += "T#";
        }
        if (MainFrame.this.trainClassArr5.isSelected()) {
            trainClass += "K#";
        }
        if (MainFrame.this.trainClassArr6.isSelected()) {
            trainClass += "QT#";
        }
        return trainClass;
    }

    public void addTicketTableModel() {
        DefaultTableModel defaultTableModel = ((DefaultTableModel) MainFrame.this.ticketTable.getModel());
        for (int i = 0; i < defaultTableModel.getRowCount(); ) {
            defaultTableModel.removeRow(i);
        }
        String trainClass = getTrainClass();
        System.out.println("共查询出" + ticketResult.size());
        rowDatas = new ArrayList<>();
        Object[] rowData = new Object[17];
        // tranInfos = new ArrayList<>();
        TranInfo tranInfo = null;
        for (int i = 0; i < ticketResult.size(); i++) {
            String data = ticketResult.getString(i);
            String[] datas = data.split("\\|");

            String stationTrainCode = datas[3];
            String trainCode = stationTrainCode.substring(0, 1);
            tranInfo = new TranInfo();
            if (trainClass.contains(trainCode) || (trainClassArr6.isSelected() && (trainCode.equals("Y") || trainCode.matches("\\d+")))) {
                rowData = new Object[17];
               /* tranInfo.setSelect(false);
                tranInfo.setTranCode(datas[3]);
                tranInfo.setFromName(sationMap.getString(datas[6]) + "(" + datas[8] + ")");
                tranInfo.setToName(sationMap.getString(datas[7]) + "(" + datas[9] + ")");
                tranInfo.setCostTime(datas[10]);
                tranInfo.setShangWuZuoTeDengZuo(datas[32].equals("") ? "--" : datas[32]);
                tranInfo.setYiDengZuo(datas[31].equals("") ? "--" : datas[31]);
                tranInfo.setErDengZuo(datas[30].equals("") ? "--" : datas[30]);
                tranInfo.setGaoJiRuanWo(datas[21].equals("") ? "--" : datas[21]);

                tranInfo.setRuanWo(datas[23].equals("") ? "--" : datas[23]);
                tranInfo.setDongWo(datas[33].equals("") ? "--" : datas[33]);
                tranInfo.setYingWo(datas[28].equals("") ? "--" : datas[28]);
                tranInfo.setRuanZuo(datas[24].equals("") ? "--" : datas[24]);
                tranInfo.setYingZuo(datas[29].equals("") ? "--" : datas[29]);
                tranInfo.setWuZuo(datas[26].equals("") ? "--" : datas[26]);*/
                StringBuffer sb = new StringBuffer();
                sb.append(datas[0]).append(",");
                sb.append(datas[12]).append(",");
                sb.append(datas[15]).append(",");
                sb.append(datas[2]).append(",");
                sb.append(datas[6]).append(",");
                sb.append(datas[7]).append(",");
                sb.append(sationMap.getString(datas[6])).append(",");
                sb.append(sationMap.getString(datas[7])).append(",");
                sb.append(stationTrainCode);
                tranInfo.setTranInfoMsg(sb.toString());
                // System.out.println(i + " tran info  " + tranInfo.toString());
                //  tranInfos.add(tranInfo);

                rowData[0] = new Boolean(false);
                rowData[1] = datas[3];
                rowData[2] = sationMap.getString(datas[6]) + "(" + datas[8] + ")";
                rowData[3] = sationMap.getString(datas[7]) + "(" + datas[9] + ")";
                rowData[4] = datas[10];
                // 商务座特等座
                rowData[5] = datas[32].equals("") ? "--" : datas[32];
                // 一等座
                rowData[6] = datas[31].equals("") ? "--" : datas[31];
                // 二等座
                rowData[7] = datas[30].equals("") ? "--" : datas[30];
                // 高级软卧
                rowData[8] = datas[21].equals("") ? "--" : datas[21];
                // 软卧
                rowData[9] = datas[23].equals("") ? "--" : datas[23];
                // 动卧
                rowData[10] = datas[33].equals("") ? "--" : datas[33];
                //  硬卧
                rowData[11] = datas[28].equals("") ? "--" : datas[28];
                // 软座
                rowData[12] = datas[24].equals("") ? "--" : datas[24];
                // 硬座
                rowData[13] = datas[29].equals("") ? "--" : datas[29];
                // 无座
                rowData[14] = datas[26].equals("") ? "--" : datas[26];
                // 其他
                rowData[15] = datas[22].equals("") ? "--" : datas[22];
                rowData[16] = sb.toString();
                rowDatas.add(rowData);
                defaultTableModel.addRow(rowData);
                if ("Y".equals(datas[11]) && !"".equals(datas[0])) {
                    hasTicket = true;
                }
            }
        }
    }

    public void queryLeftTicket() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < passengerJPanel.getComponents().length; i++) {
                    passengerJPanel.getComponent(i).setEnabled(true);
                }
                passengerTable.setEnabled(true);
                if (MainFrame.this.fromStationTelecode.getText().equals("请输入起点")) {
                    tip("请输入起点");
                    MainFrame.this.fromStationTelecode.requestFocus();
                    return;
                }
                if (MainFrame.this.toStationTelecode.getText().equals("请输入终点")) {
                    tip("请输入终点");
                    MainFrame.this.toStationTelecode.requestFocus();
                    return;
                }
                maskPanel.setMask(true);
                String train_date = MainFrame.this.trainDate.getText();
                String from_station_telecode = MainFrame.this.fromStationTelecode.getText().split("-")[1];
                String to_station_telecode = MainFrame.this.toStationTelecode.getText().split("-")[1];
                JSONObject jsonObject = TrainService.queryTrain(from_station_telecode, to_station_telecode, train_date);
                sationMap = jsonObject.containsKey("map") ? jsonObject.getJSONObject("map") : new JSONObject();
                ticketResult = jsonObject.containsKey("result") ? jsonObject.getJSONArray("result") : new JSONArray();
                addTicketTableModel();
                if (autoQueryCheckbox.isSelected() && hasTicket) {
                    autoQueryCheckbox.setSelected(false);
                    tip("有票了~~", System.currentTimeMillis());
                }
                maskPanel.setMask(false);
            }
        }.start();
    }

    public void getPassengersParameters() {
        passengerTicketStr = new StringBuffer();
        oldPassengerStr = new StringBuffer();
        for (int i = 0; i < passengerTableModel.getRowCount(); i++) {
            KeyValue seatTypes = (KeyValue) passengerTableModel.getValueAt(i, 0);
            String name = passengerTableModel.getValueAt(i, 2).toString();
            KeyValue idCardType = (KeyValue) passengerTableModel.getValueAt(i, 3);
            String idCard = passengerTableModel.getValueAt(i, 4).toString();
            String mobileno = passengerTableModel.getValueAt(i, 5).toString();
            if (null == seatType) {
                seatType = seatTypes.getValue();
            }
            if (i != 0) {
                passengerTicketStr.append("_");
            }
            passengerTicketStr.append(seatType).append(",0,1,").append(name).append(",")
                    .append(idCardType.getValue()).append(",")
                    .append(idCard).append(",").append(mobileno).append(",N");
            oldPassengerStr.append(name).append(",")
                    .append(idCardType.getValue()).append(",")
                    .append(idCard).append(",").append("1_");
        }
    }

    public void toSubmutOrderRequest(final int row) {
        if (hasTicket) {
            autoQueryCheckbox.setSelected(false);
            for (int i = 0; i < passengerJPanel.getComponents().length; i++) {
                passengerJPanel.getComponent(i).setEnabled(false);
            }
            new Thread() {
                @Override
                public void run() {
                    try {
                        String train_date = MainFrame.this.trainDate.getText();
                        String station_train_code = ticketTableModel.getValueAt(row, 0).toString();
                        String selectStr = ticketTableModel.getValueAt(row, 16).toString();
                        String[] selectStr_arr = selectStr.split(",");
                        String secretStr = selectStr_arr[0];
                        String location_code = selectStr_arr[2];
                        String train_no = selectStr_arr[3];
                        String from_station_telecode = selectStr_arr[4];
                        String to_station_telecode = selectStr_arr[5];
                        String from_station_name = selectStr_arr[6];
                        String to_station_name = selectStr_arr[7];
                        JSONObject submitOrderRequest = TrainService.submitOrderRequest(secretStr, train_date, from_station_name, to_station_name);
                        if (!submitOrderRequest.getBoolean("status") || !"[]".equals(submitOrderRequest.containsKey("messages") ? submitOrderRequest.getString("messages") : "[]")) {
                            alert(submitOrderRequest.getString("messages"));
                            return;
                        }
                        String checkOrderInfo = TrainService.checkOrderInfo("", oldPassengerStr.toString(), passengerTicketStr.toString(),
                                submitOrderRequest.getString("globalRepeatSubmitToken"), train_date, train_no, station_train_code, seatType,
                                from_station_telecode, to_station_telecode, submitOrderRequest.getString("leftTicketStr"), submitOrderRequest.getString("key_check_isChange"), location_code);
                        try {
                            JSONObject json = JSONObject.parseObject(checkOrderInfo);
                            if (json.getBoolean("status")) {
                                new OrderQueueWaitTime(submitOrderRequest.getString("globalRepeatSubmitToken")).start();
                            }
                        } catch (Exception e) {
                            alert(checkOrderInfo);
                        }
                    } catch (Exception e) {

                    }
                }
            }.start();
        }
    }
}