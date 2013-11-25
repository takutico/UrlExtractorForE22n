package es.typ.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import es.typ.extractor.Extractor;
import es.typ.extractor.FilterBean;
import es.typ.extractor.UrlBean;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;

import javax.swing.JTabbedPane;

public class FramePrincipal extends javax.swing.JFrame {

	private JPanel contentPane;
	private JTextField txtHttp;
	public static final JTextArea taLog = new JTextArea();
	private JTextField txtFiltradoUrl;
	private JTextField txtFiltradoClass;
	private JTextArea taFilterCode_0;
	private JTextArea taFilterCode_1;
	private JTextArea taFilterCode_2;
	private JTextArea taFilterCode_3;
	private JTextField taScript;
	public static JTextArea taUrlDescartadas = new JTextArea();
	private JTextField txtPrecio;
	public static JTextArea taTotales = new JTextArea();
	private JTextField txtInsertByFilterUrl;
	private FilterBean filterBean = new FilterBean();
	public static FramePrincipal frame;
	public static JPanel pnlScript;
	private static Extractor extractor;
	
	// CheckBoxs
	private JCheckBox cbShowBodyText;
	private JCheckBox cbOnlyThisUrl;
	private JCheckBox chckbxSelect = new JCheckBox("Contar texto de Select (Combo)");
	private JCheckBox chckbxMetas = new JCheckBox("Metas");
	private JCheckBox chckbxTitle = new JCheckBox("Title");
	private JCheckBox cbContarExcluidos = new JCheckBox("Contar 1 vez");
	private JCheckBox cbIsScript;
	// Tablas
	public static ExtractorTableModel modeloTblResult = new ExtractorTableModel();
	private static JTable tblResult = new JTable(modeloTblResult);
	public static ExtractorTableModel modeloTblResultSinContenidoDuplicado = new ExtractorTableModel();
	private static JTable tblSinContenidoDuplicado = new JTable(modeloTblResultSinContenidoDuplicado);
	public static JLabel lblImg = new JLabel("");
	private JTextField txtDescripcion;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FramePrincipal frame = new FramePrincipal();
					// Se establece el tamaño de la pantalla al 75% y se centra
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setSize((int) (screenSize.getWidth() * 0.75f),
			                (int) (screenSize.getHeight() * 0.75f));
					frame.setLocationRelativeTo(null);
					
					frame.setTitle("Analizador URL - Easy");
					frame.setVisible(true);
					FramePrincipal.frame = frame;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FramePrincipal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pnlTop = new JPanel();
		contentPane.add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		txtHttp = new JTextField();
		txtHttp.setText("http://");
		txtHttp.setToolTipText("Ejemplo: http://www.miweb.com");
		pnlTop.add(txtHttp, BorderLayout.CENTER);
		txtHttp.setColumns(10);
		
		JPanel pnlBtn = new JPanel();
		pnlTop.add(pnlBtn, BorderLayout.EAST);
		pnlBtn.setLayout(new BorderLayout(0, 0));
		
		JButton btnAnalizar = new JButton("Analizar");
		pnlBtn.add(btnAnalizar, BorderLayout.CENTER);
		
		JButton btnStopAnalisis = new JButton("Stop");
		btnStopAnalisis.setVisible(false);
		btnStopAnalisis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extractor.cancel(true);
				System.out.println("se paran todos los procesos");
			}
		});
		pnlBtn.add(btnStopAnalisis, BorderLayout.EAST);
		
		cbOnlyThisUrl = new JCheckBox("Only");
		pnlBtn.add(cbOnlyThisUrl, BorderLayout.WEST);
		
		JPanel panel_8 = new JPanel();
		pnlTop.add(panel_8, BorderLayout.WEST);
				
		JPanel pnlBotton = new JPanel();
		contentPane.add(pnlBotton, BorderLayout.SOUTH);
		
		JPanel pnlLeft = new JPanel();
		contentPane.add(pnlLeft, BorderLayout.WEST);
		
		JPanel pnlRight = new JPanel();
		pnlRight.setPreferredSize(new Dimension(400, 10));
		pnlRight.setMinimumSize(new Dimension(100, 10));
		contentPane.add(pnlRight, BorderLayout.EAST);
		pnlRight.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(10, 170));
		pnlRight.add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		panel_3.add(taTotales, BorderLayout.CENTER);
		
		JButton btnCatlcTotal = new JButton("Calcular total");
		panel_3.add(btnCatlcTotal, BorderLayout.SOUTH);
		
		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5, BorderLayout.NORTH);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_2 = new JLabel("Precio por palabra:");
		panel_5.add(lblNewLabel_2, BorderLayout.WEST);
		
		txtPrecio = new JTextField();
		txtPrecio.setText("0.0");
		panel_5.add(txtPrecio);
		txtPrecio.setColumns(10);
		
		JPanel pnlImg = new JPanel();
		pnlImg.setPreferredSize(new Dimension(60, 26));
		panel_5.add(pnlImg, BorderLayout.EAST);
		
		lblImg.setIcon(new ImageIcon(this.getClass().getResource("check.png")));
		pnlImg.add(lblImg);
		
		JPanel panel_10 = new JPanel();
		panel_10.setPreferredSize(new Dimension(10, 4));
		panel_10.setMinimumSize(new Dimension(10, 2));
		panel_5.add(panel_10, BorderLayout.SOUTH);
		lblImg.setVisible(false);
		
		// Listener para cuando se ha pulsado el boton de calcular
		btnCatlcTotal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterBean.setPrecio(Float.parseFloat(txtPrecio.getText()));
				Extractor.CalcularTotales();
			}
		});
		
		JPanel panel_4 = new JPanel();
		pnlRight.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel_4.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblFiltradoUrl = new JLabel("Filtrado url:");
		GridBagConstraints gbc_lblFiltradoUrl = new GridBagConstraints();
		gbc_lblFiltradoUrl.insets = new Insets(0, 0, 5, 0);
		gbc_lblFiltradoUrl.gridx = 0;
		gbc_lblFiltradoUrl.gridy = 0;
		panel.add(lblFiltradoUrl, gbc_lblFiltradoUrl);
		
		txtFiltradoUrl = new JTextField();
		GridBagConstraints gbc_txtFiltradoUrl = new GridBagConstraints();
		gbc_txtFiltradoUrl.insets = new Insets(0, 0, 5, 0);
		gbc_txtFiltradoUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFiltradoUrl.gridx = 0;
		gbc_txtFiltradoUrl.gridy = 1;
		panel.add(txtFiltradoUrl, gbc_txtFiltradoUrl);
		txtFiltradoUrl.setColumns(10);
		
		GridBagConstraints gbc_chckbxSelect = new GridBagConstraints();
		gbc_chckbxSelect.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxSelect.gridx = 0;
		gbc_chckbxSelect.gridy = 7;
		chckbxSelect.setVisible(false);
		
		cbShowBodyText = new JCheckBox("Mostrar textos (Body)");
		cbShowBodyText.setVisible(false);
		
		txtFiltradoClass = new JTextField();
		txtFiltradoClass.setVisible(false);
		
		JLabel lblFiltadoClass = new JLabel("Filtado class");
		lblFiltadoClass.setVisible(false);
		
		JLabel lblInsertarPorFiltrado = new JLabel("Insertar por filtrado url:");
		GridBagConstraints gbc_lblInsertarPorFiltrado = new GridBagConstraints();
		gbc_lblInsertarPorFiltrado.insets = new Insets(0, 0, 5, 0);
		gbc_lblInsertarPorFiltrado.gridx = 0;
		gbc_lblInsertarPorFiltrado.gridy = 2;
		panel.add(lblInsertarPorFiltrado, gbc_lblInsertarPorFiltrado);
		
		txtInsertByFilterUrl = new JTextField();
		GridBagConstraints gbc_txtInsertByFilterUrl = new GridBagConstraints();
		gbc_txtInsertByFilterUrl.insets = new Insets(0, 0, 5, 0);
		gbc_txtInsertByFilterUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtInsertByFilterUrl.gridx = 0;
		gbc_txtInsertByFilterUrl.gridy = 3;
		panel.add(txtInsertByFilterUrl, gbc_txtInsertByFilterUrl);
		txtInsertByFilterUrl.setColumns(10);
		GridBagConstraints gbc_lblFiltadoClass = new GridBagConstraints();
		gbc_lblFiltadoClass.insets = new Insets(0, 0, 5, 0);
		gbc_lblFiltadoClass.gridx = 0;
		gbc_lblFiltadoClass.gridy = 4;
		panel.add(lblFiltadoClass, gbc_lblFiltadoClass);
		GridBagConstraints gbc_txtFiltradoClass = new GridBagConstraints();
		gbc_txtFiltradoClass.insets = new Insets(0, 0, 5, 0);
		gbc_txtFiltradoClass.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFiltradoClass.gridx = 0;
		gbc_txtFiltradoClass.gridy = 5;
		panel.add(txtFiltradoClass, gbc_txtFiltradoClass);
		txtFiltradoClass.setColumns(10);
		GridBagConstraints gbc_cbShowBodyText = new GridBagConstraints();
		gbc_cbShowBodyText.insets = new Insets(0, 0, 5, 0);
		gbc_cbShowBodyText.gridx = 0;
		gbc_cbShowBodyText.gridy = 6;
		panel.add(cbShowBodyText, gbc_cbShowBodyText);
		panel.add(chckbxSelect, gbc_chckbxSelect);
		
		JLabel lblInsertCode = new JLabel("Insertar codigo a excluir");
		GridBagConstraints gbc_lblInsertCode = new GridBagConstraints();
		gbc_lblInsertCode.insets = new Insets(0, 0, 5, 0);
		gbc_lblInsertCode.gridx = 0;
		gbc_lblInsertCode.gridy = 8;
		panel.add(lblInsertCode, gbc_lblInsertCode);
		
		taFilterCode_1 = new JTextArea();
		GridBagConstraints gbc_taFilterCode_1 = new GridBagConstraints();
		gbc_taFilterCode_1.insets = new Insets(0, 0, 5, 0);
		gbc_taFilterCode_1.fill = GridBagConstraints.BOTH;
		gbc_taFilterCode_1.gridx = 0;
		gbc_taFilterCode_1.gridy = 9;
		
		taFilterCode_0 = new JTextArea();
		GridBagConstraints gbc_taFiltradocodigo = new GridBagConstraints();
		gbc_taFiltradocodigo.insets = new Insets(0, 0, 5, 0);
		gbc_taFiltradocodigo.fill = GridBagConstraints.BOTH;
		gbc_taFiltradocodigo.gridx = 0;
		gbc_taFiltradocodigo.gridy = 10;
		
		JScrollPane scrlFilterCode_2 = new JScrollPane();
		GridBagConstraints gbc_scrlFilterCode_2 = new GridBagConstraints();
		gbc_scrlFilterCode_2.insets = new Insets(0, 0, 5, 0);
		gbc_scrlFilterCode_2.fill = GridBagConstraints.BOTH;
		gbc_scrlFilterCode_2.gridx = 0;
		gbc_scrlFilterCode_2.gridy = 9;
		panel.add(scrlFilterCode_2, gbc_scrlFilterCode_2);
		
		taFilterCode_2 = new JTextArea();
		scrlFilterCode_2.setViewportView(taFilterCode_2);
		
		JScrollPane scrlFilterCode_3 = new JScrollPane();
		GridBagConstraints gbc_scrlFilterCode_3 = new GridBagConstraints();
		gbc_scrlFilterCode_3.insets = new Insets(0, 0, 5, 0);
		gbc_scrlFilterCode_3.fill = GridBagConstraints.BOTH;
		gbc_scrlFilterCode_3.gridx = 0;
		gbc_scrlFilterCode_3.gridy = 10;
		panel.add(scrlFilterCode_3, gbc_scrlFilterCode_3);
		
		taFilterCode_3 = new JTextArea();
		scrlFilterCode_3.setViewportView(taFilterCode_3);
		
		JScrollPane scrlFilterCode_0 = new JScrollPane();
		GridBagConstraints gbc_scrlFilterCode_0 = new GridBagConstraints();
		gbc_scrlFilterCode_0.insets = new Insets(0, 0, 5, 0);
		gbc_scrlFilterCode_0.fill = GridBagConstraints.BOTH;
		gbc_scrlFilterCode_0.gridx = 0;
		gbc_scrlFilterCode_0.gridy = 11;
		panel.add(scrlFilterCode_0, gbc_scrlFilterCode_0);
		scrlFilterCode_0.setViewportView(taFilterCode_0);
		
		JScrollPane scrlFilterCode_1 = new JScrollPane();
		GridBagConstraints gbc_scrlFilterCode_1 = new GridBagConstraints();
		gbc_scrlFilterCode_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrlFilterCode_1.fill = GridBagConstraints.BOTH;
		gbc_scrlFilterCode_1.gridx = 0;
		gbc_scrlFilterCode_1.gridy = 12;
		panel.add(scrlFilterCode_1, gbc_scrlFilterCode_1);
		scrlFilterCode_1.setViewportView(taFilterCode_1);
		
		JPanel panel_7 = new JPanel();
		GridBagConstraints gbc_panel_7 = new GridBagConstraints();
		gbc_panel_7.fill = GridBagConstraints.BOTH;
		gbc_panel_7.gridx = 0;
		gbc_panel_7.gridy = 13;
		panel.add(panel_7, gbc_panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_9 = new JPanel();
		panel_7.add(panel_9, BorderLayout.NORTH);
		panel_9.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_9.add(chckbxMetas);
		panel_9.add(chckbxTitle);
		panel_9.add(cbContarExcluidos);
		cbContarExcluidos.setVisible(false);
		cbContarExcluidos.setSelected(true);
		
		pnlScript = new JPanel();
		panel_7.add(pnlScript, BorderLayout.CENTER);
		pnlScript.setLayout(new BorderLayout(0, 0));
		
		cbIsScript = new JCheckBox("Se carga el contenido con un script");
		pnlScript.add(cbIsScript, BorderLayout.NORTH);
		
		taScript = new JTextField();
		pnlScript.add(taScript, BorderLayout.CENTER);
		
		JPanel panel_13 = new JPanel();
		panel_13.setPreferredSize(new Dimension(10, 20));
		pnlScript.add(panel_13, BorderLayout.SOUTH);
		
		chckbxTitle.setVisible(false);
		chckbxMetas.setVisible(false);
		
		JLabel lblNewLabel = new JLabel("Inserte la url: ");
		panel_8.add(lblNewLabel);
		
		// Accion tras pulsar el boton de Analizar
		btnAnalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				analizar();
			}
		});	
		
		txtHttp.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    analizar();
                }
            }
        });
		
		JButton btnReanalizar = new JButton("Re-Analizar");
		panel_4.add(btnReanalizar, BorderLayout.SOUTH);
		
		// Accion tras pulsar el boton Re-Analizar
		btnReanalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblImg.setVisible(false);
				taTotales.setBackground(Color.WHITE);
				taLog.setText("");
				// Se vacia el contenido de las tablas
				//modeloTblResult.setRowCount(0);
				modeloTblResultSinContenidoDuplicado.setRowCount(0);
				// Se obtienen los datos del frame y se guardan en el bean
				filterBean.setUrlStr(txtHttp.getText());
				filterBean.setUrlFilter(txtFiltradoUrl.getText());
				filterBean.setUrlNotFilter(txtInsertByFilterUrl.getText());
				filterBean.setContentCodeFilter_0(taFilterCode_0.getText());
				filterBean.setContentCodeFilter_1(taFilterCode_1.getText());
				filterBean.setContentCodeFilter_2(taFilterCode_2.getText());
				filterBean.setContentCodeFilter_3(taFilterCode_3.getText());
				filterBean.setClassFilter(txtFiltradoClass.getText());
				filterBean.setShowBodyText(cbShowBodyText.isSelected());
				filterBean.setPrecio(Float.parseFloat(txtPrecio.getText()));
				filterBean.setCountSelect(chckbxSelect.isSelected());
				filterBean.setCountMeta(chckbxMetas.isSelected());
				filterBean.setCountTitle(chckbxTitle.isSelected());
				filterBean.setAccion(FilterBean.ACCION_RE_ANALISIS);
				filterBean.setCountExcluded(cbContarExcluidos.isSelected());
				// Se ejecuta el extractor
				Extractor extractor = new Extractor(filterBean);
				extractor.execute();
				//extractor.reAnalizar();
				Extractor.CalcularTotales();
			}
		});
		
		
		JPanel pnlCenter = new JPanel();
		contentPane.add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("Analizando");
		pnlCenter.add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		pnlCenter.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		pnlCenter.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		final JScrollPane scrlLog = new JScrollPane();
		scrlLog.setPreferredSize(new Dimension(3, 50));
		panel_2.add(scrlLog, BorderLayout.NORTH);
		taLog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
					//scrlLog.setSize(scrlLog.getWidth(), 150);
					System.out.println("empliamos la pantalla del log");
			}
		});
		taLog.setPreferredSize(new Dimension(0, 10));
		
		taLog.setLineWrap(true);
		taLog.setEditable(false);
		scrlLog.setViewportView(taLog);
		
		JPanel pnlUrlDescartadas = new JPanel();
		pnlUrlDescartadas.setPreferredSize(new Dimension(10, 100));
		panel_2.add(pnlUrlDescartadas, BorderLayout.SOUTH);
		pnlUrlDescartadas.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrlUrlDescartadas = new JScrollPane();
		pnlUrlDescartadas.add(scrlUrlDescartadas, BorderLayout.CENTER);
		
		scrlUrlDescartadas.setViewportView(taUrlDescartadas);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel_2.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("URL Analizadas", null, panel_6, null);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrlTable = new JScrollPane();
		panel_6.add(scrlTable);
		tblResult.setAutoCreateRowSorter(true);
		tblResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					int fila = tblResult.rowAtPoint(e.getPoint());
			        int columna = tblResult.columnAtPoint(e.getPoint());
			        if ((fila > -1) && (columna > -1) && (columna == 0))
			        	//JOptionPane.showMessageDialog(frame, ((UrlBean)Extractor.hmUrlBean.get(modeloTblResult.getValueAt(fila,columna))).getBodyTxt());
			            System.out.println(((UrlBean)Extractor.hmUrlBean.get(modeloTblResult.getValueAt(fila,columna))).getHtmlCode());
			        	System.out.println(((UrlBean)Extractor.hmUrlBean.get(modeloTblResult.getValueAt(fila,columna))).getBodyTxt());
			        	taLog.append(((UrlBean)Extractor.hmUrlBean.get(modeloTblResult.getValueAt(fila,columna))).getBodyTxt());
			        
					//System.out.println("Se mostrará un dialogo con el texto. "+modeloTblResult.getValueAt(fila,columna));
				}
			}
		});
		scrlTable.setViewportView(tblResult);
		
		JPanel pnlTblBtn = new JPanel();
		panel_6.add(pnlTblBtn, BorderLayout.SOUTH);
		pnlTblBtn.setLayout(new BorderLayout(0, 0));
		
		JButton btnGuardaAnalizadas = new JButton("Pasar el comparador");
		
		// Se encarga de comparar y actualizar la tabla de sin comtenido duplicado
		btnGuardaAnalizadas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extractor.comparadorContenidos();
			}
		});
		pnlTblBtn.add(btnGuardaAnalizadas, BorderLayout.CENTER);
		
		JPanel panel_12 = new JPanel();
		pnlTblBtn.add(panel_12, BorderLayout.NORTH);
		panel_12.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlSinContenidoDuplicado = new JPanel();
		tabbedPane.addTab("Sin Duplicados", null, pnlSinContenidoDuplicado, null);
		pnlSinContenidoDuplicado.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrlResultadoSinContenidoDuplicado = new JScrollPane();
		pnlSinContenidoDuplicado.add(scrlResultadoSinContenidoDuplicado, BorderLayout.CENTER);
		tblSinContenidoDuplicado.setAutoCreateRowSorter(true);
		scrlResultadoSinContenidoDuplicado.setViewportView(tblSinContenidoDuplicado);
		
		JPanel panel_11 = new JPanel();
		pnlSinContenidoDuplicado.add(panel_11, BorderLayout.SOUTH);
		panel_11.setLayout(new BorderLayout(0, 0));
		
		JLabel lblDescripcion = new JLabel(" Descripción: ");
		panel_11.add(lblDescripcion, BorderLayout.WEST);
		
		txtDescripcion = new JTextField();
		panel_11.add(txtDescripcion, BorderLayout.CENTER);
		txtDescripcion.setColumns(10);
		
		JButton btnNewButton = new JButton("Guardar");
		panel_11.add(btnNewButton, BorderLayout.SOUTH);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				   Extractor.SaveDataToFile(chooser.getSelectedFile().getAbsolutePath(), txtDescripcion.getText());
				}
			}
		});

		// Se forman las columnas para las tablas
		//modeloTblResult.addColumn("Id");
		modeloTblResult.addColumn("URL");
		modeloTblResult.addColumn("Cuerpo");
		tblResult.getColumn("URL").setPreferredWidth(900);
		modeloTblResultSinContenidoDuplicado.addColumn("Id");
		modeloTblResultSinContenidoDuplicado.addColumn("URL");		
		modeloTblResultSinContenidoDuplicado.addColumn("Cuerpo");
		tblSinContenidoDuplicado.getColumn("URL").setPreferredWidth(900);
	}
	
	private void analizar(){
		lblImg.setVisible(false);
		taLog.setText("");
		// Se vacia el contenido de las tablas
		modeloTblResult.setRowCount(0);
		modeloTblResultSinContenidoDuplicado.setRowCount(0);
		// Se obtienen los datos del frame y se guardan en el bean
		
		filterBean.setUrlStr(txtHttp.getText());
		filterBean.setUrlFilter(txtFiltradoUrl.getText());
		filterBean.setUrlNotFilter(txtInsertByFilterUrl.getText());
		filterBean.setContentCodeFilter_0(taFilterCode_0.getText());
		filterBean.setContentCodeFilter_1(taFilterCode_1.getText());
		filterBean.setContentCodeFilter_2(taFilterCode_2.getText());
		filterBean.setContentCodeFilter_3(taFilterCode_3.getText());
		filterBean.setShowBodyText(cbShowBodyText.isSelected());
		filterBean.setPrecio(Float.parseFloat(txtPrecio.getText()));
		filterBean.setAccion(FilterBean.ACCION_ANALISIS);
		filterBean.setCountExcluded(cbContarExcluidos.isSelected());
		filterBean.setOnlyThisUrl(cbOnlyThisUrl.isSelected());
		filterBean.setTratarComoScript(cbIsScript.isSelected());
		filterBean.setPostParamsScript(taScript.getText());
		extractor = new Extractor(filterBean);
		extractor.execute();
		
		Extractor.CalcularTotales();
	}
	
}
