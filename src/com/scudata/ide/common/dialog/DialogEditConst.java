package com.scudata.ide.common.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import com.scudata.cellset.datamodel.PgmNormalCell;
import com.scudata.common.DateFactory;
import com.scudata.common.MessageManager;
import com.scudata.common.StringUtils;
import com.scudata.dm.Env;
import com.scudata.dm.Param;
import com.scudata.dm.ParamList;
import com.scudata.dm.Sequence;
import com.scudata.dm.Table;
import com.scudata.ide.common.GC;
import com.scudata.ide.common.GM;
import com.scudata.ide.common.GV;
import com.scudata.ide.common.resources.IdeCommonMessage;
import com.scudata.ide.common.swing.AllPurposeEditor;
import com.scudata.ide.common.swing.AllPurposeRenderer;
import com.scudata.ide.common.swing.DateChooser;
import com.scudata.ide.common.swing.DatetimeChooser;
import com.scudata.ide.common.swing.JTableEx;
import com.scudata.ide.common.swing.VFlowLayout;
import com.scudata.util.Variant;

/**
 * �����༭
 *
 */
public class DialogEditConst extends DialogMaxmizable {
	private static final long serialVersionUID = 1L;

	/**
	 * ȷ�ϰ�ť
	 */
	private JButton jBOK = new JButton();
	/**
	 * ȡ����ť
	 */
	private JButton jBCancel = new JButton();

	/**
	 * ���ڸ�ʽ
	 */
	private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(
			Env.getDateFormat());

	/**
	 * �¼���ʽ
	 */
	private java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(
			Env.getDateTimeFormat());

	/**
	 * Common��Դ������
	 */
	private MessageManager mm = IdeCommonMessage.get();
	/** �ַ��� */
	private final String STR_STR = mm.getMessage("dialogeditconst.str");
	/** ���� */
	private final String STR_INT = mm.getMessage("dialogeditconst.int");
	/** ������ */
	private final String STR_DOUBLE = mm.getMessage("dialogeditconst.double");
	/** ���� */
	private final String STR_DATE = mm.getMessage("dialogeditconst.date");
	/** ����ʱ�� */
	private final String STR_DATE_TIME = mm
			.getMessage("dialogeditconst.datetime");
	/** ���� */
	private final String STR_SERIES = mm.getMessage("dialogeditconst.series");
	/** ��� */
	private final String STR_TABLE = mm.getMessage("dialogeditconst.table");
	/** ����ʽ */
	private final String STR_EXP = mm.getMessage("dialogeditconst.exp");

	/** ����� */
	private final byte COL_INDEX = 0;
	/** ������ */
	private final byte COL_NAME = 1;
	/** ������ */
	private final byte COL_KIND = 2;
	/** ֵ�� */
	private final byte COL_VALUE = 3;
	/** ���ö����� */
	private final byte COL_CONFIG = 4;
	/** ���ö������� */
	private final String STR_PARAM = "COL_CONFIG";
	/**
	 * ��������ؼ������,����,����,ֵ,COL_CONFIG
	 */
	private JTableEx tableConst = new JTableEx(
			mm.getMessage("dialogeditconst.tableconst") + STR_PARAM) {
		private static final long serialVersionUID = 1L;

		/**
		 * ����ֵ���¼�
		 */
		public void setValueAt(Object aValue, int row, int column) {
			if (!isItemDataChanged(row, column, aValue)) {
				return;
			}
			super.setValueAt(aValue, row, column);
			switch (column) {
			case COL_KIND:
				data.setValueAt(null, row, COL_VALUE);
				data.setValueAt(null, row, COL_CONFIG);
				acceptText();
				break;
			}
		}

		/**
		 * ˫���¼�
		 */
		public void doubleClicked(int xpos, int ypos, int row, int col,
				MouseEvent e) {
			switch (col) {
			case COL_NAME:
				GM.dialogEditTableText(tableConst, row, col);
				break;
			case COL_VALUE:
				Object val = data.getValueAt(row, COL_VALUE);
				byte kind = ((Byte) data.getValueAt(row, COL_KIND)).byteValue();
				Param p = new Param();
				if (StringUtils.isValidString(tableConst.data.getValueAt(row,
						COL_NAME))) {
					p.setName(tableConst.data.getValueAt(row, COL_NAME) == null ? ""
							: (String) tableConst.data
									.getValueAt(row, COL_NAME));
				}
				p.setKind(getParamKind(kind));
				p.setValue(val);
				acceptText();
				switch (kind) {
				case GC.KIND_STR:
				case GC.KIND_INT:
				case GC.KIND_DOUBLE:
				case GC.KIND_EXP:
					String title = p.getName();
					if (StringUtils.isValidString(title)) {
						title += " : ";
					}
					if (kind == GC.KIND_STR) {
						title += STR_STR;
					} else if (kind == GC.KIND_INT) {
						title += STR_INT;
					} else if (kind == GC.KIND_DOUBLE) {
						title += STR_DOUBLE;
					} else {
						title += STR_EXP;
					}
					DialogInputText dit = new DialogInputText(GV.appFrame, true);
					dit.setText(val == null ? null : (String) val);
					dit.setTitle(title);
					dit.setVisible(true);
					if (dit.getOption() == JOptionPane.OK_OPTION) {
						data.setValueAt(dit.getText(), row, COL_VALUE);
						acceptText();
					}
					break;
				case GC.KIND_DATE:
					title = p.getName();
					if (StringUtils.isValidString(title)) {
						title += " : ";
					}
					title += STR_DATE;
					DateChooser dc = new DateChooser(GV.appFrame, true);
					dc.setTitle(title);
					java.util.Calendar selectedCalendar = java.util.Calendar
							.getInstance();
					try {
						if (StringUtils.isValidString(data.getValueAt(row,
								COL_VALUE))) {
							selectedCalendar.setTime(dateFormatter
									.parse((String) data.getValueAt(row,
											COL_VALUE)));
						}
						dc.initDate(selectedCalendar);
					} catch (Exception x) {
					}
					GM.centerWindow(dc);
					dc.setVisible(true);
					if (dc.getSelectedDate() != null) {
						selectedCalendar = dc.getSelectedDate();
					} else {
						selectedCalendar = null;
					}
					if (selectedCalendar == null) {
						dc.dispose();
						return;
					}
					long time = selectedCalendar.getTimeInMillis();
					java.util.Date date = new java.sql.Date(time);
					data.setValueAt(dateFormatter.format(date), row, COL_VALUE);
					dc.dispose();
					break;
				case GC.KIND_DATE_TIME:
					title = p.getName();
					if (StringUtils.isValidString(title)) {
						title += " : ";
					}
					title += STR_DATE_TIME;
					DatetimeChooser dtc = new DatetimeChooser(GV.appFrame, true);
					dtc.setTitle(title);
					selectedCalendar = java.util.Calendar.getInstance();
					try {
						if (StringUtils.isValidString(data.getValueAt(row,
								COL_VALUE))) {
							selectedCalendar.setTime(timeFormatter
									.parse((String) data.getValueAt(row,
											COL_VALUE)));
						}
						dtc.initDate(selectedCalendar);
					} catch (Exception x) {
					}
					GM.centerWindow(dtc);
					dtc.setVisible(true);
					if (dtc.getSelectedDatetime() != null) {
						selectedCalendar = dtc.getSelectedDatetime();
					} else {
						selectedCalendar = null;
					}
					if (selectedCalendar == null) {
						dtc.dispose();
						return;
					}
					time = selectedCalendar.getTimeInMillis();
					java.sql.Time datetime = new java.sql.Time(time);
					data.setValueAt(timeFormatter.format(datetime), row,
							COL_VALUE);
					dtc.dispose();
					break;
				case GC.KIND_SERIES:
					DialogEditSeries des = new DialogEditSeries();
					des.setParam(p);
					title = p.getName();
					if (StringUtils.isValidString(title)) {
						title += " : ";
					}
					title += STR_SERIES;
					des.setTitle(title);
					des.setVisible(true);
					if (des.getOption() == JOptionPane.OK_OPTION) {
						p = des.getParam();
						tableConst.data
								.setValueAt(p.getValue(), row, COL_VALUE);
						acceptText();
					}
					break;
				case GC.KIND_TABLE:
					DialogEditTable det = new DialogEditTable(p);
					title = p.getName();
					if (StringUtils.isValidString(title)) {
						title += " : ";
					}
					title += STR_TABLE;
					det.setTitle(title);
					det.setVisible(true);
					if (det.getOption() == JOptionPane.OK_OPTION) {
						p = det.getParam();
						tableConst.data
								.setValueAt(p.getValue(), row, COL_VALUE);
						acceptText();
					}
					break;
				}
				break;
			}
		}
	};

	/**
	 * ���Ӱ�ť
	 */
	private JButton jBAdd = new JButton();

	/**
	 * ɾ����ť
	 */
	private JButton jBDel = new JButton();

	/**
	 * �����б�����
	 */
	private ParamList pl;

	/**
	 * �˳�ѡ��
	 */
	private int m_option = JOptionPane.CANCEL_OPTION;

	/**
	 * �Ѿ����ڵ�����
	 */
	private Vector<String> usedNames = new Vector<String>();

	/**
	 * ������������
	 */
	private Vector<String> otherNames;

	/**
	 * ���캯��
	 * 
	 * @param isGlobal
	 *            �Ƿ�ȫ�ֱ���
	 */
	public DialogEditConst(boolean isGlobal) {
		super(GV.appFrame, "�����༭", true);
		try {
			initUI();
			init(isGlobal);
			setSize(450, 300);
			GM.setDialogDefaultButton(this, jBOK, jBCancel);
			resetText(isGlobal);
		} catch (Exception ex) {
			GM.showException(ex);
		}
	}

	/**
	 * ����������Դ
	 * 
	 * @param isGlobal
	 */
	private void resetText(boolean isGlobal) {
		setTitle(isGlobal ? mm.getMessage("dialogeditconst.title") : mm
				.getMessage("dialogeditconst.title1")); // �����༭
		jBOK.setText(mm.getMessage("button.ok"));
		jBCancel.setText(mm.getMessage("button.cancel"));
		jBAdd.setText(mm.getMessage("button.add"));
		jBDel.setText(mm.getMessage("button.delete"));
	}

	/**
	 * ȡ�˳�ѡ��
	 * 
	 * @return
	 */
	public int getOption() {
		return m_option;
	}

	/**
	 * �����Ѿ����ڵ�����
	 * 
	 * @param usedNames
	 */
	public void setUsedNames(Vector<String> usedNames) {
		this.otherNames = usedNames;
	}

	/**
	 * ���ò����б�����
	 * 
	 * @param pl
	 */
	public void setParamList(ParamList pl) {
		if (pl == null) {
			return;
		}
		this.pl = pl;
		setParamList2Table(pl);
		ParamList otherList = new ParamList(); // Param.ARG ������
		if (pl != null) {
			pl.getAllVarParams(otherList);
			int count = otherList.count();
			for (int i = 0; i < count; i++) {
				usedNames.add(otherList.get(i).getName());
			}
		}
	}

	/**
	 * ���ò����б�������ؼ�
	 * 
	 * @param pl
	 */
	private void setParamList2Table(ParamList pl) {
		ParamList constList = new ParamList();
		pl.getAllConsts(constList);
		int count = constList.count();
		Param p;
		byte kind;
		Object val;
		int row;
		for (int i = 0; i < count; i++) {
			p = constList.get(i);
			row = tableConst.addRow();
			tableConst.data.setValueAt(p.getName(), row, COL_NAME);
			kind = getKind(p);
			tableConst.data.setValueAt(new Byte(kind), row, COL_KIND);
			val = p.getValue();
			switch (kind) {
			case GC.KIND_INT:
			case GC.KIND_DOUBLE:
			case GC.KIND_DATE:
				val = Variant.toString(val);
				break;
			case GC.KIND_DATE_TIME:
				val = timeFormatter.format((Date) val);
				break;
			}
			tableConst.data.setValueAt(val, row, COL_VALUE);
		}
	}

	/**
	 * ȡ�����б�
	 * 
	 * @return
	 */
	public ParamList getParamList() {
		return getParamList(true);
	}

	/**
	 * ȡ�����б�
	 * 
	 * @param containTsx
	 * @return
	 */
	private ParamList getParamList(boolean containTsx) {
		ParamList otherList = new ParamList();
		if (pl == null) {
			pl = new ParamList();
		} else if (containTsx) {
			pl.getAllVarParams(otherList);
		}
		ParamList newList = new ParamList();
		int count = tableConst.getRowCount();
		Param p;
		byte kind;
		Object val;
		for (int i = 0; i < count; i++) {
			p = new Param();
			p.setName((String) tableConst.data.getValueAt(i, COL_NAME));
			kind = ((Byte) tableConst.data.getValueAt(i, COL_KIND)).byteValue();
			p.setKind(getParamKind(kind));
			val = tableConst.data.getValueAt(i, COL_VALUE);
			switch (kind) {
			case GC.KIND_INT:
				p.setValue(new Integer(Integer.parseInt((String) val)));
				break;
			case GC.KIND_DOUBLE:
				p.setValue(new Double(Double.parseDouble((String) val)));
				break;
			case GC.KIND_DATE:
				try {
					p.setValue(DateFactory.parseDate((String) val));
				} catch (ParseException ex) {
				}
				break;
			case GC.KIND_DATE_TIME:
				try {
					p.setValue(new java.sql.Time(timeFormatter.parse(
							(String) val).getTime()));
				} catch (ParseException ex) {
				}
				break;
			case GC.KIND_SERIES:
				if (val instanceof String) {
					val = PgmNormalCell.parseConstValue((String) val);
				}
				p.setValue(val);
				break;
			default:
				p.setValue(val);
				break;
			}
			newList.add(p);
		}
		count = otherList.count();
		for (int i = 0; i < count; i++) {
			newList.add(otherList.get(i));
		}
		return newList;
	}

	/**
	 * ȡ��������
	 * 
	 * @param kind
	 * @return
	 */
	private byte getParamKind(byte kind) {
		switch (kind) {
		case GC.KIND_STR:
		case GC.KIND_INT:
		case GC.KIND_DOUBLE:
		case GC.KIND_DATE:
		case GC.KIND_DATE_TIME:
		case GC.KIND_SERIES:
		case GC.KIND_TABLE:
			return Param.CONST;
		}
		return -1;
	}

	/**
	 * ȡ����
	 * 
	 * @param p
	 * @return
	 */
	private byte getKind(Param p) {
		Object val = p.getValue();
		if (val instanceof Integer) {
			return GC.KIND_INT;
		} else if (val instanceof Double) {
			return GC.KIND_DOUBLE;
		} else if (val instanceof java.sql.Time) {
			return GC.KIND_DATE_TIME;
		} else if (val instanceof Date) {
			return GC.KIND_DATE;
		} else if (val instanceof Sequence) {
			Sequence s = (Sequence) val;
			if (s.isPmt()) {
				return GC.KIND_TABLE;
			}
			return GC.KIND_SERIES;
		} else if (val instanceof String) {
			if (p.getKind() == Param.CONST) {
				return GC.KIND_STR;
			}
		}
		return -1;
	}

	/**
	 * ��ʼ��
	 * 
	 * @param isGlobal
	 *            �Ƿ�ȫ�ֱ���
	 */
	private void init(boolean isGlobal) {
		tableConst.setIndexCol(COL_INDEX);
		tableConst.setRowHeight(20);
		Vector<Byte> code = new Vector<Byte>();
		code.add(new Byte(GC.KIND_STR));
		code.add(new Byte(GC.KIND_INT));
		code.add(new Byte(GC.KIND_DOUBLE));
		code.add(new Byte(GC.KIND_DATE));
		code.add(new Byte(GC.KIND_DATE_TIME));
		code.add(new Byte(GC.KIND_SERIES));
		code.add(new Byte(GC.KIND_TABLE));
		Vector<String> disp = new Vector<String>();
		disp.add(STR_STR);
		disp.add(STR_INT);
		disp.add(STR_DOUBLE);
		disp.add(STR_DATE);
		disp.add(STR_DATE_TIME);
		disp.add(STR_SERIES);
		disp.add(STR_TABLE);
		JComboBox combo = tableConst.setColumnDropDown(COL_KIND, code, disp);
		combo.setMaximumRowCount(10);
		tableConst.setColumnVisible(STR_PARAM, false);
		TableColumn tc = tableConst.getColumn(COL_VALUE);
		tc.setCellEditor(new AllPurposeEditor(new JTextField(), tableConst));
		tc.setCellRenderer(new AllPurposeRenderer());

		tableConst.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableConst.getTableHeader().setReorderingAllowed(false);
		tableConst.setColumnWidth(COL_NAME, 120);
		tableConst.setColumnWidth(COL_KIND, 86);
		tableConst.setColumnWidth(COL_VALUE, 120);

	}

	/**
	 * ��ʼ���ؼ�
	 * 
	 * @throws Exception
	 */
	private void initUI() throws Exception {
		JPanel jPanel2 = new JPanel();
		VFlowLayout vFlowLayout1 = new VFlowLayout();
		jPanel2.setLayout(vFlowLayout1);
		jBOK.setMnemonic('O');
		jBOK.setText("ȷ��(O)");
		jBOK.addActionListener(new DialogEditConst_jBOK_actionAdapter(this));
		jBCancel.setMnemonic('C');
		jBCancel.setText("ȡ��(C)");
		jBCancel.addActionListener(new DialogEditConst_jBCancel_actionAdapter(
				this));
		jBAdd.addActionListener(new DialogEditConst_jBAdd_actionAdapter(this));
		jBDel.addActionListener(new DialogEditConst_jBDel_actionAdapter(this));
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new DialogEditConst_this_windowAdapter(this));
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.getViewport().add(tableConst);
		JPanel jPanel3 = new JPanel();
		BorderLayout borderLayout1 = new BorderLayout();
		JButton jButton1 = new JButton();
		jButton1.setMaximumSize(new Dimension(450, 1));
		jButton1.setBorder(null);
		jPanel3.setLayout(borderLayout1);
		jBAdd.setMnemonic('A');
		jBAdd.setText("����(A)");
		jBDel.setMnemonic('D');
		jBDel.setText("ɾ��(D)");
		this.getContentPane().add(jPanel3, BorderLayout.CENTER);
		this.getContentPane().add(jPanel2, BorderLayout.EAST);
		jPanel2.add(jBOK, null);
		jPanel2.add(jBCancel, null);
		JPanel jPanel1 = new JPanel();
		jPanel2.add(jPanel1, null);
		jPanel2.add(jBAdd, null);
		jPanel2.add(jBDel, null);
		GridBagConstraints gbc = GM.getGBC(2, 1, true, true);
		gbc.gridwidth = 3;
		jPanel3.add(jScrollPane1, BorderLayout.CENTER);
		jPanel3.add(jButton1, BorderLayout.NORTH);
	}

	/**
	 * ���ڹر��¼�
	 * 
	 * @param e
	 */
	void this_windowClosing(WindowEvent e) {
		GM.setWindowDimension(this);
		dispose();
	}

	/**
	 * �������
	 * 
	 * @return
	 */
	private boolean checkData() {
		tableConst.acceptText();
		if (!tableConst
				.verifyColumnData(COL_NAME, mm.getMessage("public.name"))) { // ����
			return false;
		}
		int count = tableConst.getRowCount();
		byte kind;
		String name;
		Object val;
		for (int i = 0; i < count; i++) {
			name = (String) tableConst.data.getValueAt(i, COL_NAME);
			if (usedNames.contains(name)) {
				JOptionPane.showMessageDialog(GV.appFrame, mm.getMessage(
						"dialogeditconst.existname", i + 1 + "", name)); // ��{0}�в�������{1}�Ѿ����ڡ�
				return false;
			}
			kind = ((Byte) tableConst.data.getValueAt(i, COL_KIND)).byteValue();
			val = tableConst.data.getValueAt(i, COL_VALUE);
			if (val == null) {
				JOptionPane.showMessageDialog(GV.appFrame,
						mm.getMessage("dialogeditconst.emptyval", i + 1 + "")); // ��{0}�в���ֵΪ�ա�
				return false;
			}
			String strKind = "";
			String message = mm.getMessage("dialogeditconst.notvalid", i + 1
					+ ""); // ��{0}�в���ֵ����Ӧ��Ϊ��
			try {
				switch (kind) {
				case GC.KIND_INT:
					strKind = STR_INT;
					Integer.parseInt((String) val);
					break;
				case GC.KIND_DOUBLE:
					strKind = STR_DOUBLE;
					Double.parseDouble((String) val);
					break;
				case GC.KIND_DATE:
					strKind = STR_DATE;
					DateFactory.parseDate((String) val);
					break;
				case GC.KIND_SERIES:
					strKind = STR_SERIES;
					if (StringUtils.isValidString(val)) {
						val = PgmNormalCell.parseConstValue((String) val);
					}
					if (!(val instanceof Sequence)) {
						JOptionPane.showMessageDialog(GV.appFrame, message
								+ STR_SERIES); // ��{0}�в���ֵ����Ӧ��Ϊ������
						return false;
					}
					break;
				case GC.KIND_TABLE:
					if (!(val instanceof Table)) {
						JOptionPane.showMessageDialog(GV.appFrame, message
								+ STR_TABLE); // ��{0}�в���ֵ����Ӧ��Ϊ�����
						return false;
					}
					break;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(GV.appFrame, message + strKind);
				return false;
			}
		}
		return true;
	}

	/**
	 * ȷ�ϰ�ť�¼�
	 * 
	 * @param ae
	 */
	void jBOK_actionPerformed(ActionEvent ae) {
		if (!checkData()) {
			return;
		}
		m_option = JOptionPane.OK_OPTION;
		GM.setWindowDimension(this);
		dispose();
	}

	/**
	 * ȡ����ť�¼�
	 * 
	 * @param e
	 */
	void jBCancel_actionPerformed(ActionEvent e) {
		GM.setWindowDimension(this);
		dispose();
	}

	/**
	 * ���Ӱ�ť�¼�
	 * 
	 * @param e
	 */
	void jBAdd_actionPerformed(ActionEvent e) {
		int row = tableConst.addRow();
		tableConst.acceptText();
		Vector<String> names = new Vector<String>();
		for (int i = 0; i < tableConst.getRowCount(); i++) {
			if (tableConst.data.getValueAt(i, COL_NAME) != null) {
				names.add((String) tableConst.data.getValueAt(i, COL_NAME));
			}
		}
		names.addAll(otherNames);
		int index = 1;
		while (names.contains(GC.PRE_PARAM + index)) {
			index++;
		}
		tableConst.data.setValueAt(GC.PRE_PARAM + index, row, COL_NAME);
		tableConst.data.setValueAt(new Byte(GC.KIND_STR), row, COL_KIND);
	}

	/**
	 * ɾ����ť�¼�
	 * 
	 * @param e
	 */
	void jBDel_actionPerformed(ActionEvent e) {
		tableConst.deleteSelectedRows();
	}

}

class DialogEditConst_this_windowAdapter extends java.awt.event.WindowAdapter {
	DialogEditConst adaptee;

	DialogEditConst_this_windowAdapter(DialogEditConst adaptee) {
		this.adaptee = adaptee;
	}

	public void windowClosing(WindowEvent e) {
		adaptee.this_windowClosing(e);
	}
}

class DialogEditConst_jBOK_actionAdapter implements
		java.awt.event.ActionListener {
	DialogEditConst adaptee;

	DialogEditConst_jBOK_actionAdapter(DialogEditConst adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jBOK_actionPerformed(e);
	}
}

class DialogEditConst_jBCancel_actionAdapter implements
		java.awt.event.ActionListener {
	DialogEditConst adaptee;

	DialogEditConst_jBCancel_actionAdapter(DialogEditConst adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jBCancel_actionPerformed(e);
	}
}

class DialogEditConst_jBAdd_actionAdapter implements
		java.awt.event.ActionListener {
	DialogEditConst adaptee;

	DialogEditConst_jBAdd_actionAdapter(DialogEditConst adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jBAdd_actionPerformed(e);
	}
}

class DialogEditConst_jBDel_actionAdapter implements
		java.awt.event.ActionListener {
	DialogEditConst adaptee;

	DialogEditConst_jBDel_actionAdapter(DialogEditConst adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jBDel_actionPerformed(e);
	}
}