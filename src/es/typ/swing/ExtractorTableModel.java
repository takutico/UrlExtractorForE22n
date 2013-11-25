package es.typ.swing;

import javax.swing.table.DefaultTableModel;

public class ExtractorTableModel extends DefaultTableModel {

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	
}
