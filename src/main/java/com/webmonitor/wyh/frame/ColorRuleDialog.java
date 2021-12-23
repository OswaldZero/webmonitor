package com.webmonitor.wyh.frame;



import com.webmonitor.wyh.bean.FormatFilter;
import com.webmonitor.wyh.deal.FilterDeal;
import com.webmonitor.wyh.filter.ExpressionCheck;
import com.webmonitor.wyh.utils.ColorRule;
import com.webmonitor.wyh.utils.Properties;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ColorRuleDialog extends JDialog {

	private JTable filtersTable;
	private DefaultTableModel ftableModel;
	private static final String[] header = { "名称", "过滤器" };
	private FilterDeal fdeal;
	private List<FormatFilter> filters;
	private JScrollPane filterpane;
	private JButton btn_delete;
	private JButton btn_add;
	private JButton btn_save;
	private MainPage parent;
	private JPanel bgcolorPanel; // 更改背景色面板
	private JButton bgcolorButton;
	private JDialog thisDialog;
	private JButton btn_test;
	private ArrayList<Color> colors; // 所有过滤颜色

	public ColorRuleDialog(MainPage parent) {
		this.parent = parent;
		thisDialog = this;
		setTitle("着色规则");
		setModal(true);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		loadFilters();

		initFiltersTable();
		loadColor();

		btn_delete = new JButton("-");
		btn_add = new JButton("+");
		btn_save = new JButton("保存");
		btn_test = new JButton("语法测试");
		bgcolorPanel = new JPanel();

		bgcolorButton = new JButton("背景");
		bgcolorButton.setMargin(new Insets(0, 0, 0, 0));// 将边框外的上下左右空间设置为0
		bgcolorButton.setIconTextGap(0);// 将标签中显示的文本和图标之间的间隔量设置为0
		bgcolorButton.setBorderPainted(false);// 不打印边框
		bgcolorButton.setBorder(null);// 除去边框
		bgcolorButton.setFocusPainted(false);// 除去焦点的框
		bgcolorButton.setContentAreaFilled(false);// 除去默认的背景填充

		bgcolorPanel.add(bgcolorButton);
		bgcolorPanel.setBorder(new LineBorder(Color.BLACK));
		bgcolorPanel.setVisible(false);
		btn_delete.setEnabled(false);

		JPanel operatepanel = new JPanel();
		operatepanel.add(btn_add);
		operatepanel.add(btn_delete);
		operatepanel.add(btn_save);
		// operatepanel.add(btn_test);
		operatepanel.add(bgcolorPanel);
		add(operatepanel);

		setOnClickListener();

		setSize(600, 600);
		setVisible(true);

	}

	private void initFiltersTable() {
		ftableModel = new DefaultTableModel(new Object[0][0], header);
		filtersTable = new JTable(ftableModel);
		filtersTable.setRowHeight(20);
		filtersTable.setBorder(BorderFactory.createRaisedBevelBorder());
		filterpane = new JScrollPane(filtersTable);
		for (int i = 0; i < filters.size(); i++) {
			ftableModel.addRow(new Object[] { filters.get(i).getName(), filters.get(i).getGrammar() });
		}
		add(filterpane);

		filtersTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				btn_delete.setEnabled(true);
				bgcolorPanel.setVisible(true);
				int row = filtersTable.getSelectedRow();
				bgcolorPanel.setBackground(colors.get(row)); // 改变面板的背景色
			}

		});
	}

	private void loadColor() {

		int columnCount = filtersTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			filtersTable.getColumn(filtersTable.getColumnName(i)).setCellRenderer(new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					setBackground(ColorRule.colors[row]);
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			});
		}
	}

	private void setOnClickListener() {
		btn_delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = filtersTable.getSelectedRow();
				ftableModel.removeRow(row);
				filters.remove(row);
				colors.remove(row);
			}
		});

		btn_add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FormatFilter newFilter = new FormatFilter();
				newFilter.setName("请输入过滤器名称");
				newFilter.setGrammar("请输入过滤器语法");
				newFilter.setColor(ColorRule.defaultBgColor);
				ftableModel.addRow(new Object[] { newFilter.getName(), newFilter.getGrammar() });
				filters.add(newFilter);
				colors.add(newFilter.getColor());
			}
		});

		btn_save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveFilters();
			}
		});

		btn_test.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row;
				if ((row = filtersTable.getSelectedRow()) != -1) {

					// String expression = filters.get(row).getGrammar();
					String expression = (String) filtersTable.getValueAt(row, 1);
					int result = ExpressionCheck.checkFilterExpression(expression);
					if (result == -1) {
						showMessage("过滤器表达式有错误");
						return;
					} else if (result == -2) {
						showMessage("未知错误");
						return;
					}
					showMessage("表达式语法正确");
				}
			}
		});

		bgcolorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JColorChooser chooser = new JColorChooser(); // 实例化颜色选择器
				Color color = chooser.showDialog(thisDialog, "选取颜色", Color.lightGray); // 得到选择的颜色
				if (color == null) // 如果未选取
					color = Color.gray; // 则设置颜色为灰色
				bgcolorPanel.setBackground(color); // 改变面板的背景色
				int row = filtersTable.getSelectedRow();
				filters.get(row).setColor(color);
				colors.set(row, color);
			}
		});

		// 关闭Dialog时将filter信息存回文件
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				saveFilters();
				setVisible(false);
				dispose();
			}

		});
	}

	private void loadFilters() {
		fdeal = FilterDeal.newInstance(Properties.colorRulepath);
		filters = fdeal.loadFilters();
		colors = new ArrayList<Color>();
		for (FormatFilter filter : filters) {
			colors.add(filter.getColor());
		}
	}

	private void saveFilters() {
		filters = new LinkedList<FormatFilter>();
		for (int i = 0; i < filtersTable.getRowCount(); i++) {
			filters.add(new FormatFilter((String) filtersTable.getValueAt(i, 0), (String) filtersTable.getValueAt(i, 1),
					colors.get(i)));
		}
		// 更新主页着色规则
		parent.setColors(colors.toArray(new Color[colors.size()]));

		// 停止编辑，这样JTable才能保存编辑内容，否则存回的是原内容
		if (filtersTable.getCellEditor() != null) {
			filtersTable.getCellEditor().stopCellEditing();
		}
		fdeal.setFilters(filters);
		fdeal.saveFilters(filters);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
}
