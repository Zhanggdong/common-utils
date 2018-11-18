package com.huasisoft.hae.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import java.lang.reflect.Field;

/**
 * 导出list的数据
 * @param <T>
 */
public class ExportUtil<T> {
	public void exportExcel(Class<T> clz,List<T>list,String[] columns,OutputStream os,String exeName) throws Exception{
			WritableWorkbook workbook = null;
			//获取类的所有属性
			Field[] fields = clz.getDeclaredFields();
			try {
				workbook = Workbook.createWorkbook(os);
				WritableSheet sheet = workbook.createSheet(exeName, 0); // 添加第一个工作表
				initialSheetSetting(sheet);
				for(int i=0;i<columns.length;i++){
					Label label = new Label(i, 0,columns[i],getTitleCellFormat());
					sheet.addCell(label);
				}
				//遍历集合值
				for(int i=0;i<list.size();i++){
					//遍历类的属性
					for(int j=0;j<fields.length;j++){
						Label v1 = null;
						if(j==1||j==3||j==4){
							//左对齐
							 v1 = new Label(j, i + 1, list.get(i).getClass().getMethod("get" + getMethodName(fields[j].getName())).invoke(list.get(i))+"",
									getDataCellFormat(CellType.STRING_FORMULA,"left"));
						}else{
							//居中
							 v1 = new Label(j, i + 1, list.get(i).getClass().getMethod("get" + getMethodName(fields[j].getName())).invoke(list.get(i))+"",
									getDataCellFormat(CellType.STRING_FORMULA,""));
						}
						sheet.addCell(v1);
					}
				}
				workbook.write();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if (workbook != null) {
					try {
						workbook.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	}
	// 把一个字符串的第一个字母大写、效率是最高的
	 private static String getMethodName(String fildeName) throws Exception{
	  byte[] items = fildeName.getBytes();
	  items[0] = (byte) ((char) items[0] - 'a' + 'A');
	  return new String(items);
	 }
	/**
	 * 初始化表格属性
	 * 
	 * @param sheet
	 */
	public void initialSheetSetting(WritableSheet sheet) {
		try {
			// sheet.getSettings().setProtected(true); //设置xls的保护，单元格为只读的
			sheet.getSettings().setDefaultColumnWidth(20); // 设置列的默认宽度
			// sheet.setRowView(2, false);// 行高自动扩展
			sheet.setColumnView(0, 20);// 设置第1列宽度
			sheet.setColumnView(1, 50);// 设置第2列宽度
			sheet.setColumnView(2, 20);// 设置第3列宽度
			sheet.setColumnView(3, 60);// 设置第4列宽度
			sheet.setColumnView(3, 60);// 设置第5列宽度
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到数据表头格式
	 * 
	 * @return
	 */
	public WritableCellFormat getTitleCellFormat() {
		WritableCellFormat wcf = null;
		try {
			// 字体样式
			WritableFont wf = new WritableFont(WritableFont.TIMES, 12,
					WritableFont.NO_BOLD, false);// 最后一个为是否italic
			//wf.setColour(Colour.RED);
			wcf = new WritableCellFormat(wf);
			// 对齐方式
			wcf.setAlignment(Alignment.CENTRE);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			// 边框
			wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			// 背景色
			wcf.setBackground(Colour.GREY_25_PERCENT);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return wcf;
	}
	
	/**
	 * 得到数据格式
	 * 
	 * @return
	 */
	public WritableCellFormat getDataCellFormat(CellType type,String align) {
		WritableCellFormat wcf = null;
		try {
			// 字体样式
			if (type == CellType.NUMBER || type == CellType.NUMBER_FORMULA) {// 数字
				NumberFormat nf = new NumberFormat("#.00");
				wcf = new WritableCellFormat(nf);
			} else if (type == CellType.DATE || type == CellType.DATE_FORMULA) {// 日期
				jxl.write.DateFormat df = new jxl.write.DateFormat(
						"yyyy-MM-dd HH:mm:ss");
				wcf = new jxl.write.WritableCellFormat(df);
			} else {
				WritableFont wf = new WritableFont(WritableFont.TIMES, 10,
						WritableFont.NO_BOLD, false);// 最后一个为是否italic
				wcf = new WritableCellFormat(wf);
			}
			// 对齐方式
			if("left".equals(align)){
				wcf.setAlignment(Alignment.LEFT);
				wcf.setVerticalAlignment(VerticalAlignment.BOTTOM);
			}else{
				wcf.setAlignment(Alignment.CENTRE);
				wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			}
			// 边框
			wcf.setBorder(Border.LEFT, BorderLineStyle.THIN);
			wcf.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
			wcf.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			// 背景色
			wcf.setBackground(Colour.WHITE);

			wcf.setWrap(true);// 自动换行

		} catch (WriteException e) {
			e.printStackTrace();
		}
		return wcf;
	}
}
